package com.jeecms.channel.service;

import java.util.List;

import com.jeecms.channel.domain.ChannelContentTpl;
import com.jeecms.common.base.service.IBaseService;
import com.jeecms.common.exception.GlobalException;

/**
 * 栏目内容目标service接口
 * 
 * @author: chenming
 * @date: 2019年4月18日 上午9:45:49
 */
public interface ChannelContentTplService extends IBaseService<ChannelContentTpl, Integer> {

	/**
	 * 通过栏目ID获取到栏目内容List集合
	 * 
	 * @Title: findByChannelId
	 * @param channelId 栏目ID
	 * @throws GlobalException 全局异常
	 * @return:
	 */
	List<ChannelContentTpl> findByChannelId(Integer channelId) throws GlobalException;

	/**
	 * 通过栏目Id和模型id进行检索
	 * 
	 * @Title: findByChannelIdAndModelId
	 * @param channelId 栏目id
	 * @param modelId   模型id
	 * @return: ChannelContentTpl
	 */
	ChannelContentTpl findByChannelIdAndModelId(Integer channelId, Integer modelId);
	
	List<ChannelContentTpl> save(List<ChannelContentTpl> contentTpls,Integer channelId,Integer siteId) throws GlobalException;
}
