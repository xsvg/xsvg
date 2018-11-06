package cc.cnplay.platform.vo;

import java.io.Serializable;
import java.util.Date;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.platform.domain.User;

@Memo("登录信息")
public class LoginUser extends User implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String sessionId;

	@Memo("用户指纹登录")
	private String fingerData;

	@Memo("指纹设备标示")
	private String fingerDev = "1";

	@Memo("密码是否加密")
	private boolean passwordEncoder;

	@Memo("登录URL")
	private String url;

	@Memo("登录时间")
	private Date loginTime;
	
	@Memo("登录时间格式化字段")
	private String loginTimeStr;

	@Memo("登录结果")
	private boolean result;

	@Memo("尝试次数")
	private int tryCount = 1;

	@Memo("登录客户端地址")
	private String remoteIP;

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
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

	public boolean isPasswordEncoder()
	{
		return passwordEncoder;
	}

	public void setPasswordEncoder(boolean passwordEncoder)
	{
		this.passwordEncoder = passwordEncoder;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public Date getLoginTime()
	{
		return loginTime;
	}

	public void setLoginTime(Date loginTime)
	{
		this.loginTime = loginTime;
	}


	public String getLoginTimeStr()
	{
		return loginTimeStr;
	}

	public void setLoginTimeStr(String loginTimeStr)
	{
		this.loginTimeStr = loginTimeStr;
	}

	public boolean isResult()
	{
		return result;
	}

	public void setResult(boolean result)
	{
		this.result = result;
	}

	public int getTryCount()
	{
		return tryCount;
	}

	public void setTryCount(int tryCount)
	{
		this.tryCount = tryCount;
	}

	public String getRemoteIP()
	{
		return remoteIP;
	}

	public void setRemoteIP(String remoteIP)
	{
		this.remoteIP = remoteIP;
	}

}
