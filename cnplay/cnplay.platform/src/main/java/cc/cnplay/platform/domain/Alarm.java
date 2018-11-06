/**
 * 没有status，删除：物理删除
 */

package cc.cnplay.platform.domain;

import java.util.Date;

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
@Table(name = "p_alarm")
public class Alarm extends SuperCheckEntity
{

	private static final long serialVersionUID = 1L;

	@Memo("锁ID")
	@Column(name = "lock_id")
	private String lockId;

	@Memo("ATM编号")
	@Column(name = "atm_sn")
	private String atmSn;

	@Memo("用户aID")
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "usera_id", updatable = false)
	private User usera;

	@Memo("用户bID")
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userb_id", updatable = false)
	private User userb;

	// 0.胁迫告警，1.振动告警，2.高温告警，3.登录告警
	@Memo("告警类型")
	@Column(name = "alarm_type")
	private int alarmType;

	@Memo("告警时间")
	@Column(name = "alarm_time")
	private Date alarmTime;

	@Memo("所属机构")
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "org_id", updatable = false)
	private Organization org;

	@Memo("状态")
	@Column(name = "status")
	private int status = 0; // 0.未处理、1.已接收、2.已处理

	@Memo("处理人")
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "processUserId")
	private User processUser;

	@Memo("处理时间")
	@Column(name = "processTime")
	private Date processTime;

	@Memo("处理描述")
	@Column(name = "processDesc", length = 256)
	private String processDesc;

	@Transient
	private String alarmTypeStr;// 类型字串
	@Transient
	private String statusStr;// 类型字串

	@Transient
	private String userName;

	@Transient
	private String processUserId;
	@Transient
	private String processUserName;

	@Transient
	private String orgName;
	@Transient
	private String alarmTimeStr;
	@Transient
	private String address;
	@Transient
	private String processTimeStr;

	// --------------------------

	public String getAlarmTimeStr()
	{
		return alarmTimeStr;
	}

	public String getStatusStr()
	{
		return statusStr;
	}

	public void setStatusStr(String statusStr)
	{
		this.statusStr = statusStr;
	}

	public void setAlarmTimeStr(String alarmTimeStr)
	{
		this.alarmTimeStr = alarmTimeStr;
	}

	public Integer getAlarmType()
	{
		return alarmType;
	}

	public void setAlarmType(Integer alarmType)
	{
		this.alarmType = alarmType;
	}

	public Integer getStatus()
	{
		return status;
	}

	public void setStatus(Integer status)
	{
		this.status = status;
	}

	public String getAlarmTypeStr()
	{
		return alarmTypeStr;
	}

	public void setAlarmTypeStr(String alarmTypeStr)
	{
		this.alarmTypeStr = alarmTypeStr;
	}

	public String getAtmSn()
	{
		return atmSn;
	}

	public void setAtmSn(String atmSn)
	{
		this.atmSn = atmSn;
	}

	public String getOrgName()
	{
		return orgName;
	}

	public void setOrgName(String orgName)
	{
		this.orgName = orgName;
	}

	public Date getAlarmTime()
	{
		return alarmTime;
	}

	public void setAlarmTime(Date alarmTime)
	{
		this.alarmTime = alarmTime;
	}

	public User getProcessUser()
	{
		return processUser;
	}

	public void setProcessUser(User processUser)
	{
		this.processUser = processUser;
	}

	public String getProcessUserId()
	{
		if (processUser != null)
		{
			processUserId = processUser.getId();
		}
		return processUserId;
	}

	public String getProcessUserName()
	{
		if (processUser != null)
		{
			processUserName = processUser.getUsername();
		}
		return processUserName;
	}

	public void setProcessUserName(String processUserName)
	{
		this.processUserName = processUserName;
	}

	public Date getProcessTime()
	{
		return processTime;
	}

	public void setProcessTime(Date processTime)
	{
		this.processTime = processTime;
	}

	public String getProcessDesc()
	{
		return processDesc;
	}

	public void setProcessDesc(String processDesc)
	{
		this.processDesc = processDesc;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getProcessTimeStr()
	{
		return processTimeStr;
	}

	public void setProcessTimeStr(String processTimeStr)
	{
		this.processTimeStr = processTimeStr;
	}

	public String getLockId()
	{
		return lockId;
	}

	public void setLockId(String lockId)
	{
		this.lockId = lockId;
	}

	public User getUsera()
	{
		return usera;
	}

	public void setUsera(User usera)
	{
		this.usera = usera;
	}

	public User getUserb()
	{
		return userb;
	}

	public void setUserb(User userb)
	{
		this.userb = userb;
	}

	public Organization getOrg()
	{
		return org;
	}

	public void setOrg(Organization org)
	{
		this.org = org;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

}