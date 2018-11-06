package cc.cnplay.platform.domain;

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
import cc.cnplay.core.domain.SuperEntity;
import cc.cnplay.core.util.DateUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "p_check_log")
public class CheckLog extends SuperEntity
{
	private static final long serialVersionUID = 1L;

	@Memo("复核权限ID")
	public static final String PowerId = "A9288638B1B948B1AF1V73943V669A9A";

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "create_user_id", referencedColumnName = "id", updatable = false)
	private User createUser;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "check_user_id", referencedColumnName = "id")
	private User checkUser;

	@Transient
	private String createUsername;

	@Transient
	private String createCheckUsername;

	@Column(name = "check_time")
	private Date checkTime = new Date();

	@Column(name = "url")
	private String url;

	@JsonIgnore
	@Column(name = "check_json", updatable = false, length = 4000)
	private String checkJson;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "org_id", referencedColumnName = "id")
	private Organization organization;

	@Column(name = "status")
	private Status status = Status.Delete;

	@Transient
	private String checkTimeStr;

	public String getCheckJson()
	{
		return checkJson;
	}

	public void setCheckJson(String checkJson)
	{
		this.checkJson = checkJson;
	}

	public User getCreateUser()
	{
		return createUser;
	}

	public void setCreateUser(User createUser)
	{
		this.createUser = createUser;
	}

	public User getCheckUser()
	{
		return checkUser;
	}

	public void setCheckUser(User checkUser)
	{
		this.checkUser = checkUser;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public Date getCheckTime()
	{
		return checkTime;
	}

	public void setCheckTime(Date checkTime)
	{
		this.checkTime = checkTime;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public String getCheckTimeStr()
	{
		return checkTime == null ? null : DateUtil.format(checkTime);
	}

	public void setCheckTimeStr(String checkTimeStr) throws ParseException
	{
		this.checkTimeStr = checkTimeStr;
		this.checkTime = DateUtil.parsetDate(checkTimeStr);
	}

	public Organization getOrganization()
	{
		return organization;
	}

	public void setOrganization(Organization organization)
	{
		this.organization = organization;
	}

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

	public String getOrgName()
	{
		return organization != null ? organization.getName() : "";
	}
}
