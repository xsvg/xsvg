package cc.cnplay.platform.vo;

public class HomeInfo
{
	private String id;
	private String username;
	private String userFullname;
	private String logoutUrl;
	private String logoutText;
	private String logoImg;
	private String topbgImg;
	private String name;
	private String title;
	private String fontStyle;
	private String desktopPanel;
	private boolean needModifyPswd;
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getUserFullname()
	{
		return userFullname;
	}

	public void setUserFullname(String userFullname)
	{
		this.userFullname = userFullname;
	}

	public String getLogoutUrl()
	{
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl)
	{
		this.logoutUrl = logoutUrl;
	}

	public String getLogoImg()
	{
		return logoImg;
	}

	public void setLogoImg(String logoImg)
	{
		this.logoImg = logoImg;
	}

	public String getTopbgImg()
	{
		return topbgImg;
	}

	public void setTopbgImg(String topbgImg)
	{
		this.topbgImg = topbgImg;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getFontStyle()
	{
		return fontStyle;
	}

	public void setFontStyle(String fontStyle)
	{
		this.fontStyle = fontStyle;
	}

	public String getDesktopPanel()
	{
		return desktopPanel;
	}

	public void setDesktopPanel(String desktopPanel)
	{
		this.desktopPanel = desktopPanel;
	}

	public String getLogoutText()
	{
		return logoutText;
	}

	public void setLogoutText(String logoutText)
	{
		this.logoutText = logoutText;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isNeedModifyPswd() {
		return needModifyPswd;
	}

	public void setNeedModifyPswd(boolean needModifyPswd) {
		this.needModifyPswd = needModifyPswd;
	}

}
