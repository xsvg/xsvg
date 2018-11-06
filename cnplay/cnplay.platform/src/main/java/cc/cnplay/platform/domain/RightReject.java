package cc.cnplay.platform.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cc.cnplay.core.domain.SuperEntity;

@Entity
@Table(name = "p_right_reject")
public class RightReject extends SuperEntity implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(length = 32, name = "ref_right_id")
	private String refRightId;
	@Column(length = 32, name = "reject_right_id")
	private String rejectRightId;

	public String getRefRightId()
	{
		return refRightId;
	}

	public void setRefRightId(String refRightId)
	{
		this.refRightId = refRightId;
	}

	public String getRejectRightId()
	{
		return rejectRightId;
	}

	public void setRejectRightId(String rejectRightId)
	{
		this.rejectRightId = rejectRightId;
	}

}
