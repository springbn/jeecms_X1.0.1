package com.jeecms.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecms.common.base.service.BaseCacheServiceImpl;
import com.jeecms.common.exception.GlobalException;
import com.jeecms.system.dao.EmailDao;
import com.jeecms.system.domain.CmsSite;
import com.jeecms.system.domain.CmsSiteConfig;
import com.jeecms.system.domain.Email;
import com.jeecms.system.service.CmsSiteService;
import com.jeecms.system.service.EmailService;

/**
 * email的service实现类
 * 
 * @author: chenming
 * @date: 2019年4月12日 下午6:18:19
 */
@Service
@Transactional(rollbackFor = Exception.class)
@CacheConfig(cacheNames = "Email-")
public class EmailServiceImpl extends BaseCacheServiceImpl<Email, EmailDao, Integer> implements EmailService {

	@Override
	public Email findDefault() throws GlobalException {
		return dao.findByisGloable(true);
	}

	@Override
	public Email findOnly() throws GlobalException {
		if (super.findAll(true).size() > 0) {
			return super.findAll(true).get(0);
		}
		return null;
	}

	@Override
	public Email save(Email email) throws GlobalException {
		Email only = this.findOnly();
		/**
		 * 如果进入说明之前一定不存在数据
		 * 存在两种情况：
		 * 		1. 为全局开启	设置
		 * 		2. 非全局开启	不动
		 */
		if (only != null) {
			super.physicalDelete(only);
		}
		Email bean = super.save(email);
		if (bean.getIsGloable()) {
			this.cmsSiteConfig(bean);
		}
		return bean;
	}

	@Override
	public Email update(Email email) throws GlobalException {
		/**
		 * 如果进入说明之前一定存在数据
		 * 存在两种情况：
		 * 		1. 为全局开启	之前为非全局	设置
		 * 					之前为全局		设置
		 * 		2. 非全局开启	之前为非全局	不动
		 * 					之前为全局		设置
		 */
		Boolean mark = this.findOnly().getIsGloable();
		Email bean = super.update(email);
		if (bean.getIsGloable()) {
			this.cmsSiteConfig(bean);
		} else {
			if (mark) {
				this.cmsSiteConfig(bean);
			}
		}
		return bean;
	}

	private void cmsSiteConfig(Email email) throws GlobalException {
		List<CmsSite> cmsSiteList = cmsService.findAll(true);
		if (email.getIsGloable()) {
			for (CmsSite cmsSite : cmsSiteList) {
				CmsSiteConfig cmsSiteConfig = cmsSite.getConfig();
				cmsSiteConfig.setSMTPService(email.getSmtpService());
				cmsSiteConfig.setSMTPPort(email.getSmtpPort());
				cmsSiteConfig.setSendAccount(email.getEmailName());
				cmsSiteConfig.setEmailPassword(email.getEmailPassword());
				cmsSiteConfig.setSslUse(email.getIsSsl() ? "1" : "0");
				cmsSite.setConfig(cmsSiteConfig);
			}
		} else {
			for (CmsSite cmsSite : cmsSiteList) {
				CmsSiteConfig cmsSiteConfig = cmsSite.getConfig();
				cmsSiteConfig.setSMTPService("");
				cmsSiteConfig.setSMTPPort("");
				cmsSiteConfig.setSendAccount("");
				cmsSiteConfig.setEmailPassword("");
				cmsSiteConfig.setSslUse("");
				cmsSite.setConfig(cmsSiteConfig);
			}
		}
		if (cmsSiteList.size() > 0) {
			cmsService.batchUpdate(cmsSiteList);
		}
	}

	@Autowired
	private CmsSiteService cmsService;
}
