package cc.cnplay.platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cc.cnplay.core.annotation.Ignore;
import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;

@Entity
@Table(name = "p_organization_config")
public class OrganizationConfig extends SuperCheckEntity
{

	/**
	 * 机构配置
	 */
	private static final long serialVersionUID = 1L;

	@Column
	private String name;

	@Column
	private String value;

	@Memo("下级机构是否可配置项")
	@Column(name = "sub_org_update", columnDefinition = "char(1)")
	private boolean subOrgUpdate;

	@Ignore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id", referencedColumnName = "id")
	private Organization organization;

//	@Column(name = "organization_id", length = 32)
//	private String organizationId;

	@Column
	private Integer sort = 0;

	// -------------------------------------------

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

//	public String getOrganizationId()
//	{
//		return organizationId;
//	}
//
//	public void setOrganizationId(String organizationId)
//	{
//		this.organizationId = organizationId;
//	}

	public boolean getSubOrgUpdate()
	{
		return subOrgUpdate;
	}

	public Organization getOrganization()
	{
		return organization;
	}

	public void setOrganization(Organization organization)
	{
		this.organization = organization;
	}

	public void setSubOrgUpdate(boolean subOrgUpdate)
	{
		this.subOrgUpdate = subOrgUpdate;
	}

	public Integer getSort()
	{
		if (this.sort == null)
		{
			return new Integer(0);
		}
		else
		{
			return sort;
		}
	}

	public void setSort(Integer sort)
	{
		if (sort == null)
		{
			this.sort = new Integer(0);
		}
		else
		{
			this.sort = sort;
		}
	}

}
