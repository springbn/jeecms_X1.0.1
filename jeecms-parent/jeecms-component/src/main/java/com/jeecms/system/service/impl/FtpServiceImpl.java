/*
 * * @Copyright:  江西金磊科技发展有限公司  All rights reserved.
 * Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
package com.jeecms.system.service.impl;

import com.jeecms.common.base.service.BaseServiceImpl;
import com.jeecms.common.exception.GlobalException;
import com.jeecms.common.response.ResponseInfo;
import com.jeecms.common.security.Digests;
import com.jeecms.resource.domain.UploadFtp;
import com.jeecms.system.dao.FtpDao;
import com.jeecms.system.service.FtpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FTP管理service实现类
 *
 * @author: wulongwei
 * @date: 2019年4月9日 下午2:27:38
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FtpServiceImpl extends BaseServiceImpl<UploadFtp, FtpDao, Integer> implements FtpService {


	@Override
	public ResponseInfo saveFtpInfo(UploadFtp ftp) throws GlobalException {
		byte[] salt = Digests.generateSaltFix();
		ftp.setObfuscationCode(Digests.getSaltStr(salt));
		ftp.setPassword(ftp.getPassword());
		return new ResponseInfo(super.save(ftp));
	}


	@Override
	public ResponseInfo updateFtpInfo(UploadFtp ftp) throws GlobalException {
		//密码为空则不修改
		if (StringUtils.isNoneBlank(ftp.getPassword())) {
			byte[] salt = Digests.generateSaltFix();
			ftp.setObfuscationCode(Digests.getSaltStr(salt));
			ftp.setPassword(ftp.getPassword());
		} else {
			ftp.setPassword(null);
		}
		return new ResponseInfo(super.update(ftp));
	}
}
