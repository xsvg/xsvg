package cc.cnplay.platform.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cc.cnplay.core.domain.Identity;

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
@Table(name = "p_role_right")
public class RoleRight extends Identity implements Serializable
{
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "right_id")
	private Right right;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id")
	private Role role;

	public RoleRight()
	{
	}

	public Right getRight()
	{
		return right;
	}

	public void setRight(Right right)
	{
		this.right = right;
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