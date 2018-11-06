package cc.cnplay.platform.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;
import cc.cnplay.core.util.DateUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * 角色权限信息表
 * 
 * @author peixere@qq.com
 * 
 * @version 2012-12-03
 * 
 */
@Entity
@Table(name = "p_user_empower")
public class UserEmpower extends SuperCheckEntity implements Serializable
{
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "form_user_id")
	@JsonIgnore
	private User fromUser;
	@Transient
	private String fromUserId;
	@Transient
	private String fromUserName;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "to_user_id")
	@JsonIgnore
	private User toUser;
	@Transient
	private String toUserId;
	@Transient
	private String toUserName;

	@Memo("用户状态")
	@Column(name = "state", length = 1)
	private Status status = Status.Normal;

	@Column(name = "start_time")
	private Date startTime = new Date();

	@Column(name = "end_time")
	private Date endTime = new Date();

	@Transient
	private String startTimeStr;

	@Transient
	private String endTimeStr;

	public User getFromUser()
	{
		return fromUser;
	}

	public void setFromUser(User fromUser)
	{
		this.fromUser = fromUser;
	}

	public User getToUser()
	{
		return toUser;
	}

	public void setToUser(User toUser)
	{
		this.toUser = toUser;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}

	public Date getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}

	public String getStartTimeStr()
	{
		return startTime == null ? null : DateUtil.format(startTime);
	}

	public void setStartTimeStr(String startTimeStr) throws ParseException
	{
		this.startTimeStr = startTimeStr;
		this.startTime = DateUtil.parsetDate(startTimeStr);
	}

	public String getEndTimeStr()
	{
		return endTime == null ? null : DateUtil.format(endTime);
	}

	public void setEndTimeStr(String endTimeStr) throws ParseException
	{
		this.endTimeStr = endTimeStr;
		this.endTime = DateUtil.parsetDate(endTimeStr);
	}

	public String getFromUserId()
	{
		return fromUser == null ? fromUserId : fromUser.getId();
	}

	public String getToUserId()
	{
		return toUser == null ? toUserId : toUser.getId();
	}

	public String getFromUserName()
	{
		return fromUserName;
	}

	public void setFromUserName(String fromUserName)
	{
		this.fromUserName = fromUserName;
	}

	public String getToUserName()
	{
		return toUserName;
	}

	public void setToUserName(String toUserName)
	{
		this.toUserName = toUserName;
	}

	public void setFromUserId(String fromUserId)
	{
		this.fromUserId = fromUserId;
	}

	public void setToUserId(String toUserId)
	{
		this.toUserId = toUserId;
	}

}