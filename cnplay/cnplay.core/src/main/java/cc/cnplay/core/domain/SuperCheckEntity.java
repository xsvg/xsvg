package cc.cnplay.core.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

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
public abstract class SuperCheckEntity extends SuperEntity
{
	private static final long serialVersionUID = 1L;

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

}
