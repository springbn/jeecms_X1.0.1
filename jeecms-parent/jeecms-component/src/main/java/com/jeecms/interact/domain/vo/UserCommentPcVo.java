package com.jeecms.interact.domain.vo;

import org.springframework.data.domain.Page;

import com.jeecms.interact.domain.UserComment;

public class UserCommentPcVo {

	private Page<UserComment> page;

	private Integer count;

	public UserCommentPcVo(Page<UserComment> page, Integer count) {
		super();
		this.page = page;
		this.count = count;
	}

	public Page<UserComment> getPage() {
		return page;
	}

	public void setPage(Page<UserComment> page) {
		this.page = page;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
