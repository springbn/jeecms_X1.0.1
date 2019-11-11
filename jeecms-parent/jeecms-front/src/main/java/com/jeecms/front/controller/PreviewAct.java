package com.jeecms.front.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeecms.auth.domain.CoreUser;
import com.jeecms.channel.domain.Channel;
import com.jeecms.channel.service.ChannelService;
import com.jeecms.common.constants.WebConstants;
import com.jeecms.common.exception.GlobalException;
import com.jeecms.common.response.ResponseInfo;
import com.jeecms.common.web.util.CookieUtils;
import com.jeecms.common.web.util.ResponseUtils;
import com.jeecms.content.domain.Content;
import com.jeecms.content.service.ContentService;
import com.jeecms.system.domain.CmsSite;
import com.jeecms.system.service.CmsSiteService;
import com.jeecms.util.FrontUtils;
import com.jeecms.util.SystemContextUtils;

@Controller
public class PreviewAct {
	/**
	 * 预览接口
	 * 
	 * @Title: preview
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param device
	 *            设备 pc mobile
	 * @param contentId
	 *            内容ID
	 * @param channelId
	 *            栏目ID
	 * @param siteId
	 *            站点ID
	 * @param model
	 * @return
	 * @throws GlobalException
	 * @throws IOException 
	 */
	@RequestMapping(value = "/preview", method = RequestMethod.GET)
	public String preview(HttpServletRequest request, HttpServletResponse response, String device, String type,
			Integer contentId, Integer channelId, Integer siteId, ModelMap model) throws GlobalException, IOException {
		FrontUtils.frontData(request, model);
		CoreUser  user = SystemContextUtils.getCoreUser();
		/**必须管理员才能预览*/
		if(user==null||!user.getAdmin()){
			ResponseUtils.redirectToLogin(request, response);
		}
		if (StringUtils.isBlank(device) || device.equals(WebConstants.PREVIEW_DEVICE_PC)) {
			SystemContextUtils.setPc(true);
			SystemContextUtils.setMobile(false);
			SystemContextUtils.setTablet(false);
		} else if (StringUtils.isNotBlank(device) && device.equals(WebConstants.PREVIEW_DEVICE_TABLET)) {
			SystemContextUtils.setTablet(true);
			SystemContextUtils.setMobile(false);
			SystemContextUtils.setPc(false);
		} else {
			SystemContextUtils.setMobile(true);
			SystemContextUtils.setPc(false);
			SystemContextUtils.setTablet(false);
		}
		if (StringUtils.isNotBlank(device)) {
			CookieUtils.addCookie(request, response, WebConstants.COOKIE_PREVIEW_DEVICE, device.toString(), 36000, null,
					WebConstants.PREVIEW_URL);
		}
		CmsSite site = SystemContextUtils.getSite(request);
		String previewLoadUrl = site.getUrl();
		if (StringUtils.isNotBlank(type)) {
			if (type.equals(WebConstants.PREVIEW_TYPE_SITE) && siteId != null) {
				site = siteService.findById(siteId);
				previewLoadUrl = site.getUrl();
			} else if (type.equals(WebConstants.PREVIEW_TYPE_CHANNEL) && channelId != null) {
				Channel c = channelService.findById(channelId);
				if (c != null) {
					previewLoadUrl = c.getUrl();
				}
			} else if (type.equals(WebConstants.PREVIEW_TYPE_CONTENT) && contentId != null) {
				Content c = contentService.findById(contentId);
				if (c != null) {
					previewLoadUrl = c.getUrl();
				}
			}
		}
		model.addAttribute("loadUrl", previewLoadUrl);
		String previewTpl = FrontUtils.getSysPagePath(request, "common", "preview");
		return previewTpl;
	}

	@ResponseBody
	@RequestMapping(value = "/preview" , method = RequestMethod.PUT)
	public ResponseInfo previewChangeDevice(HttpServletRequest request, HttpServletResponse response, String device)
			throws GlobalException {
		if (StringUtils.isBlank(device) || device.equals(WebConstants.PREVIEW_DEVICE_PC)) {
			SystemContextUtils.setPc(true);
		} else {
			SystemContextUtils.setMobile(true);
		}
		if (StringUtils.isNotBlank(device)) {
			CookieUtils.addCookie(request, response, WebConstants.COOKIE_PREVIEW_DEVICE, device.toString(), 36000, null,
					WebConstants.PREVIEW_URL);
		}
		return new ResponseInfo();
	}

	@Autowired
	private ChannelService channelService;
	@Autowired
	private ContentService contentService;
	@Autowired
	private CmsSiteService siteService;
}
