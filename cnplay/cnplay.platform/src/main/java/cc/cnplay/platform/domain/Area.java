package cc.cnplay.platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;

@Entity
@Table(name = "p_area")
public class Area extends SuperCheckEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String LevelCode = "10000";

	@Column(name = "parent_id")
	private String parentId;

	@Transient
	private String parentName;

	@Transient
	private boolean checked;

	@Memo("区域编码")
	@Column(name = "area_code", unique = true)
	private String code;

	@Memo("域名称")
	@Column(name = "name")
	private String name;

	@Memo("负责人")
	@Column
	private String leader;

	@Memo("层次编码")
	@Column(name = "level_code")
	private String levelCode;

	@Column
	private int sort = 0;

	@Column
	private Status status = Status.Normal;

	@Memo("创建机构")
	@Column(name = "org_id", length = 32)
	private String orgId;

	public String getParentId()
	{
		return parentId;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	public String getParentName()
	{
		return parentName;
	}

	public void setParentName(String parentName)
	{
		this.parentName = parentName;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getLeader()
	{
		return leader;
	}

	public void setLeader(String leader)
	{
		this.leader = leader;
	}

	public String getLevelCode()
	{
		return levelCode;
	}

	public void setLevelCode(String levelCode)
	{
		this.levelCode = levelCode;
	}

	public int getSort()
	{
		return sort;
	}

	public void setSort(int sort)
	{
		this.sort = sort;
	}

	public static String getLevelcode()
	{
		return LevelCode;
	}

	public String getOrgId()
	{
		return orgId;
	}

	public void setOrgId(String orgId)
	{
		this.orgId = orgId;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

}
