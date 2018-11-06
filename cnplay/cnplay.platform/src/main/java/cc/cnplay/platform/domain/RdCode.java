package cc.cnplay.platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cc.cnplay.core.domain.SuperEntity;

@Entity
@Table(name = "p_rd_code")
public class RdCode extends SuperEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "rd_code")
	private String code;

	@Column(name = "using_num")
	private Integer usingNum = 0;

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public int getUsingNum()
	{
		return usingNum != null ? usingNum : 0;
	}

	public void setUsingNum(Integer usingNum)
	{
		this.usingNum = usingNum;
	}

}
