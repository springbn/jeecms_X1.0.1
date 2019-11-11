package com.jeecms.component;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.jeecms.common.exception.SystemExceptionEnum;
import com.jeecms.common.response.ResponseInfo;
import com.jeecms.common.web.springmvc.MessageResolver;
import com.jeecms.common.web.util.RequestUtils;
import com.jeecms.common.web.util.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 无权限处理Handler
 * 
 * @Author tom
 */
@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {
        /**
         * 自定义token head标识符
         */
        @Value("${token.header}")
        private String tokenHeader;
        @Value("${redirect.header}")
        private String redirectHeader;

        /**
         * 未登录或无权限时触发的操作 返回 503 缺少token认证信息
         * 
         * @param httpServletRequest
         *                HttpServletRequest
         * @param httpServletResponse
         *                HttpServletResponse
         * @param e
         *                AuthenticationException
         * @throws IOException
         *                 IOException
         * @throws ServletException
         *                 ServletException
         */
        @Override
        public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                        AuthenticationException e) throws IOException, ServletException {
                // 将 ServletRequest 转换为 HttpServletRequest 才能拿到请求头中的 token
                HttpServletRequest httpRequest = (HttpServletRequest) httpServletRequest;
                HttpServletResponse httpResponse = (HttpServletResponse) httpServletResponse;
                String redirectHeader = httpRequest.getHeader(this.redirectHeader);
                String falseKey = "false";
                if (StringUtils.isNoneBlank(redirectHeader) && falseKey.equals(redirectHeader)) {
                        // 尝试获取请求头的 token
                        String authToken = httpRequest.getHeader(this.tokenHeader);
                        // 返回json形式的错误信息
                        String msg = MessageResolver.getMessage(SystemExceptionEnum.TOKEN_ERROR.getCode(),
                                        SystemExceptionEnum.TOKEN_ERROR.getDefaultMessage());
                        String code = SystemExceptionEnum.TOKEN_ERROR.getCode();
                        if (StringUtils.isBlank(authToken)) {
                                msg = MessageResolver.getMessage(SystemExceptionEnum.REQUIRED_TOKEN.getCode(),
                                                SystemExceptionEnum.REQUIRED_TOKEN.getDefaultMessage());
                                code = SystemExceptionEnum.REQUIRED_TOKEN.getCode();
                        }
                        ResponseInfo responseInfo = new ResponseInfo(code, msg, httpServletRequest,
                                        httpServletRequest.getRequestURI(), null);
                        ResponseUtils.renderJson(httpServletResponse, JSON.toJSONString(responseInfo));
                } else {
                        ResponseUtils.redirectToLogin(httpRequest, httpResponse);
                }
        }

}
