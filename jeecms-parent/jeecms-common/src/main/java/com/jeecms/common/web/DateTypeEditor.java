package com.jeecms.common.web;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.util.StringUtils;

/**
 * 日期编辑器 根据日期字符串长度判断是长日期还是短日期。只支持yyyy-MM-dd，yyyy-MM-dd HH:mm:ss两种格式。
 * 扩展支持yyyy,yyyy-MM日期格式
 * 
 * @author: tom
 * @date: 2018年12月27日 上午9:28:18
 * @Copyright: 江西金磊科技发展有限公司 All rights reserved.Notice
 *             仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class DateTypeEditor extends PropertyEditorSupport {

	public static final String DF_LONG = "yyyy-MM-dd HH:mm:ss";
	public static final String DF_SHORT = "yyyy-MM-dd";
	public static final String DF_YEAR = "yyyy";
	public static final String DF_MONTH = "yyyy-MM";

	/**
	 * 获取FastDateFormat实例
	 * 
	 * @Title: getFastDateFormatInstance
	 * @param pattern
	 *            时间字符串
	 * @return FastDateFormat实例
	 */
	public static FastDateFormat getFastDateFormatInstance(String pattern) {
		return FastDateFormat.getInstance(pattern);
	}

	/**
	 * 短类型日期长度
	 */
	public static final int SHORT_DATE = 10;

	public static final int YEAR_DATE = 4;

	public static final int MONTH_DATE = 7;

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		text = text.trim();
		if (!StringUtils.hasText(text)) {
			setValue(null);
			return;
		}
		try {
			if (text.length() <= YEAR_DATE) {
				setValue(getFastDateFormatInstance(DF_YEAR).parse(text));
			} else if (text.length() <= MONTH_DATE) {
				setValue(getFastDateFormatInstance(DF_MONTH).parse(text));
			} else if (text.length() <= SHORT_DATE) {
				setValue(getFastDateFormatInstance(DF_SHORT).parse(text));
			} else {
				setValue(getFastDateFormatInstance(DF_LONG).parse(text));
			}
		} catch (ParseException ex) {
			IllegalArgumentException iae = new IllegalArgumentException(
					"Could not parse date: " + ex.getMessage());
			iae.initCause(ex);
			throw iae;
		}
	}

	/**
	 * Format the Date as String, using the specified DateFormat.
	 */
	@Override
	public String getAsText() {
		Date value = (Date) getValue();
		return (value != null ? getFastDateFormatInstance(DF_LONG).format(value) : "");
	}
}
