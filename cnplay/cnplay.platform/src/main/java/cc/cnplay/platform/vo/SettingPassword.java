package cc.cnplay.platform.vo;

public class SettingPassword
{
	private String username;
	private String password;
	private String newpass;
	private String newpassCheck;
	private boolean passwordEncoder;

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getNewpass()
	{
		return newpass;
	}

	public void setNewpass(String newpass)
	{
		this.newpass = newpass;
	}

	public String getNewpassCheck()
	{
		return newpassCheck;
	}

	public void setNewpassCheck(String newpassCheck)
	{
		this.newpassCheck = newpassCheck;
	}

	public boolean isPasswordEncoder()
	{
		return passwordEncoder;
	}

	public void setPasswordEncoder(boolean passwordEncoder)
	{
		this.passwordEncoder = passwordEncoder;
	}

}
