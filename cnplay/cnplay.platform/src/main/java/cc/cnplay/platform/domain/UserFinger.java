package cc.cnplay.platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "p_user_finger")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class UserFinger extends SuperCheckEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	@Memo("手指编号")
	@Column(name = "finger_num")
	private String fingerNum;

	@Memo("指纹数据")
	@Column(name = "finger_data", length = 600)
	private String fingerData;

	@Memo("指纹设备供应商标识")
	@Column(name = "finger_dev", length = 10)
	private String fingerDev;

	@Memo("是否胁迫指纹")
	@Column(name = "stress", columnDefinition = "char(1)")
	private boolean stress;
	
	@Transient
	private String stressStr;
	
	public String getUserId()
	{
		return user != null ? user.getId() : null;
	}

	public void setUserId(String userId)
	{
		if (userId != null && userId.trim().length() > 0)
		{
			if (user == null)
			{
				user = new User();
			}
			user.setId(userId);
		}
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public String getFingerNum()
	{
		return fingerNum;
	}

	public void setFingerNum(String fingerNum)
	{
		this.fingerNum = fingerNum;
	}

	public String getFingerData()
	{
		return fingerData;
	}

	public void setFingerData(String fingerData)
	{
		this.fingerData = fingerData;
	}

	public String getFingerDev()
	{
		return fingerDev;
	}

	public void setFingerDev(String fingerDev)
	{
		this.fingerDev = fingerDev;
	}

	public boolean isStress()
	{
		return stress;
	}

	public void setStress(boolean stress)
	{
		this.stress = stress;
	}

	public String getStressStr() {
		return stressStr;
	}

	public void setStressStr(String stressStr) {
		this.stressStr = stressStr;
		this.stress=Boolean.valueOf(stressStr);
	}
	
	

}
