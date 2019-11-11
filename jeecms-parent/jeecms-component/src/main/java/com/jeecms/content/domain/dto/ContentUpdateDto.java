package com.jeecms.content.domain.dto;

import javax.validation.constraints.NotNull;

import com.jeecms.auth.domain.CoreUser;

/**
 * 修改内容Dto
 * @author: chenming
 * @date:   2019年5月22日 下午5:06:04
 */
public class ContentUpdateDto extends ContentSaveDto {
	/** 内容Id*/
	private Integer id;
	
	private CoreUser user;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public CoreUser getUser() {
		return user;
	}

	public void setUser(CoreUser user) {
		this.user = user;
	}
}
