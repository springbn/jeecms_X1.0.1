/*
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */

package com.jeecms.member.controller;

import com.jeecms.auth.domain.CoreUser;
import com.jeecms.auth.service.CoreUserService;
import com.jeecms.common.base.controller.BaseController;
import com.jeecms.common.base.domain.DeleteDto;
import com.jeecms.common.exception.GlobalException;
import com.jeecms.common.exception.SystemExceptionEnum;
import com.jeecms.common.jsonfilter.annotation.SerializeField;
import com.jeecms.common.response.ResponseInfo;
import com.jeecms.content.domain.vo.ContentMobileVo;
import com.jeecms.content.service.ContentFrontService;
import com.jeecms.member.domain.UserCollection;
import com.jeecms.member.service.UserCollectionService;
import com.jeecms.util.SystemContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * 我的收藏
 *
 * @author xiaohui
 * @version 1.0
 * @date 2019-04-24
 */
@RequestMapping("/usercollections")
@RestController
public class UserCollectionController extends BaseController<UserCollection, Integer> {

    @Autowired
    private UserCollectionService service;
    @Autowired
    ContentFrontService contentFrontService;
    @Autowired
    private CoreUserService userService;

    @PostConstruct
    public void init() {
        String[] queryParams = new String[]{};
        super.setQueryParams(queryParams);
    }

    /**
     * 列表分页
     *
     * @param request   {@link HttpServletRequest}
     * @param title     标题
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageable  分页组件
     * @return ResponseInfo
     */
    @GetMapping(value = "/page")
    @SerializeField(clazz = UserCollection.class, includes = {"id", "title", "url",
            "releaseTimeString", "source", "iconUrl", "createTime", "createDate"})
    public ResponseInfo page(HttpServletRequest request, String title, Date startTime, Date endTime,
                             @PageableDefault(sort = "createTime", direction =
                                     Direction.DESC) Pageable pageable) {
        CoreUser user = SystemContextUtils.getUser(request);
        if (user != null) {
            return new ResponseInfo(service.getPage(title, startTime, endTime, user.getUserId(), pageable));
        } else {
            return new ResponseInfo();
        }
    }

    /**
     * 列表分页
     *
     * @param request   {@link HttpServletRequest}
     * @param title     标题
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageable  分页组件
     * @return ResponseInfo
     */
    @GetMapping(value = "/mobile/page")
    @SerializeField(clazz = UserCollection.class, includes = {"id", "title",
            "releaseTimeString", "createTime", "createDate", "contentMobileVo"})
    public ResponseInfo MobilePage(HttpServletRequest request, String title, Date startTime, Date endTime,
                             @PageableDefault(sort = "createTime", direction =
                                     Direction.DESC) Pageable pageable) throws GlobalException {
        CoreUser user = SystemContextUtils.getUser(request);
        if (user != null) {
            Page<UserCollection> page = service.getPage(title, startTime, endTime, user.getUserId(), pageable);
            for (UserCollection userCollection : page) {
                ContentMobileVo contentMobileVo = contentFrontService.initMobileVo(new ContentMobileVo(), userCollection.getContent());
                userCollection.setContentMobileVo(contentMobileVo);
            }
            return new ResponseInfo(page);
        } else {
            return new ResponseInfo();
        }
    }

    /**
     * 添加
     *
     * @param userCollection 收藏对象
     * @param request        HttpServletRequest
     * @param result         BindingResult
     * @return ResponseInfo
     * @throws GlobalException 异常
     */
    @PostMapping
    public ResponseInfo save(@RequestBody @Valid UserCollection userCollection, HttpServletRequest request,
                             BindingResult result) throws GlobalException {
        Integer userId = SystemContextUtils.getUserId(request);
        if (userId != null) {
            Integer contentId = userCollection.getContentId();
            userCollection = new UserCollection();
            userCollection.setUser(userService.findById(userId));
            userCollection.setUserId(userId);
            userCollection.setContent(contentFrontService.findById(contentId));
            userCollection.setContentId(contentId);
            return super.save(userCollection, result);
        } else {
            return new ResponseInfo(SystemExceptionEnum.ACCOUNT_NOT_LOGIN.getCode(),
                    SystemExceptionEnum.ACCOUNT_NOT_LOGIN.getDefaultMessage());
        }

    }

    /**
     * 取消收藏
     *
     * @param id 收藏id
     * @return ResponseInfo
     * @throws GlobalException 异常
     */
    @GetMapping("/cancel")
    public ResponseInfo delete(Integer id, HttpServletRequest request) throws GlobalException {
        validateId(id);
        Integer userId = SystemContextUtils.getUserId(request);
        if (userId == null) {
            return new ResponseInfo(SystemExceptionEnum.ACCOUNT_NOT_LOGIN.getCode(),
                    SystemExceptionEnum.ACCOUNT_NOT_LOGIN.getDefaultMessage());
        }
        return super.physicalDelete(id);
    }

    /**
     * 取消收藏（根据内容）
     *
     * @param id 收藏id
     * @return ResponseInfo
     * @throws GlobalException 异常
     */
    @GetMapping("/recall")
    public ResponseInfo cancel(Integer id, HttpServletRequest request) throws GlobalException {
        validateId(id);
        contentFrontService.findById(id);
        Integer userId = SystemContextUtils.getUserId(request);
        if (userId == null) {
            return new ResponseInfo(SystemExceptionEnum.ACCOUNT_NOT_LOGIN.getCode(),
                    SystemExceptionEnum.ACCOUNT_NOT_LOGIN.getDefaultMessage());
        }
        UserCollection userCollection = service.findByContentIdAndUserId(id, userId);
        if (userCollection != null) {
            return super.physicalDelete(userCollection.getId());
        }
        return new ResponseInfo(false);
    }

    /**
     * 取消收藏
     *
     * @param deleteDto 删除及批量删除dto
     * @param result    BindingResult
     * @return ResponseInfo
     * @throws GlobalException 异常
     */
    @DeleteMapping("/delete")
    public ResponseInfo delete(@RequestBody @Valid DeleteDto deleteDto, HttpServletRequest request,
                               BindingResult result) throws GlobalException {
        Integer userId = SystemContextUtils.getUserId(request);
        if (userId == null) {
			return new ResponseInfo(SystemExceptionEnum.ACCOUNT_NOT_LOGIN.getCode(),
					SystemExceptionEnum.ACCOUNT_NOT_LOGIN.getDefaultMessage());
		}
        return super.physicalDelete(deleteDto, result);
    }

	/**
	 * 一键清空
	 *
	 * @return ResponseInfo
	 * @throws GlobalException 异常
	 */
	@DeleteMapping("/deleteAll")
	public ResponseInfo deleteAll(HttpServletRequest request) throws GlobalException {
		Integer userId = SystemContextUtils.getUserId(request);
		if (userId == null) {
			return new ResponseInfo(SystemExceptionEnum.ACCOUNT_NOT_LOGIN.getCode(),
					SystemExceptionEnum.ACCOUNT_NOT_LOGIN.getDefaultMessage());
		}
		List<UserCollection> list = service.findAllByUserId(userId);
		return super.physicalDeleteInBatch(list);
	}
}
