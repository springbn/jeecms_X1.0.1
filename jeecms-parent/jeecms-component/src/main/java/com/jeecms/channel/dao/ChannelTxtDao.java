/*   
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */  

package com.jeecms.channel.dao;

import java.util.List;

import com.jeecms.common.base.dao.IBaseDao;
import com.jeecms.content.domain.ChannelTxt;

/**
 * 栏目内容dao接口
 * @author: chenming
 * @date:   2019年6月25日 上午9:36:54
 */
public interface ChannelTxtDao extends IBaseDao<ChannelTxt, Integer> {
	
	List<ChannelTxt> findByChannelId(Integer channelId);
}
