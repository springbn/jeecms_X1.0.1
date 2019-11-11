package com.jeecms.front.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.jeecms.common.constants.ServerModeEnum;
import com.jeecms.common.constants.WebConstants;
import com.jeecms.common.exception.GlobalException;
import com.jeecms.common.exception.NotFundExceptionInfo;
import com.jeecms.common.exception.SystemExceptionEnum;
import com.jeecms.common.exception.UnknownExceptionInfo;
import com.jeecms.common.web.util.UrlUtil;
import com.jeecms.util.FrontUtils;

/**
 * 重定义/error异常信息
 * @author: tom
 * @date: 2018年3月26日 下午9:13:26
 * @Copyright: 江西金磊科技发展有限公司 All rights reserved. Notice
 *             仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Controller
public class ExceptionHandlerController {
	static Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);
	public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";

	/**
	 * 映射404 springboot 统一异常 /error
	 * @Title: handlerException404
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param e Exception
	 * @param model ModelMap
	 * @throws GlobalException GlobalException
	 * @throws IOException IOException
	 * @return: String
	 */
	@RequestMapping(value = { WebConstants.ERROR_404 })
	public String handlerException404(HttpServletRequest request, HttpServletResponse response, Exception e,
			ModelMap model) throws GlobalException, IOException {
		GlobalException globalException = getMessage(request, true);
		/** 开发者模式则屏蔽错误信息 */
		if (ServerModeEnum.dev.toString().equals(serverMode) 
				|| ServerModeEnum.beta.toString().equals(serverMode)) {
			model.put("exception", globalException.getExceptionInfo());
		}
		String errorRequestUri = (String) request.getAttribute(ERROR_REQUEST_URI);
		String ctx = request.getContextPath();
		if (StringUtils.isNoneBlank(ctx)) {
			errorRequestUri = errorRequestUri.substring(ctx.length());
		}
		/** 资源不返回数据 */
		if (UrlUtil.isResourceUri(errorRequestUri)) {
			return null;
		}
		/** 非重定向请求则返回404 异常JSON数据 */
		String header = request.getHeader(redirectHeader);
		if (StringUtils.isNoneBlank(header) && WebConstants.FALSE.equals(header)) {
			throw new GlobalException(new NotFundExceptionInfo());
		}
		logger.error(globalException.getExceptionInfo().getDefaultMessage());
		return FrontUtils.pageNotFound(request, response, model);
	}

	/**
	 * 映射500异常
	 * @Title: handlerException500
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param e Exception
	 * @param model ModelMap
	 * @throws GlobalException GlobalException
	 * @return: String
	 */
	@RequestMapping(value = { WebConstants.ERROR_500 })
	public String handlerException500(HttpServletRequest request, HttpServletResponse response, Exception e,
			ModelMap model) throws GlobalException {
		e.printStackTrace();
		GlobalException globalException = getMessage(request, true);
		/** 开发者模式则屏蔽错误信息 */
		if (ServerModeEnum.dev.toString().equals(serverMode) 
				|| ServerModeEnum.beta.toString().equals(serverMode)) {
			model.put("exception", globalException.getExceptionInfo());
		}
		/** 非重定向请求则返回500 异常JSON数据 */
		String header = request.getHeader(redirectHeader);
		if (StringUtils.isNoneBlank(header) && WebConstants.FALSE.equals(header)) {
			throw new GlobalException(SystemExceptionEnum.INTERNAL_SERVER_ERROR);
		}
		logger.error(globalException.getExceptionInfo().getDefaultMessage());
		return FrontUtils.systemError(request, response, model);
	}

	/**
	 * 从errorAttributes 获取异常信息，抽取status和message
	 * @Title: getMessage
	 * @param: @param
	 *             request
	 * @param: @param
	 *             includeStackTrace message是否追加堆栈信息
	 * @param: @return
	 * @return: GlobalException
	 */
	private GlobalException getMessage(HttpServletRequest request, Boolean includeStackTrace) {
		Map<String, Object> errorAttributes = getErrorAttributes(request, includeStackTrace);
		StringBuffer buff = new StringBuffer();
		String path = (String) errorAttributes.get("path");
		String messageFound = (String) errorAttributes.get("message");
		buff.append(messageFound);
		String message = "";
		String trace = "";
		if (!StringUtils.isEmpty(path)) {
			message = String.format("Requested path %s with result %s", path, messageFound);
			buff.append(message);
		}
		if (includeStackTrace) {
			trace = (String) errorAttributes.get("trace");
			if (!StringUtils.isEmpty(trace)) {
				// message += String.format(" and trace %s", trace);
				buff.append(String.format(" and trace %s", trace));
			}
		}
		Integer status = (Integer) errorAttributes.get("status");
		return new GlobalException(new UnknownExceptionInfo(buff.toString(), status.toString()));
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
		WebRequest requestAttributes = new ServletWebRequest(request);
		return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
	}

	@Autowired
	private ErrorAttributes errorAttributes;

	/** application.properties 中配置应用模式 */
	@Value("${spring.profiles.active}")
	private String serverMode;

	@Value("${redirect.header}")
	private String redirectHeader;
}
