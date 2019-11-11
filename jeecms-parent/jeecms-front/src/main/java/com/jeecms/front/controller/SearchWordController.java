/*
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */

package com.jeecms.front.controller;

import com.jeecms.common.jsonfilter.annotation.SerializeField;
import com.jeecms.common.page.Paginable;
import com.jeecms.common.page.PaginableRequest;
import com.jeecms.common.response.ResponseInfo;
import com.jeecms.system.domain.SysSearchWord;
import com.jeecms.system.service.SysSearchWordService;
import com.jeecms.util.SystemContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索词控制层
 *
 * @author xiaohui
 * @version 1.0
 * @date 2019/7/16 17:38
 */

@RestController
@RequestMapping("/searchWord")
public class SearchWordController {

	@Autowired
	private SysSearchWordService service;

	/**
	 * 搜索词列表
	 *
	 * @param count   搜索词数量
	 * @param request {@link HttpServletRequest}
	 * @return 搜索词列表
	 */
	@RequestMapping("/list")
	@SerializeField(clazz = SysSearchWord.class, includes = {"word"})
	public ResponseInfo list(Integer count, HttpServletRequest request) {
		Integer siteId = SystemContextUtils.getSiteId(request);
		Paginable paginable = new PaginableRequest(0, count);
		Map<String, String[]> params = new HashMap<String, String[]>(1);
		params.put("EQ_siteId_Integer", new String[]{siteId.toString()});
		List<SysSearchWord> sysSearchWords = service.getList(params, paginable, false);
		return new ResponseInfo(sysSearchWords);
	}
}
