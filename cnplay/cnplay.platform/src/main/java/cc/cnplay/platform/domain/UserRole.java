package cc.cnplay.platform.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cc.cnplay.core.domain.SuperCheckEntity;

@Entity
@Table(name = "p_user_role")
public class UserRole extends SuperCheckEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ManyToOne(fetch = FetchType.EAGER)
	private User user;
	@ManyToOne(fetch = FetchType.EAGER)
	private Role role;
	public User getUser()
	{
		return user;
	}
	public void setUser(User user)
	{
		this.user = user;
	}
	public Role getRole()
	{
		return role;
	}
	public void setRole(Role role)
	{
		this.role = role;
	}
	
}
