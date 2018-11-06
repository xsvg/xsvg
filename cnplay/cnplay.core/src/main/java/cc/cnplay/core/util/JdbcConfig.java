package cc.cnplay.core.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class JdbcConfig
{
	public static final String jdbc_driver = "jdbc.driver";
	public static final String jdbc_url = "jdbc.url";
	public static final String jdbc_username = "jdbc.username";
	public static final String jdbc_password = "jdbc.password";
	private String driver;
	private String url;
	private String username;
	private String password;

	public JdbcConfig()
	{
		this("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306?createDatabaseIfNotExist=true&amp;useUnicode=true&amp;characterEncoding=utf-8", "root", "");
	}

	public JdbcConfig(String connConfig)
	{
		ResourceBundle bundle = ResourceBundle.getBundle(connConfig, Locale.ROOT);
		this.driver = bundle.getString(jdbc_driver);
		this.url = bundle.getString(jdbc_url);
		this.username = bundle.getString(jdbc_username);
		this.password = bundle.getString(jdbc_password);

	}

	public JdbcConfig(String driver, String url, String username, String password)
	{
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public String getDriver()
	{
		return driver;
	}

	public void setDriver(String driver)
	{
		this.driver = driver;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

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
}
