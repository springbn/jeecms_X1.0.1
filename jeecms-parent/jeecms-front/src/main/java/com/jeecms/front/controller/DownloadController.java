/**
 * @Copyright: 江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */

package com.jeecms.front.controller;

import com.jeecms.auth.domain.CoreUser;
import com.jeecms.common.base.domain.DeleteDto;
import com.jeecms.common.exception.GlobalException;
import com.jeecms.common.exception.error.ContentErrorCodeEnum;
import com.jeecms.common.exception.error.SysOtherErrorCodeEnum;
import com.jeecms.common.util.Zipper;
import com.jeecms.common.web.springmvc.RealPathResolver;
import com.jeecms.common.web.util.RequestUtils;
import com.jeecms.content.constants.ContentConstant;
import com.jeecms.content.domain.Content;
import com.jeecms.content.domain.ContentAttr;
import com.jeecms.content.service.ContentAttrResService;
import com.jeecms.content.service.ContentFrontService;
import com.jeecms.content.service.ContentService;
import com.jeecms.resource.domain.ResourcesSpaceData;
import com.jeecms.resource.service.ResourcesSpaceDataService;
import com.jeecms.system.domain.SysSecret;
import com.jeecms.system.domain.SysUserSecret;
import com.jeecms.system.service.GlobalConfigService;
import com.jeecms.util.FrontUtils;
import com.jeecms.util.SystemContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 前台下载控制器
 * 
 * @author: ljw
 * @date: 2019年6月14日 上午9:51:27
 */
@RequestMapping(value = "/download")
@Controller
public class DownloadController {

	private static final Logger log = LoggerFactory.getLogger(DownloadController.class);

	@Autowired
	private ContentService contentService;
	@Autowired
	private ResourcesSpaceDataService service;
	@Autowired
	private ContentAttrResService resService;
	@Autowired
	private RealPathResolver realPathResolver;
	@Autowired
	private GlobalConfigService globalConfigService;
	@Autowired
	private ContentFrontService contentFrontService;

	/**
	 * 下载附件文件
	 *
	 * @param dto
	 *            资源id数组
	 * @param response
	 *            HttpServletResponse
	 * @return ResponseInfo
	 */
	@RequestMapping(value = "/o_accessory")
	public String download(@RequestBody DeleteDto dto, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		// 可下载资源
		List<Integer> sourceIds = new ArrayList<Integer>(10);
		Integer[] ids = dto.getIds();
		try {
			// 判断是否开启附件下载
			Boolean flag = globalConfigService.get().getConfigAttr().getOpenAttachmentSecurity();
			if (flag) {
				// 得到当前登录的人
				CoreUser user = SystemContextUtils.getCoreUser();
				for (Integer integer : ids) {
					// 得到资源的密级ID
					List<Integer> secrets = resService.getSecretByRes(integer);
					// 判断当前密级，与用户拥有的密级做比较
					if (!secrets.isEmpty()) {
						SysUserSecret userSecret = user.getUserSecret();
						if (userSecret != null && !userSecret.getSysSecrets().isEmpty()) {
							// 过滤附件密级，得到用户拥有的密级
							List<Integer> secrets2 = userSecret.getSysSecrets().stream()
									.filter(x -> x.getSecretType().equals(SysSecret.ANNEX_SECRET)).map(SysSecret::getId)
									.collect(Collectors.toList());
							if (secrets2.isEmpty()) {
								model.put(ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getCode(),
										ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getDefaultMessage());
								return FrontUtils.systemError(request, response, model);
							}
							// 如果用户拥有的密级包含资源的密级
							if (secrets2.containsAll(secrets)) {
								sourceIds.add(integer);
							}
						} else {
							model.put(ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getCode(),
									ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getDefaultMessage());
							return FrontUtils.systemError(request, response, model);
						}
					}
				}
				if (sourceIds.isEmpty()) {
					model.put(ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getCode(),
							ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getDefaultMessage());
					return FrontUtils.systemError(request, response, model);
				}
			}

		} catch (GlobalException e) {
			model.put(ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getCode(),
					ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getDefaultMessage());
			return FrontUtils.systemError(request, response, model);
		}
		downAttachment(sourceIds, model, request, response);
		return "";
	}

	@RequestMapping(value = "/byContent")
	public String downloadByContentId(Integer contentId, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		// 可下载资源
		List<Integer> sourceIds = new ArrayList<Integer>(10);
		if (contentId != null) {
			Content c = contentService.findById(contentId);
			try {
				// 判断是否开启附件下载
				Boolean flag = globalConfigService.get().getConfigAttr().getOpenAttachmentSecurity();
				if (flag && c != null) {
					// 得到当前登录的人
					List<Integer> resIds = ContentAttr.fetchIds(c.getAttachments());
					sourceIds = getDownloadResIds(resIds);
					if (sourceIds.isEmpty()) {
						model.put(ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getCode(),
								ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getDefaultMessage());
						return FrontUtils.systemError(request, response, model);
					}
				}
			} catch (GlobalException e) {
				model.put(ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getCode(),
						ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getDefaultMessage());
				return FrontUtils.systemError(request, response, model);
			}
		}
		downAttachment(sourceIds, model, request, response);
		try {
			contentFrontService.saveOrUpdateNum(contentId, null, ContentConstant.CONTENT_NUM_TYPE_DOWNLOADS, false);
		} catch (GlobalException e) {
			log.error("统计下载失败");
		}
		return "";
	}

	@RequestMapping(value = "/byField")
	public String downloadByContentIdAndField(Integer contentId, String field, ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		// 可下载资源
		List<Integer> sourceIds = new ArrayList<Integer>(10);
		if (contentId != null) {
			Content c = contentService.findById(contentId);
			try {
				// 判断是否开启附件下载
				Boolean flag = globalConfigService.get().getConfigAttr().getOpenAttachmentSecurity();
				if (flag && c != null) {
					List<ContentAttr> atts= c.getAttachmentsByField(field);
					if(atts!=null){
						List<Integer> resIds = ContentAttr.fetchIds(atts);
						sourceIds = getDownloadResIds(resIds);
					}
				}
				if (sourceIds.isEmpty()) {
					model.put(ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getCode(),
							ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getDefaultMessage());
					return FrontUtils.systemError(request, response, model);
				}
			} catch (GlobalException e) {
				model.put(ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getCode(),
						ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getDefaultMessage());
				return FrontUtils.systemError(request, response, model);
			}
		}
		downAttachment(sourceIds, model, request, response);
		return "";
	}

	@RequestMapping(value = "/byResId")
	public String downloadByResId(Integer resId, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		// 可下载资源
		List<Integer> sourceIds = new ArrayList<Integer>(10);
		if (resId != null) {
			try {
				// 判断是否开启附件下载
				Boolean flag = globalConfigService.get().getConfigAttr().getOpenAttachmentSecurity();
				if (flag) {
					sourceIds = getDownloadResIds(Arrays.asList(resId));
					if (sourceIds.isEmpty()) {
						model.put(ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getCode(),
								ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getDefaultMessage());
						return FrontUtils.systemError(request, response, model);
					}
				}
			} catch (GlobalException e) {
				model.put(ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getCode(),
						ContentErrorCodeEnum.CONTENT_DOWNLOAD_NOT_PERMISSIONS.getDefaultMessage());
				return FrontUtils.systemError(request, response, model);
			}
		}
		downAttachment(sourceIds, model, request, response);
		return "";
	}

	private List<Integer> getDownloadResIds(List<Integer> toDownResIds) throws GlobalException {
		CoreUser user = SystemContextUtils.getCoreUser();
		List<Integer> canDownResIds = new ArrayList<Integer>();
		for (Integer id : toDownResIds) {
			// 得到资源的密级ID
			List<Integer> secrets = resService.getSecretByRes(id);
			// 判断当前密级，与用户拥有的密级做比较
			if (!secrets.isEmpty()) {
				if (user != null) {
					SysUserSecret userSecret = user.getUserSecret();
					if (userSecret != null && !userSecret.getSysSecrets().isEmpty()) {
						// 过滤附件密级，得到用户拥有的密级
						List<Integer> userResSecrets = userSecret.getSysSecrets().stream()
								.filter(x -> x.getSecretType().equals(SysSecret.ANNEX_SECRET)).map(SysSecret::getId)
								.collect(Collectors.toList());
						// 如果用户拥有的密级包含资源的密级
						if (userResSecrets != null && userResSecrets.containsAll(secrets)) {
							canDownResIds.add(id);
						}
					}
				}
			} else {
				/** 附件未设置密级 */
				canDownResIds.add(id);
			}
		}
		return canDownResIds;
	}

	private String downAttachment(List<Integer> ids, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		// 出现下载的对话框
		response.setContentType("application/x-download;charset=UTF-8");
		InputStream input = null;
		OutputStream output = null;
		try {
			if (ids.size() == 1) {
				ResourcesSpaceData spaceData = service.findById(ids.get(0));
				String filename = spaceData.getAlias();
				RequestUtils.setDownloadHeader(response, filename);
				File file = new File(realPathResolver.get(spaceData.getUrl()));
				input = new FileInputStream(file);
				output = response.getOutputStream();
				byte[] buff = new byte[1024];
				int len = 0;
				while ((len = input.read(buff)) > -1) {
					output.write(buff, 0, len);
				}
			} else {
				String filename = "";
				List<Zipper.FileEntry> list = new ArrayList<Zipper.FileEntry>();
				for (Integer id : ids) {
					ResourcesSpaceData spaceData = service.findById(id);
					filename = spaceData.getAlias();
					list.addAll(export(spaceData.getUrl()));
				}
				RequestUtils.setDownloadHeader(response, filename);
				Zipper.zip(response.getOutputStream(), list, "GBK");
			}
		} catch (IOException e) {
			model.put(SysOtherErrorCodeEnum.IO_ERROR.getCode(), SysOtherErrorCodeEnum.IO_ERROR.getDefaultMessage());
			log.error(e.getMessage());
			return FrontUtils.systemError(request, response, model);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
		}
		return "";
	}

	/**
	 * 文库文档下载
	 * 
	 * @param contentId
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/o_doc_download")
	public String downloadLibrary(Integer contentId, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>(1);
		if (contentId == null) {
			model.put("error", "缺少参数");
			return FrontUtils.systemError(request, response, model);
		}
		InputStream input = null;
		OutputStream output = null;
		try {
			Content content = contentService.findById(contentId);
			if (content == null) {
				model.put("error", "文档未找到");
				return FrontUtils.systemError(request, response, model);
			}
			String pdfUrl = content.getContentExt().getDocResource().getUrl();
			if (StringUtils.isBlank(pdfUrl)) {
				model.put("error", "文档未找到");
				return FrontUtils.systemError(request, response, model);
			}
			String filename = pdfUrl.substring(pdfUrl.lastIndexOf("/"));
			// 出现下载的对话框
			response.setContentType("application/x-download;charset=UTF-8");
			RequestUtils.setDownloadHeader(response, filename);
			File file = new File(realPathResolver.get(pdfUrl));
			// 下载后的名称
			input = new FileInputStream(file);
			output = response.getOutputStream();
			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = input.read(buff)) > -1) {
				output.write(buff, 0, len);
			}
		} catch (IOException e) {
			model.put(SysOtherErrorCodeEnum.IO_ERROR.getCode(), SysOtherErrorCodeEnum.IO_ERROR.getDefaultMessage());
			log.error(e.getMessage());
			return FrontUtils.systemError(request, response, model);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
		}
		try {
			contentFrontService.saveOrUpdateNum(contentId, null, ContentConstant.CONTENT_NUM_TYPE_DOWNLOADS, false);
		} catch (GlobalException e) {
			log.error("统计下载失败");
		}
		return null;
	}

	/**
	 * 将文件导入集合
	 * 
	 * @Title: export
	 * @param url
	 *            资源地址
	 * @return
	 */
	private List<Zipper.FileEntry> export(String url) {
		List<Zipper.FileEntry> fileEntrys = new ArrayList<Zipper.FileEntry>();
		File tpl = new File(realPathResolver.get(url));
		fileEntrys.add(new Zipper.FileEntry("", "", tpl));
		return fileEntrys;
	}

}
