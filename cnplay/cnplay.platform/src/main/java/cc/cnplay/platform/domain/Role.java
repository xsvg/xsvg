package cc.cnplay.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import cc.cnplay.core.domain.SuperCheckEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "p_role")
public class Role extends SuperCheckEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SuperAdminId = "888cdd31a73425fa53af55bb439888";

	@Column
	private String name;

	@Column
	private int sort = 0;

	@Column(name = "sub_flag", columnDefinition = "char(1) default 1")
	private Boolean subFlag = true;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "org_id")
	private Organization org;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "role")
	private List<UserRole> userRoles = new ArrayList<UserRole>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "role")
	private List<RoleRight> roleRight = new ArrayList<RoleRight>();

	@Transient
	private boolean checked = false;
	@Transient
	private boolean leaf = true;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getSort()
	{
		return sort;
	}

	public void setSort(int sort)
	{
		this.sort = sort;
	}

	public Boolean getSubFlag()
	{
		return subFlag == null ? Boolean.FALSE : subFlag;
	}

	public void setSubFlag(Boolean subFlag)
	{
		this.subFlag = subFlag;
	}

	public Organization getOrg()
	{
		return org;
	}

	public void setOrg(Organization org)
	{
		this.org = org;
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

	public boolean isLeaf()
	{
		return leaf;
	}

	public void setLeaf(boolean leaf)
	{
		this.leaf = leaf;
	}

}
