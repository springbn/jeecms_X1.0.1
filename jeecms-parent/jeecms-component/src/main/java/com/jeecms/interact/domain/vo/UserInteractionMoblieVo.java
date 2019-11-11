/**   
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */  

package com.jeecms.interact.domain.vo;

import com.jeecms.content.domain.vo.ContentMobileVo;

/**   
 * 我的互动手机VO
 * @author: ljw
 * @date:   2019年7月20日 下午2:05:05     
 */
public class UserInteractionMoblieVo {

	/**评论ID**/
	private Integer id;
	/**文本内容**/
	private String text;
	/**评论时间**/
	private String commentTime;
	/**互动类型 1.我的评论，2.我的回复，3.回复我的**/
	private Integer type;
	/**互动用户名称**/
	private String username;
	/**我的点赞数**/
	private Integer upCount;
	/**父结点Id**/
	private Integer parentId;
	/**手机内容VO**/
	private ContentMobileVo mobileContent;
	
	public UserInteractionMoblieVo(){}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(String commentTime) {
		this.commentTime = commentTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getUpCount() {
		return upCount;
	}

	public void setUpCount(Integer upCount) {
		this.upCount = upCount;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public ContentMobileVo getMobileContent() {
		return mobileContent;
	}

	public void setMobileContent(ContentMobileVo mobileContent) {
		this.mobileContent = mobileContent;
	}

	/**我的评论**/
	public static final Integer TYPE_1 = 1;
	/**我的回复**/
	public static final Integer TYPE_2 = 2;
	/**回复我的**/
	public static final Integer TYPE_3 = 3;
}