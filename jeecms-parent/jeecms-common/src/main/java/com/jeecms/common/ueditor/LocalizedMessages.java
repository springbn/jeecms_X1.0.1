package com.jeecms.common.ueditor;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.jeecms.common.web.springmvc.MessageResolver;

/**
 * Provides access to localized messages (properties).
 * <p>
 * Localized messages are loaded for a particular locale from a HTTP request.
 * The locale is resolved by the current {@link LocaleResolver}
 * instance/singleton. If a locale or a bundle for a locale cannot be found,
 * default messages are used.
 * </p>
 * <p>
 * Note: Loaded messages are cached per locale, any subsequent call of the same
 * locale will be served by the cache instead of another resource bundle
 * retrieval.
 * </p>
 * 
 * @version $Id: LocalizedMessages.java,v 1.1 2011/12/06 01:36:06 administrator
 *          Exp $
 */
/**
 * 国际化
 * @author: tom
 * @date:   2018年12月26日 上午10:48:43     
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class LocalizedMessages {
	/**
	 * Searches for the message with the specified key in this message list.
	 * 
	 * @see Properties#getProperty(String)
	 */
	private static String getMessage(HttpServletRequest request, String key, Object... args) {
		return MessageResolver.getMessage(key, args);
	}

	/**
	 * Returns localized <code>fck.editor.compatibleBrowser.yes</code> property.
	 */
	public static String getCompatibleBrowserYes(HttpServletRequest request) {
		return getMessage(request, "fck.editor.compatibleBrowser.yes"); 
	}

	/**
	 * Returns localized <code>fck.editor.compatibleBrowser.no</code> property.
	 */
	public static String getCompatibleBrowserNo(HttpServletRequest request) {
		return getMessage(request, "fck.editor.compatibleBrowser.no"); 
	}

	/**
	 * Returns localized <code>fck.connector.fileUpload.enabled</code> property.
	 */
	public static String getFileUploadEnabled(HttpServletRequest request) {
		return getMessage(request, "fck.connector.fileUpload.enabled"); 
	}

	/**
	 * Returns localized <code>fck.connector.fileUpload.disabled</code>
	 * property.
	 */
	public static String getFileUploadDisabled(HttpServletRequest request) {
		return getMessage(request, "fck.connector.fileUpload.disabled"); 
	}

	/**
	 * Returns localized <code>fck.connector.file_renamed_warning</code>
	 * property.
	 * 
	 * @param newFileName
	 *            the new filename of the warning
	 * @return localized message with new filename
	 */
	public static String getFileRenamedWarning(HttpServletRequest request, String newFileName) {
		return getMessage(request, "fck.connector.fileUpload.file_renamed_warning", newFileName); 
	}

	/**
	 * Returns localized
	 * <code>fck.connector.fileUpload.invalid_file_type_specified</code>
	 * property.
	 */
	public static String getInvalidFileTypeSpecified(HttpServletRequest request) {
		return getMessage(request, "fck.connector.fileUpload.invalid_file_type_specified"); 
	}

	/**
	 * Returns localized <code>fck.connector.fileUpload.write_error</code>
	 * property.
	 */
	public static String getFileUploadWriteError(HttpServletRequest request) {
		return getMessage(request, "fck.connector.fileUpload.write_error"); 
	}

	/**
	 * Returns localized <code>fck.connector.getResources.enabled</code>
	 * property.
	 */
	public static String getGetResourcesEnabled(HttpServletRequest request) {
		return getMessage(request, "fck.connector.getResources.enabled"); 
	}

	/**
	 * Returns localized <code>fck.connector.getResources.disabled</code>
	 * property.
	 */
	public static String getGetResourcesDisabled(HttpServletRequest request) {
		return getMessage(request, "fck.connector.getResources.disabled"); 
	}

	/**
	 * Returns localized <code>fck.connector.getResources.read_error</code>
	 * property.
	 */
	public static String getGetResourcesReadError(HttpServletRequest request) {
		return getMessage(request, "fck.connector.getResources.read_error"); 
	}

	/**
	 * Returns localized <code>fck.connector.createFolder.enabled</code>
	 * property.
	 */
	public static String getCreateFolderEnabled(HttpServletRequest request) {
		return getMessage(request, "fck.connector.createFolder.enabled"); 
	}

	/**
	 * Returns localized <code>fck.connector.createFolder.disabled</code>
	 * property.
	 */
	public static String getCreateFolderDisabled(HttpServletRequest request) {
		return getMessage(request, "fck.connector.createFolder.disabled"); 
	}

	/**
	 * Returns localized <code>fck.connector.invalid_command_specified</code>
	 * property.
	 */
	public static String getInvalidCommandSpecified(HttpServletRequest request) {
		return getMessage(request, "fck.connector.invalid_command_specified"); 
	}

	/**
	 * Returns localized
	 * <code>fck.connector.createFolder.folder_already_exists_error</code>
	 * property.
	 */
	public static String getFolderAlreadyExistsError(HttpServletRequest request) {
		return getMessage(request, "fck.connector.createFolder.folder_already_exists_error"); 
	}

	/**
	 * Returns localized
	 * <code>fck.connector.createFolder.invalid_new_folder_name_specified</code>
	 * property.
	 */
	public static String getInvalidNewFolderNameSpecified(HttpServletRequest request) {
		return getMessage(request, "fck.connector.createFolder.invalid_new_folder_name_specified"); 
	}

	/**
	 * Returns localized <code>fck.connector.createFolder.write_error</code>
	 * property.
	 */
	public static String getCreateFolderWriteError(HttpServletRequest request) {
		return getMessage(request, "fck.connector.createFolder.write_error"); 
	}

	/**
	 * Returns localized
	 * <code>fck.connector.invalid_resource_type_specified</code> property.
	 */
	public static String getInvalidResouceTypeSpecified(HttpServletRequest request) {
		return getMessage(request, "fck.connector.invalid_resource_type_specified"); 
	}

	/**
	 * Returns localized
	 * <code>fck.connector.invalid_current_folder_specified</code> property.
	 */
	public static String getInvalidCurrentFolderSpecified(HttpServletRequest request) {
		return getMessage(request, "fck.connector.invalid_current_folder_specified"); 
	}

	public static String getInvalidFileSuffixSpecified(HttpServletRequest request) {
		return getMessage(request, "fck.connector.invalid_file_suffix_specified"); 
	}

	public static String getInvalidFileToLargeSpecified(HttpServletRequest request, String filename, Integer max) {
		return getMessage(request, "fck.connector.invalid_file_toolarge_specified", filename, max); 
	}

	public static String getInvalidUploadDailyLimitSpecified(HttpServletRequest request, String lavesize) {
		return getMessage(request, "fck.connector.invalid_file_dailylimit_specified", lavesize); 
	}

	public static String getInvalidUploadMultipartSpecified(HttpServletRequest request) {
		return getMessage(request, "fck.connector.invalid_multipart_specified"); 
	}

	public static String getInvalidUploadInputStreamSpecified(HttpServletRequest request) {
		return getMessage(request, "fck.connector.invalid_inputstream_specified"); 
	}

	public static String getRemoteImageSuccessSpecified(HttpServletRequest request) {
		return getMessage(request, "fck.connector.getremoteimage_success"); 
	}

}
