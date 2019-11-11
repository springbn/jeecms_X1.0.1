package com.jeecms.content.util;

import com.jeecms.common.exception.GlobalException;
import com.jeecms.common.exception.SystemExceptionInfo;
import com.jeecms.common.exception.error.ContentErrorCodeEnum;
import com.jeecms.common.util.MyBeanUtils;
import com.jeecms.content.constants.ContentConstant;
import com.jeecms.content.domain.Content;
import com.jeecms.content.domain.ContentExt;
import com.jeecms.content.domain.ContentTxt;
import com.jeecms.resource.domain.ResourcesSpaceData;
import com.jeecms.system.domain.ContentMark;
import com.jeecms.system.domain.ContentSource;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 内容进行初始化的util
 * 
 * @author: chenming
 * @date: 2019年8月12日 下午3:21:56
 */
public class ContentInitUtils {

	/**
	 * 初始化content的数值数据(使用于新增时)
	 */
	public static Content initContentNum(Content content) {
		content.setSortWeight(0);
		content.setViews(0);
		content.setComments(0);
		content.setUps(0);
		content.setDowns(0);
		content.setDownloads(0);
		return content;
	}

	/**
	 * 初始化content的部分默认字段
	 */
	public static Content initContentDefault(Content content) {
		content.setTop(false);
		content.setSortNum(10);
		content.setEdit(false);
		content.setRecycle(false);
		return content;
	}

	/**
	 * 初始化content发布平台为true
	 */
	public static Content initTrueContentRelease(Content content) {
		content.setReleasePc(true);
		content.setReleaseWap(true);
		content.setReleaseApp(true);
		content.setReleaseMiniprogram(true);
		return content;
	}

	/**
	 * 初始化content发布平台为false
	 */
	public static Content initFalseContentRelease(Content content) {
		content.setReleasePc(false);
		content.setReleaseWap(false);
		content.setReleaseApp(false);
		content.setReleaseMiniprogram(false);
		return content;
	}
	
	/**
	 * 初始化contentExt的数值数据(使用于新增时)
	 */
	public static ContentExt initContentExtCount(ContentExt contentExt) {
		contentExt.setViewsMonth(0);
		contentExt.setCommentsMonth(0);
		contentExt.setDownloadsMonth(0);
		contentExt.setUpsMonth(0);
		contentExt.setDownsMonth(0);
		contentExt.setViewsWeek(0);
		contentExt.setCommentsWeek(0);
		contentExt.setDownloadsWeek(0);
		contentExt.setUpsWeek(0);
		contentExt.setDownsWeek(0);
		contentExt.setViewsDay(0);
		contentExt.setCommentsDay(0);
		contentExt.setDownloadsDay(0);
		contentExt.setUpsDay(0);
		contentExt.setDownsDay(0);
		return contentExt;
	}

	/**
	 * 初始化copy是进行初始化content部分默认字段(使用场景：复制、推送到站群)
	 */
	public static Content clearContentObject(Content content) {
		content.setId(null);
		content.setContentRecords(null);
		content.setContentTypes(null);
		content.setUserComments(null);
		content.setChannel(null);
		content.setContentExt(null);
		content.setUser(null);
		content.setPublishUser(null);
		content.setContentTxts(null);
		content.setContentTags(null);
		content.setModel(null);
		content.setContentAttrs(null);
		content.setContentChannels(null);
		content.setContentVersions(null);
		content.setSecret(null);
		content.setContentCopys(null);
		content.setUpdateTime(null);
		content.setUpdateUser(null);
		content.setContentTxts(null);
		content.setTopStartTime(null);
		content.setTopEndTime(null);
		// 发布管理员id，因为无论是复制还是推送到站群的场景下此处都不可能是发布状态
		content.setPublishUserId(null);
		content.setContentRelations(null);
		return content;
	}

	/**
	 * 初始化copy进行初始化contentExt默认字段(使用场景：复制、推送到站群)
	 */
	public static ContentExt initCopyContentExtDefault(ContentExt contentExt) {
		contentExt.setContentId(null);
		contentExt.setWxMediaId(null);
		contentExt.setWbMediaId(null);
		contentExt.setSueOrg(null);
		contentExt.setSueYear(null);
		contentExt.setContentSource(null);
		contentExt.setReData(null);
		return contentExt;
	}

	/**
	 * copy时初始化contentExt(推送到站群与复制功能皆都使用此功能所以放到功能列表中) 复用的尽量通用
	 */
	public static ContentExt initCopyContentExt(ContentExt contentExt, Integer siteId, ContentMark sueOrg,
			ContentMark sueYear, ContentSource source, ResourcesSpaceData reData) throws GlobalException {
		ContentExt newContentExt = new ContentExt();
		MyBeanUtils.copyProperties(contentExt, newContentExt);
		newContentExt = ContentInitUtils.initCopyContentExtDefault(newContentExt);
		if (newContentExt.getIssueOrg() != null) {
			newContentExt.setSueOrg(sueOrg);
		}
		if (newContentExt.getIssueYear() != null) {
			newContentExt.setSueYear(sueYear);
		}
		newContentExt = ContentInitUtils.initContentExtCount(newContentExt);
		if (newContentExt.getContentSourceId() != null) {
			newContentExt.setContentSource(source);
		}
		if (newContentExt.getPicResId() != null) {
			newContentExt.setReData(reData);
		}
		return newContentExt;
	}

	/**
	 * 初始化浏览设置(与栏目配置和站点配置不相符)
	 */
	public static Short initViewControl(Short viewControl) {
		switch (viewControl) {
		case 1:
			return 1;
		case 2:
			return 2;
		case 3:
			return 2;
		default:
			return 1;
		}
	}

	/**
	 * 校验状态
	 */
	public static void checkStatus(Integer oldType,Integer type, boolean workflow,boolean isUpdate) throws GlobalException{
		if (isUpdate) {
			if (workflow) {
				switch (oldType) {
				case ContentConstant.STATUS_DRAFT:
					ContentInitUtils.workflowThrowException(type);
					break;
				case ContentConstant.STATUS_FIRST_DRAFT:
					ContentInitUtils.workflowThrowException(type);
					break;
				case ContentConstant.STATUS_FLOWABLE:
					ContentInitUtils.throwException();
					break;
				case ContentConstant.STATUS_BACK:
					ContentInitUtils.workflowThrowException(type);
					break;
				case ContentConstant.STATUS_WAIT_PUBLISH:
					ContentInitUtils.workflowThrowException(type);
					break;
				case ContentConstant.STATUS_PUBLISH:
					ContentInitUtils.workflowThrowException(type);
					break;
				case ContentConstant.STATUS_NOSHOWING:
					ContentInitUtils.workflowThrowException(type);
					break;
				case ContentConstant.STATUS_PIGEONHOLE:
					ContentInitUtils.throwException();
					break;
				}
			} else {
				if (ContentConstant.STATUS_DRAFT == oldType || ContentConstant.STATUS_FIRST_DRAFT == oldType || ContentConstant.STATUS_PUBLISH == oldType || ContentConstant.STATUS_NOSHOWING == oldType) {
					ContentInitUtils.throwException(type);
				}
				if (ContentConstant.STATUS_PIGEONHOLE == oldType) {
					ContentInitUtils.throwException();
				}
			}
		} else {
			if (workflow) {
				ContentInitUtils.workflowThrowException(type);
			} else {
				ContentInitUtils.throwException(type);
			}
			
		}
	}
	
	/**
	 * 抛出异常
	 */
	public static void throwException() throws GlobalException {
		throw new GlobalException(
				new SystemExceptionInfo(ContentErrorCodeEnum.CONTENT_STATUS_ERROR.getDefaultMessage(),
						ContentErrorCodeEnum.CONTENT_STATUS_ERROR.getCode()));
	}

	/**
	 * 判断有工作流的场景下如果type传入的不满足场景抛出异常
	 * 场景：存为草稿、存为初稿、提交审核(流转中)
	 */
	public static void workflowThrowException(Integer type) throws GlobalException {
		if (!Arrays.asList(ContentConstant.STATUS_DRAFT,ContentConstant.STATUS_FIRST_DRAFT,ContentConstant.STATUS_FLOWABLE).contains(type)) {
			ContentInitUtils.throwException();
		}
	}
	
	/**
	 * 判断无工作流的场景下如果type传入的不满足场景抛出异常
	 * 场景：存为草稿、存为初稿、发布
	 */
	public static void throwException(Integer type) throws GlobalException {
		if (!Arrays.asList(ContentConstant.STATUS_DRAFT,ContentConstant.STATUS_FIRST_DRAFT,ContentConstant.STATUS_PUBLISH).contains(type)) {
			ContentInitUtils.throwException();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(Object object) {
		Map<String, Object> returnMap = new HashMap<String, Object>(50);
		try {
			returnMap = BeanUtils.describe(object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return returnMap;
	}

	/**
	 * 将map转换成list内容的内容集合
	 */
	public static List<ContentTxt> toListTxt(Map<String, String> txtMap) {
		List<ContentTxt> contentTxts = new ArrayList<ContentTxt>();
		for (String txt : txtMap.keySet()) {
			ContentTxt contentTxt = new ContentTxt();
			contentTxt.setAttrKey(txt);
			contentTxt.setAttrTxt(txtMap.get(txt));
			contentTxts.add(contentTxt);
		}
		return contentTxts;
	}

	/**
	 * 将内容的内容的list集合转换成map
	 */
	public static Map<String, String> toMapTxt(List<ContentTxt> contentTxts) {
		Map<String, String> txtMap = new LinkedHashMap<String, String>();
		if (contentTxts != null) {
			for (ContentTxt contentTxt : contentTxts) {
				txtMap.put(contentTxt.getAttrKey(), contentTxt.getAttrTxt());
			}
		}
		return txtMap;
	}

}
