package com.jeecms.common.exception;

/**
 * 账户无权限信息类，用于支持账户无权限的处理。
 * @author: tom
 * @date:   2019年3月8日 下午4:27:33   
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class AccountAuthorityExceptionInfo implements ExceptionInfo {

	/**账户无权限的代码。 */
	private String code = SystemExceptionEnum.HAS_NOT_PERMISSION.getCode();

	/**账户无权限对应的默认提示信息。 */
	private String defaultMessage = SystemExceptionEnum.HAS_NOT_PERMISSION.getDefaultMessage();

	/**账户无权限对应的原始提示信息。 */
	private String originalMessage= SystemExceptionEnum.HAS_NOT_PERMISSION.getDefaultMessage();

	/** 当前请求的URL。 */
	private String requestUrl;

	/** 默认的转向（重定向）的URL，默认为空。 */
	private String defaultRedirectUrl = "";

	/**账户无权限对应的响应数据。 */
	private Object data = new Object();

	/**
	 * Description: 通过非法参数异常的默认提示信息、响应数据构建一个非法参数异常信息对象。
	 *
	 * @param defaultMessage
	 *           账户无权限的默认提示信息。
	 * 
	 * @param data
	 *           账户无权限的响应数据。
	 */
	public AccountAuthorityExceptionInfo(String defaultMessage, Object data) {
		this.defaultMessage = defaultMessage;
		this.data = data;
	}
	
	public AccountAuthorityExceptionInfo() {
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDefaultMessage() {
		return defaultMessage;
	}

	@Override
	public String getOriginalMessage() {
		return originalMessage;
	}

	@Override
	public void setOriginalMessage(String originalMessage) {
		this.originalMessage = originalMessage;
	}

	@Override
	public String getRequestUrl() {
		return requestUrl;
	}

	@Override
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	@Override
	public String getDefaultRedirectUrl() {
		return defaultRedirectUrl;
	}

	@Override
	public void setDefaultRedirectUrl(String defaultRedirectUrl) {
		this.defaultRedirectUrl = defaultRedirectUrl;
	}

	@Override
	public Object getData() {
		return data;
	}

	@Override
	public void setData(Object data) {
		this.data = data;
	}

}
