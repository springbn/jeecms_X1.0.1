/**
* @Copyright:  江西金磊科技发展有限公司  All rights reserved. 
* Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */

package com.jeecms.content.dao;

import java.util.List;

import com.jeecms.common.base.dao.IBaseDao;
import com.jeecms.content.domain.ContentTxt;


/**
* @author ljw
* @version 1.0
* @date 2019-05-15
*/
public interface ContentTxtDao extends IBaseDao<ContentTxt, Integer> {
	
	/**
	 * 通过对象id查询list集合
	 * @Title: findByContentId  
	 * @param contentId	对象id
	 * @return: List
	 */
	List<ContentTxt> findByContentId(Integer contentId);
}
