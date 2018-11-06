package cc.cnplay.platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "p_organization")
public class Organization extends SuperCheckEntity
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

	@Memo("机构编码")
	@Column(name = "org_code", unique = true)
	private String code;

	@Memo("机构名称")
	@Column(name = "name")
	private String name;

	@Memo("机构地址")
	@Column(name = "address")
	private String address;

	@Memo("负责人")
	@Column
	private String leader;

	@Memo("层次编码")
	@Column(name = "level_code")
	private String levelCode;

	@Column
	private Integer sort = 0;

	@Column
	private Status status = Status.Normal;

	@Memo("机构类型")
	@Column(name = "org_type")
	private String orgType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "manager_id")
	private Organization manager;

	@Memo("同步IDe")
	@Column(name = "sync_id")
	private String syncId;
	
	@Transient
	private String managerId;
	@Transient
	private String managerName;
	@Transient
	private boolean checked;

	public String getManagerId()
	{
		return manager != null ? manager.getId() : managerId;
	}

	public void setManagerId(String mangerId)
	{
		this.managerId = mangerId;
	}

	public String getManagerName()
	{
		return manager != null ? manager.getName() : managerName;
	}

	public void setManagerName(String managerName)
	{
		this.managerName = managerName;
	}

	public String getParentId()
	{
		return parentId;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
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

	public Integer getSort()
	{
		return sort;
	}

	public void setSort(Integer sort)
	{
		this.sort = sort;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public String getOrgType()
	{
		return orgType;
	}

	public void setOrgType(String orgType)
	{
		this.orgType = orgType;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getParentName()
	{
		return parentName;
	}

	public void setParentName(String parentName)
	{
		this.parentName = parentName;
	}

	public Organization getManager()
	{
		return manager;
	}

	public void setManager(Organization manager)
	{
		this.manager = manager;
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

	public String getSyncId()
	{
		return syncId;
	}

	public void setSyncId(String syncId)
	{
		this.syncId = syncId;
	}

}
