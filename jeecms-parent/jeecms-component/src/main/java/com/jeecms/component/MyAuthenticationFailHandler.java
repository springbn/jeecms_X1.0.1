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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.jeecms.auth.dto.RequestLoginUser;
import com.jeecms.common.exception.ExceptionInfo;
import com.jeecms.common.exception.error.UserErrorCodeEnum;
import com.jeecms.common.response.ResponseInfo;
import com.jeecms.common.web.springmvc.MessageResolver;
import com.jeecms.common.web.util.ResponseUtils;

/**
 * 自定义登录失败处理器
 * 
 * @author: tom
 * @date: 2019年7月25日 上午8:53:04
 */
@Component
public class MyAuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException e) throws IOException, ServletException {
                Object loginResult = request.getAttribute(RequestLoginUser.LOGIN_RESULT);
                String msg = MessageResolver.getMessage(UserErrorCodeEnum.ACCOUNT_CREDENTIAL_ERROR.getCode(),
                                UserErrorCodeEnum.ACCOUNT_CREDENTIAL_ERROR.getDefaultMessage());
                String code = UserErrorCodeEnum.ACCOUNT_CREDENTIAL_ERROR.getCode();
                ExceptionInfo exceptionInfo;
                Object resData = new Object();
                if (loginResult instanceof ExceptionInfo) {
                        exceptionInfo = (ExceptionInfo) loginResult;
                        msg = MessageResolver.getMessage(exceptionInfo.getCode(), exceptionInfo.getDefaultMessage());
                        code = exceptionInfo.getCode();
                        resData = exceptionInfo.getData();
                }
                ResponseInfo responseInfo = new ResponseInfo(code, msg, request, request.getRequestURI(), resData);
                ResponseUtils.renderJson(response, JSON.toJSONString(responseInfo));
        }
}
