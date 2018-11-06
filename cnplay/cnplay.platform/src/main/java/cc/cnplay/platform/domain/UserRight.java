package cc.cnplay.platform.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cc.cnplay.core.domain.SuperCheckEntity;

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
@Table(name = "p_user_right")
public class UserRight extends SuperCheckEntity implements Serializable
{
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "right_id")
	private Right right;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	public Right getRight()
	{
		return right;
	}

	public void setRight(Right right)
	{
		this.right = right;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}


}