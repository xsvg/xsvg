package cc.cnplay.store.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperEntity;

/**
 * 
 * 组织架构
 * 
 * @author peixere@qq.com
 * 
 * @version 2013-08-13
 * 
 */
@Entity
@Table(name = "store_area")
public class StoreArea extends SuperEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String LevelCode = "10000";

	@Column(name = "parent_id", nullable = true, columnDefinition = "char(36)", length = 36)
	private String parentId;
	@Transient
	private String parentName;
	@Memo("所在网点")
	@Column(name = "org_id")
	private String orgId;

	@Memo("所在网点")
	@Column(name = "org_name")
	private String orgName;

	@Memo("层次编码")
	@Column(name = "level_code")
	private String levelCode;

	@Column(name = "code", nullable = false, length = 50)
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "sort", nullable = false)
	private int sort;

	@Transient
	private java.util.List<StoreArea> children;

	@Transient
	private boolean checked;

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public java.util.List<StoreArea> getChildren() {
		return children;
	}

	public void setChildren(java.util.List<StoreArea> children) {
		this.children = children;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

}
