/**
** @Copyright:  江西金磊科技发展有限公司  All rights reserved. 
* Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
*/

package com.jeecms.system.dao;

import com.jeecms.common.base.dao.IBaseDao;
import com.jeecms.system.dao.ext.SysAccessRecordDaoExt;
import com.jeecms.system.domain.SysAccessRecord;


/**
 * 访问记录DAO
* @author ljw
* @version 1.0
* @date 2019-06-22
*/
public interface SysAccessRecordDao extends IBaseDao<SysAccessRecord, Integer>,SysAccessRecordDaoExt {

	/**
	 * 查询Cookie个数
	* @Title: countByCookieId 
	* @param cookie cookie值
	* @return
	 */
	Long countByCookieId(String cookie);
	
	/**
	 * 查询Cookie个数
	* @Title: countByLoginUserId 
	* @param userId 用户ID
	* @return
	 */
	Long countByLoginUserId(Integer userId);
}
