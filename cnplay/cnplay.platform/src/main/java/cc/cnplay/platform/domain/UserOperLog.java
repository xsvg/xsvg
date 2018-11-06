package cc.cnplay.platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 用户/指纹增删查改日志
 * 
 * @author peixere@qq.com
 *
 */
@Entity()
@Table(name = "p_user_oper_log")
@Memo("用户操作日志表")
public class UserOperLog extends SuperEntity
{
	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	private String name;

	@Column(name = "temote_addr")
	private String remoteAddr;

	@Column(name = "temote_user")
	private String remoteUser;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_create_id")
	private User userCreate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_check_id")
	private User userCheck;

	@Memo("用户真实姓名")
	@Transient
	private String username;
	@Memo("操作用户真实姓名")
	@Transient
	private String createUsername;
	@Memo("复核用户真实姓名")
	@Transient
	private String checkUsername;

	@Memo("用户所在机构")
	@Transient
	private String orgId;
	@Transient
	private String orgName;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public User getUserCreate()
	{
		return userCreate;
	}

	public void setUserCreate(User userCreate)
	{
		this.userCreate = userCreate;
	}

	public User getUserCheck()
	{
		return userCheck;
	}

	public void setUserCheck(User userCheck)
	{
		this.userCheck = userCheck;
	}

	public String getUsername()
	{
		return user != null ? user.getName() : "";
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getCreateUsername()
	{
		return userCreate != null ? userCreate.getName() : "";
	}

	public void setCreateUsername(String createUsername)
	{
		this.createUsername = createUsername;
	}

	public String getCheckUsername()
	{
		return userCheck != null ? userCheck.getName() : "";
	}

	public void setCheckUsername(String checkUsername)
	{
		this.checkUsername = checkUsername;
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
		return (userCreate != null && userCreate.getOrganization() != null) ? userCreate.getOrganization().getName() : "";
	}

	public void setOrgName(String orgName)
	{
		this.orgName = orgName;
	}

	public String getUserName()
	{
		return user != null ? user.getName() : "";
	}

	public String getUserCreateName()
	{
		return userCreate != null ? userCreate.getName() : "";
	}

	public String getUserCheckName()
	{
		return userCheck != null ? userCheck.getName() : "";
	}

}
