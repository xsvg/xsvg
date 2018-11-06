package cc.cnplay.platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperEntity;

@Entity
@Table(name = "p_sys_config")
public class SysConfig extends SuperEntity
{

	/**
	 * 运行参数
	 */
	private static final long serialVersionUID = 1L;

	@Memo("模块")
	@Column(name = "module_name")
	private String moduleName;

	@Column(unique = true, nullable = false)
	private String name;

	@Column
	private String value;

	@Column
	private int sort;

	@Memo("用户是否可配")
	@Column(name = "user_conf", columnDefinition = "char(1) default 0")
	private Boolean userConf = Boolean.FALSE;

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

	public int getSort()
	{
		return sort;
	}

	public void setSort(int sort)
	{
		this.sort = sort;
	}

	public String getModuleName()
	{
		return moduleName;
	}

	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}

	public Boolean getUserConf()
	{
		return userConf == null ? Boolean.FALSE : userConf;
	}

	public void setUserConf(Boolean userConf)
	{
		this.userConf = userConf;
	}

}
