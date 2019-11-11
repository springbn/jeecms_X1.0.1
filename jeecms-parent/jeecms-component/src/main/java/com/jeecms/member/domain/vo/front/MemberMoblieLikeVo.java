/**   
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */

package com.jeecms.member.domain.vo.front;

import com.jeecms.content.domain.vo.ContentMobileVo;

/**
 * 我的点赞VO
 * 
 * @author: ljw
 * @date: 2019年7月26日 上午10:38:46
 */
public class MemberMoblieLikeVo {

	/** 1.点赞评论 2.点赞内容 **/
	private Integer type;
	/** 评论ID **/
	private Integer commentId;
	/** 评论内容 **/
	private String comment;
	/**手机内容VO**/
	private ContentMobileVo mobileContent;

	public MemberMoblieLikeVo() {
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getCommentId() {
		return commentId;
	}

	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ContentMobileVo getMobileContent() {
		return mobileContent;
	}

	public void setMobileContent(ContentMobileVo mobileContent) {
		this.mobileContent = mobileContent;
	}

	/**1.点赞评论 2.点赞内容 **/
	public static final Integer TYPE_1 = 1;
	/**1.点赞评论 2.点赞内容 **/
	public static final Integer TYPE_2 = 2;
}
