package cc.cnplay.platform.domain;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.Identity;
import cc.cnplay.core.util.DateUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity()
@Table(name = "p_user_log")
@Memo("系统日志表")
public class UserLog extends Identity
{
	private static final long serialVersionUID = 1L;

	@Column(name = "method")
	private String method;

	@Column(name = "name")
	private String name;

	@Column(name = "value")
	private String value;

	@Column(name = "url")
	private String url;

	@Column(name = "proceed", length = 512)
	private String proceed;

	@Column(name = "args_clazz")
	private String argsClazz;

	@Column(name = "return_clazz")
	private String returnClazz;

	@Column(name = "remote_addr")
	private String remoteAddr;

	@Column(name = "remote_user")
	private String remoteUser;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	@Memo("用户真实姓名")
	@Transient
	private String username;

	@Memo("用户所在机构")
	@Transient
	private String orgId;
	@Transient
	private String orgName;

	@Column(name = "args_json", length = 4000)
	private String argsJson;

	// 日志单独公共字段，不进行继承
	@Column(name = "create_username", updatable = false, length = 50)
	private String createUsername;

	@Column(name = "create_check_username", updatable = false, length = 50)
	private String createCheckUsername;

	@Column(name = "update_username", length = 50)
	private String updateUsername;

	@Column(name = "update_check_username", length = 50)
	private String updateCheckUsername;

	public String getCreateUsername()
	{
		return createUsername;
	}

	public void setCreateUsername(String createUsername)
	{
		this.createUsername = createUsername;
	}

	public String getCreateCheckUsername()
	{
		return createCheckUsername;
	}

	public void setCreateCheckUsername(String createCheckUsername)
	{
		this.createCheckUsername = createCheckUsername;
	}

	public String getUpdateUsername()
	{
		return updateUsername;
	}

	public void setUpdateUsername(String updateUsername)
	{
		this.updateUsername = updateUsername;
	}

	public String getUpdateCheckUsername()
	{
		return updateCheckUsername;
	}

	public void setUpdateCheckUsername(String updateCheckUsername)
	{
		this.updateCheckUsername = updateCheckUsername;
	}
	
	@Column(name = "create_time", updatable = false)
	@OrderBy("desc")
	@Index(name="UserLogCreateTimeIndex")
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
	/**	公共部分字段完 **/
	
	public String getArgsJson()
	{
		return argsJson;
	}

	public void setArgsJson(String argsJson)
	{
		this.argsJson = argsJson;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getArgsClazz()
	{
		return argsClazz;
	}

	public void setArgsClazz(String argsClazz)
	{
		this.argsClazz = argsClazz;
	}

	public String getReturnClazz()
	{
		return returnClazz;
	}

	public void setReturnClazz(String returnClazz)
	{
		this.returnClazz = returnClazz;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getProceed()
	{
		return proceed;
	}

	public void setProceed(String proceed)
	{
		this.proceed = proceed;
	}

	public String getRemoteAddr()
	{
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr)
	{
		this.remoteAddr = remoteAddr;
	}

	public String getRemoteUser()
	{
		return remoteUser;
	}

	public void setRemoteUser(String remoteUser)
	{
		this.remoteUser = remoteUser;
	}

	public String getOrgId()
	{
		return orgId;
	}

	public void setOrgId(String orgId)
	{
		this.orgId = orgId;
	}

	public String getOrgName()
	{
		return orgName;
	}

	public void setOrgName(String orgName)
	{
		this.orgName = orgName;
	}
}
