/**   
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */

package com.jeecms.component;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.jeecms.auth.domain.CoreUser;
import com.jeecms.auth.dto.RequestLoginUser;
import com.jeecms.auth.service.CoreUserService;
import com.jeecms.auth.service.LoginService;
import com.jeecms.common.base.domain.RequestLoginTarget;
import com.jeecms.common.exception.GlobalException;
import com.jeecms.common.exception.SystemExceptionEnum;
import com.jeecms.common.exception.error.UserErrorCodeEnum;
import com.jeecms.common.response.ResponseInfo;
import com.jeecms.common.web.springmvc.MessageResolver;
import com.jeecms.common.web.util.ResponseUtils;
import com.jeecms.system.domain.CmsSite;
import com.jeecms.system.service.CmsSiteService;
import com.jeecms.util.SystemContextUtils;

/**
 * 自定义登录成功处理类
 * 
 * @author: tom
 * @date: 2019年7月25日 上午8:49:38
 */
@Component
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	 	static Logger logger = LoggerFactory.getLogger(MyAuthenticationSuccessHandler.class);
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
        		/**Security认证通过后生成token*/
                String msg = MessageResolver.getMessage(SystemExceptionEnum.SUCCESSFUL.getCode(),
                                SystemExceptionEnum.SUCCESSFUL.getDefaultMessage());
                String code = SystemExceptionEnum.SUCCESSFUL.getCode();
                Object loginResult = new Object();
                try {
                        Object identityObj = request.getAttribute(RequestLoginUser.LOGIN_IDENTITY);
                        if (identityObj != null) {
                                CoreUser user = userService.findByUsername((String) identityObj);
                                if (user != null) {
                                        /** 未审核用户 */
                                        if (!user.getChecked()) {
                                                msg = MessageResolver.getMessage(
                                                                UserErrorCodeEnum.ACCOUNT_CREDENTIAL_ERROR.getCode(),
                                                                UserErrorCodeEnum.ACCOUNT_CREDENTIAL_ERROR
                                                                                .getDefaultMessage());
                                                code = UserErrorCodeEnum.ACCOUNT_CREDENTIAL_ERROR.getCode();
                                                loginService.logout(null, request, response);
                                        } else {
                                                loginResult = loginService.login(RequestLoginTarget.member,
                                                                (String) identityObj, null);
                                        }
                                }
                        }
                } catch (GlobalException e) {
                        logger.error(e.getMessage());
                }
                CmsSite site;
                String redirectUrl="";
				try {
					site = siteService.getCurrSite(request, response);
					redirectUrl = site.getMemberRedirectUrl();
				} catch (GlobalException e) {
					logger.error(e.getMessage());
				}
                ResponseInfo responseInfo = new ResponseInfo(code, msg, request, redirectUrl, loginResult);
                ResponseUtils.renderJson(response, JSON.toJSONString(responseInfo));
        }

        @Autowired
        private LoginService loginService;
        @Autowired
        private CoreUserService userService;
        @Autowired
        private CmsSiteService siteService;
}
