package cc.cnplay.core.domain;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.util.DateUtil;

/**
 * 
 * 实体表基类
 * 
 * @author peixere@qq.com
 * 
 * @version 2012-12-03
 * 
 */
@MappedSuperclass
public abstract class SuperEntity extends Identity
{
	private static final long serialVersionUID = 1L;

	@Column(name = "create_time", updatable = false)
	@OrderBy("desc")
	private Date createTime = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	private Date updateTime = new Date();

	@Memo("备注")
	@Column(name = "memo", length = 512, nullable = true)
	private String memo;

	@Transient
	private String displayName;

	public Date getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(Date createTime)
	{
		this.createTime = createTime;
	}

	public Date getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(Date updateTime)
	{
		this.updateTime = updateTime;
	}

	public String getMemo()
	{
		return memo;
	}

	public void setMemo(String memo)
	{
		this.memo = memo;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public void setCreateTimeStr(String createTimeStr) throws ParseException
	{
		this.createTime = DateUtil.parsetDate(createTimeStr);
	}

	public String getCreateTimeStr()
	{
		return createTime == null ? null : DateUtil.format(createTime);
	}

	public String getUpdateTimeStr()
	{
		return updateTime == null ? null : DateUtil.format(updateTime);
	}

	public void setUpdateTimeStr(String updateTimeStr) throws ParseException
	{
		this.updateTime = DateUtil.parsetDate(updateTimeStr);
	}

}
