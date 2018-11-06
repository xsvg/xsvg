package cc.cnplay.platform.vo;

import cc.cnplay.core.annotation.Memo;

public class LoginConf
{

	@Memo("网页登录方式")
	public static String LoginByWeb = "loginByWeb";

	@Memo("管理软件指纹设备类型1：天诚； 2：中正； 3：维尔 4：中正中行)")
	public static String webFingerType = "fingerType";

	private int fingerDev = 1;
	@Memo("网页登录方式:8位：b0.密码，b1指纹")
	private boolean verifyFinger = false;
	private boolean verifyPwd = true;

	private String msg = "";

	public int getFingerDev()
	{
		return fingerDev;
	}

	public void setFingerDev(int fingerDev)
	{
		this.fingerDev = fingerDev;
	}

	public boolean isVerifyFinger()
	{
		return verifyFinger;
	}

	public void setVerifyFinger(boolean verifyFinger)
	{
		this.verifyFinger = verifyFinger;
	}

	public boolean isVerifyPwd()
	{
		return verifyPwd;
	}

	public void setVerifyPwd(boolean verifyPwd)
	{
		this.verifyPwd = verifyPwd;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

}
