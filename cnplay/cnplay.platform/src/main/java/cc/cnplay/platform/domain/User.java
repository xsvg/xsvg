package cc.cnplay.platform.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "p_user")
public class User extends SuperCheckEntity
{
	private static final long serialVersionUID = 1L;

	public static final String ROOT = "root";

	public static final String ADMIN = "admin";

	@Memo("登录帐号")
	@Column(name = "username")
	private String username;

	@Memo("登录密码")
	@Column(name = "password")
	@JsonIgnore
	private String password;

	@Memo("是否需要改密码")
	@Column(name = "update_pwd_need", columnDefinition = "char(1) default 0")
	private Boolean updatePwdNeed = false;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_pwd_time")
	private Date updatePwdTime = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "login_time")
	private Date loginTime = new Date();

	@Column(name = "login_fail_num")
	private Integer loginFailNum = 0;

	@Memo("手机号码")
	@Column(name = "mobile")
	private String mobile;

	@Memo("身份证号码")
	@Column(name = "id_card")
	private String idCard;

	@Memo("用户姓名")
	@Column(name = "name")
	private String name;

	@Memo("岗位名称")
	@Column(name = "job_name")
	private String jobName;

	@Memo("用户性别")
	@Column(name = "sex")
	private String sex;

	@Memo("用户状态")
	@Column(name = "status", length = 1)
	private Status status = Status.Normal;

	@Memo("电话号码")
	@Column(name = "phone")
	private String phone;

	@Memo("登录验证方式 0：系统配置，1：用户ID+密码")
	@Column(name = "auth_type", length = 1)
	private int authType = 0;

	@Memo("排列顺序")
	@Column(name = "sort")
	private int sort;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "org_id", referencedColumnName = "id")
	private Organization organization;

	@Transient
	private String orgId;

	@Transient
	private String orgName;

	@Transient
	private boolean isCollectFinger;

	@Transient
	private boolean debug = false;

	@Transient
	private boolean stress;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "user")
	private List<UserRole> userRoles = new ArrayList<UserRole>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "user")
	private List<UserRight> userRight = new ArrayList<UserRight>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "user")
	private List<UserFinger> userFinger = new ArrayList<UserFinger>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "toUser")
	private List<UserEmpower> toUserEmpowers = new ArrayList<UserEmpower>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "fromUser")
	private List<UserEmpower> fromUserEmpowers = new ArrayList<UserEmpower>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "user")
	private List<UserOperLog> userOperLogs = new ArrayList<UserOperLog>();

	public boolean isSysAuthType()
	{
		return authType == 0;
	}

	public Date getLoginTime()
	{
		return loginTime;
	}

	public void setLoginTime(Date loginTime)
	{
		this.loginTime = loginTime;
	}

	public Boolean getUpdatePwdNeed()
	{
		return updatePwdNeed != null ? updatePwdNeed : false;
	}

	public void setUpdatePwdNeed(Boolean updatePwdNeed)
	{
		this.updatePwdNeed = updatePwdNeed;
	}

	public Integer getLoginFailNum()
	{
		return loginFailNum == null ? 0 : loginFailNum;
	}

	public void setLoginFailNum(Integer loginFailNum)
	{
		this.loginFailNum = loginFailNum;
	}

	public String getOrgId()
	{
		return orgId;
	}

	public void setOrgId(String orgId)
	{
		this.orgId = orgId;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public Date getUpdatePwdTime()
	{
		return updatePwdTime;
	}

	public void setUpdatePwdTime(Date updatePwdTime)
	{
		this.updatePwdTime = updatePwdTime;
	}

	public String getMobile()
	{
		return mobile;
	}

	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}

	public String getIdCard()
	{
		return idCard;
	}

	public void setIdCard(String idCard)
	{
		this.idCard = idCard;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public int getSort()
	{
		return sort;
	}

	public void setSort(int sort)
	{
		this.sort = sort;
	}

	public Organization getOrganization()
	{
		return organization;
	}

	public void setOrganization(Organization organization)
	{
		this.organization = organization;
	}

	public int getAuthType()
	{
		return authType;
	}

	public void setAuthType(int authType)
	{
		this.authType = authType;
	}

	public String getJobName()
	{
		return jobName;
	}

	public void setJobName(String jobName)
	{
		this.jobName = jobName;
	}

	public String getSex()
	{
		return sex;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}

	public String getOrgName()
	{
		return orgName;
	}

	public void setOrgName(String orgName)
	{
		this.orgName = orgName;
	}

	public boolean getIsCollectFinger()
	{
		return isCollectFinger;
	}

	public void setIsCollectFinger(boolean isCollectFinger)
	{
		this.isCollectFinger = isCollectFinger;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public boolean isStress()
	{
		return stress;
	}

	public void setStress(boolean stress)
	{
		this.stress = stress;
	}

}
