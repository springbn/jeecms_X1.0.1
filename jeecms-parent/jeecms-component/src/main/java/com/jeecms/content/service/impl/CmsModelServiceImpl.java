/*
 * * @Copyright:  江西金磊科技发展有限公司  All rights reserved. 
 * Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
package com.jeecms.content.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeecms.common.base.domain.DragSortDto;
import com.jeecms.common.base.service.BaseServiceImpl;
import com.jeecms.common.exception.GlobalException;
import com.jeecms.common.exception.SystemExceptionInfo;
import com.jeecms.common.exception.error.SettingErrorCodeEnum;
import com.jeecms.common.response.ResponseInfo;
import com.jeecms.common.web.util.RequestUtils;
import com.jeecms.content.constants.CmsModelConstant;
import com.jeecms.content.dao.CmsModelDao;
import com.jeecms.content.domain.CmsModel;
import com.jeecms.content.domain.CmsModelItem;
import com.jeecms.content.domain.dto.CmsModelDto;
import com.jeecms.content.service.CmsModelService;
import com.jeecms.util.SystemContextUtils;

/**
 * 模型service实现
 * 
 * @author: wulongwei
 * @version 1.0
 * @date: 2019年4月19日 上午9:08:30
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CmsModelServiceImpl extends BaseServiceImpl<CmsModel, CmsModelDao, Integer> implements CmsModelService {
	
	/**定义提供客户端需要的模型字段数据过滤模式
	 * ALL ：原始输出，不进行过滤
	 * SHOW_CHANNEL_AND_CONTENT：栏目及内容模型的字段中过滤掉有些特殊字段需要过滤，具体为：
	 * SHOW_MEMBER_REGISTOR : 会员模型的字段中过滤设置 “应用到会员注册” 字段*/
	public enum FilterModel{ALL,SHOW_CHANNEL_AND_CONTENT,SHOW_MEMBER_REGISTOR}
	
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<CmsModel> getList(boolean containDisabled, Integer siteId) {
        return dao.findByIsEnableAndSiteIdAndHasDeleted(containDisabled,
                siteId, false);
    }

    @Override
    public CmsModel getDefModel(Integer siteId) {
        List<CmsModel> models = getList(false, siteId);
        if (models != null && models.size() > 0) {
            return models.get(0);
        }
        return null;
    }
    
    @Override
	public void updatePriority(DragSortDto sortDto) throws GlobalException{
    	CmsModel  fromModel =  this.findById(sortDto.getFromId());
    	CmsModel toModel = this.findById(sortDto.getToId());
    	/**移动目标设置成目标位置排序，排序权重根据原始拖动元素相对于拖动目标元素位置的前后改变位置
    	*在前拖动元素位置排序权重-1
    	*在后拖动元素位置排序权重+1
    	*/
    	if(fromModel.getSortNum() >=  toModel.getSortNum() && fromModel.getSortWeight() > toModel.getSortWeight()
    			&& fromModel.getCreateTime().compareTo(toModel.getCreateTime()) > 0){
        	fromModel.setSortWeight(toModel.getSortWeight()-1);
    	}else {
        	fromModel.setSortWeight(toModel.getSortWeight()+1);
    	}
    	fromModel.setSortNum(toModel.getSortNum());
    	super.update(fromModel);
	}
    

    @Override
    public ResponseInfo getModelPage(Short tplType, Short isGlobal, Boolean isDisable,
            String modelName, Integer siteId, Pageable pageable)
            throws GlobalException {
        return new ResponseInfo(dao.getPage(tplType, isGlobal, isDisable, modelName,
                siteId, pageable));
    }

    @Override
    public ResponseInfo saveThisSiteModel(CmsModel model)
            throws GlobalException {
    	//本站模型不允许添加会员模型
    	if(CmsModel.MEMBER_TYPE.equals(model.getTplType())) {
			 throw new GlobalException(new SystemExceptionInfo(
	                    SettingErrorCodeEnum.LOCAL_SITE_MODEL_IS_NOT_ALLOW_CREATE_MEMBER_MODEL.getDefaultMessage(),
	                    SettingErrorCodeEnum.LOCAL_SITE_MODEL_IS_NOT_ALLOW_CREATE_MEMBER_MODEL.getCode()));
    	}
        model.setIsGlobal(CmsModel.THIS_SITE_MODEL);
        this.checkModelName(model);
        CmsModel maxModel = super.dao.getMaxModelType(model.getTplType());
        Integer sortNum = 0 , sortWeight = 0;
        if(maxModel != null) {
        	sortNum = maxModel.getSortNum()+1;
        	sortWeight = maxModel.getSortWeight()+1;
        }
        model.setSortNum(sortNum);
        model.setSortWeight(sortWeight);
        super.save(model);
        return new ResponseInfo();
    }

    @Override
    public ResponseInfo saveWholeSiteModel(CmsModel model)
            throws GlobalException {
    	//全站模型仅允许添加一个会员模型
    	if(CmsModel.MEMBER_TYPE.equals(model.getTplType())) {
    		List<CmsModel> models = dao.getList(CmsModel.MEMBER_TYPE, true, null);
    		if(!CollectionUtils.isEmpty(models)) {
    			 throw new GlobalException(new SystemExceptionInfo(
 	                    SettingErrorCodeEnum.MEMBER_MODEL_IS_EXIST.getDefaultMessage(),
 	                    SettingErrorCodeEnum.MEMBER_MODEL_IS_EXIST.getCode()));
    		}
    		//添加会员模型默认为启用状态
    		model.setIsEnable(true);
    	}
    	
        model.setIsGlobal(CmsModel.WHOLE_SITE_MODEL);
        this.checkModelName(model);
        CmsModel maxModel = super.dao.getMaxModelType(model.getTplType());
        Integer sortNum = 0 , sortWeight = 0;
        if(maxModel != null) {
        	sortNum = maxModel.getSortNum()+1;
        	sortWeight = maxModel.getSortWeight()+1;
        }
        model.setSortNum(sortNum);
        model.setSortWeight(sortWeight);
        super.save(model);
        return new ResponseInfo();
    }

    @Override
    public ResponseInfo updateModel(CmsModel model) throws GlobalException {
        CmsModel modelInfo = super.get(model.getId());
        if(CmsModel.MEMBER_TYPE.equals(modelInfo.getTplType())) {
        	 throw new GlobalException(new SystemExceptionInfo(
                     SettingErrorCodeEnum.MEMBER_IS_ALLOW_UPDATE.getDefaultMessage(),
                     SettingErrorCodeEnum.MEMBER_IS_ALLOW_UPDATE.getCode()));
        }
        model.setTplType(modelInfo.getTplType());
        super.update(model);
        return new ResponseInfo();
    }

    @Override
    public ResponseInfo isEnable(CmsModelDto modelDto) throws GlobalException {
        CmsModel model = super.get(modelDto.getId());
        model.setIsEnable(modelDto.getIsEnable());
        super.update(model);
        return new ResponseInfo();
    }

    @Override
    public ResponseInfo checkModelName(Integer id ,Short tplType, String modelName,Integer siteId,Short isGlobal)
            throws GlobalException {
        CmsModel modelInfo = dao.findByModelNameAndTplTypeAndHasDeleted(
                modelName, tplType, false);
        if(modelInfo != null) {
        	if(isGlobal.equals(CmsModel.THIS_SITE_MODEL) ) {
        		//本站模型中,
        		//新增下相同站点内不允许重复；不同站点内可以重复; 
        		//编辑情况下,和自身相同可用，和自身不同且为本站其他模型不可用，和自身不同且为其他站点模型可用
        		if(id != null ) {
        			if(id.equals(modelInfo.getId())) {
        				return new ResponseInfo(Boolean.TRUE);
        			}else {
        				if(modelInfo.getSite() !=null && siteId.equals(modelInfo.getSiteId()))  {
        					return new ResponseInfo(Boolean.FALSE);
        				}else {
        					return new ResponseInfo(Boolean.TRUE);
        				}
        			}
        		}else {
    				if(modelInfo.getSite() !=null && siteId.equals(modelInfo.getSiteId())) {
    					return new ResponseInfo(Boolean.FALSE);
        			}else{
        				return new ResponseInfo(Boolean.TRUE);
        			}
        		}
        	}else {
        		//全站模型中，新增下校验全局不允许重复，编辑情况下相同依然可用
        		if(id != null &&  id.equals(modelInfo.getId())) {
        			return new ResponseInfo(Boolean.TRUE);
        		}else {
        			return new ResponseInfo(Boolean.FALSE);
        		}
        	}
		}
		return new ResponseInfo(Boolean.TRUE);
    }
    
    /**
     * 校验modelName，同一类型下的modelName不能相同
     * 
     * @Title: checkModelName
     * @param model
     * @throws GlobalException
     * @return: void
     */
    private void checkModelName(CmsModel model) throws GlobalException {
        CmsModel modelInfo = dao.findByModelNameAndTplTypeAndHasDeleted( model.getModelName(), model.getTplType(), false);
        if (modelInfo != null) {
            throw new GlobalException(new SystemExceptionInfo(
                    SettingErrorCodeEnum.MODEL_NAME_ALREADY_EXIST.getDefaultMessage(),
                    SettingErrorCodeEnum.MODEL_NAME_ALREADY_EXIST.getCode()));
        }
    }
    
	@Override
	public CmsModel getInfo(Integer id) throws GlobalException {
		return getCmsModelByCondition(id,FilterModel.ALL);
		
	}
	
	@Override
	public CmsModel getChannelOrContentModel(Integer id) throws GlobalException {
		return getCmsModelByCondition(id,FilterModel.SHOW_CHANNEL_AND_CONTENT);
	}
	
	@Override
	public CmsModel getFrontMemberModel() throws GlobalException {
		return getCmsModelByCondition(null,FilterModel.SHOW_MEMBER_REGISTOR);
	}
	
	/**
	 *  通过id获取及过滤模式获取需要的模型及模型字段信息
	 *  id 为null时，默认获取会员模型
	 *  过滤模式见 {@link FilterModel}}
	 * @param id   
	 * @param filterModel
	 * @return
	 * @throws GlobalException
	 */
	private CmsModel getCmsModelByCondition(Integer id, FilterModel filterModel) throws GlobalException {
		CmsModel model = null;
		if(null != id ) {
			model = super.findById(id);
		}else {
			List<CmsModel> models = dao.getList(CmsModel.MEMBER_TYPE, true, null);
			if( CollectionUtils.isEmpty(models)) {
				return null;
			}
			model = models.get(0);
		}
		JSONObject enableJson = new JSONObject();
		List<String> groupTypes = new ArrayList<String>(0) ;
		//根据不同模型类型，初始化默认字段分组数据
		if(CmsModel.CHANNEL_TYPE.equals(model.getTplType())) {
			groupTypes =  new ArrayList<String>(Arrays.asList(CmsModelItem.CHANNEL_GROUP_TYPE));
		}else if(CmsModel.CONTENT_TYPE.equals(model.getTplType())) {
			groupTypes =  new ArrayList<String>(Arrays.asList(CmsModelItem.CONTENT_GROUP_TYPE));
		}else if(CmsModel.MEMBER_TYPE.equals(model.getTplType())) {
			groupTypes =  new ArrayList<String>(Arrays.asList(CmsModelItem.MEMBER_GROUP_TYPE));
		}
		if( !CollectionUtils.isEmpty(model.getItems())) {
			Map<String,	List<CmsModelItem>>  groupMaps = model.getItems().stream().collect(Collectors.groupingBy(CmsModelItem::getGroupType));
			for (Map.Entry<String, 	List<CmsModelItem>> item : groupMaps.entrySet()) {
				String key = item.getKey();
				//删除掉已存在字段掉分组
				groupTypes.remove(key);
				List<CmsModelItem>  list  = item.getValue();
				JSONArray fieldJsons = new JSONArray();
				//判断该分组下是否含有字段集，为空跳过，赋值空数组
				if( CollectionUtils.isEmpty(list)) {
					enableJson.put(key, fieldJsons);
					continue;
				}
				//按照sortnum排序
				list = list.stream().sorted(Comparator.comparing(CmsModelItem::getSortNum)).collect(Collectors.toList());
				for (CmsModelItem field : list) {
					JSONObject jsonObj = JSON.parseObject(field.getContent());
					if(FilterModel.SHOW_CHANNEL_AND_CONTENT.equals(filterModel)) {
						//如果系统设置尚未开启内容密级则过滤内容密级
						if(CmsModelConstant.FIELD_SYS_CONTENT_POST_CONTENT.equals(field.getField()) && !SystemContextUtils.getGlobalConfig(RequestUtils.getHttpServletRequest()).getConfigAttr().getOpenContentIssue()) {
							continue;
						}
						//如果系统设置尚未开启内容密级则过滤内容密级
						if(CmsModelConstant.FIELD_SYS_CONTENT_SECRET.equals(field.getField()) && !SystemContextUtils.getGlobalConfig(RequestUtils.getHttpServletRequest()).getConfigAttr().getOpenContentSecurity()) {
							continue;
						}
					}else if(FilterModel.SHOW_MEMBER_REGISTOR.equals(filterModel)) {
						//显示会员模型 ,isRegister 为空或为false时，默认为不应用到前台
						JSONObject valueObje = jsonObj.getJSONObject(CmsModelItem.VALUE) ;
						if( valueObje == null || (valueObje != null
								&& valueObje.getBoolean(CmsModelItem.IS_REGISTER) != null
								&& ! valueObje.getBoolean(CmsModelItem.IS_REGISTER))) continue;
					}
					fieldJsons.add(jsonObj);
				}
				enableJson.put(key, fieldJsons);
			}
			//由于保存字段时，没有字段掉分组会过滤不存入数据db，应前端要求，没有字段的分组也要返回key,并且值为空数组
			for (String str : groupTypes) {
				enableJson.put(str, new JSONArray());
			}
		}
		model.setEnableJson(enableJson);
		return model;
	}
	
	@Override
	public List<CmsModel> getModelList(Short tplType, Boolean isEnable, Integer siteId) throws GlobalException {
		return dao.getList(tplType, isEnable, siteId);
	}
	
	@Override
	public List<CmsModel> findList(Short tplType, Integer siteId) throws GlobalException {
		
		return dao.getList(tplType, true, siteId);
	}

}
