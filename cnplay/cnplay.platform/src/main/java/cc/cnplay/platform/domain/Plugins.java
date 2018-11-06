package cc.cnplay.platform.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "p_plugins")
public class Plugins extends SuperEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column
	private String name;

	@Column(name = "file_name")
	private String fileName;

	@Memo("状态")
	@Column(name = "status", length = 1)
	private Status status = Status.Normal;

	@Transient
	private String statusStr ;
	
	@Column
	private int sort;

	@Memo("版本号")
	@Column(name = "version_num", length = 50)
	private String versionNum;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "plugins")
	private List<Right> rights;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	
	
	public String getStatusStr() {
		switch (status) {
		case Normal:
			statusStr = "正常";
			break;
		case Banned:
			statusStr = "挂起";
			break;
		case Delete:
			statusStr = "删除";
			break;
		default:
			break;
		}
		return statusStr;
	}

	public int getSort()
	{
		return sort;
	}

	public void setSort(int sort)
	{
		this.sort = sort;
	}

	public String getVersionNum()
	{
		return versionNum;
	}

	public void setVersionNum(String versionNum)
	{
		this.versionNum = versionNum;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

}
