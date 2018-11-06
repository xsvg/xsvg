package cc.cnplay.jdbcconfig;

import com.alibaba.druid.pool.DruidDataSource;

public class AliPoolDruidDataSource extends DruidDataSource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean encode = false;

	public boolean isEncode()
	{
		return encode;
	}

	public void setEncode(String encode)
	{
		this.encode = Boolean.parseBoolean(encode);
	}

	@Override
	public void setUsername(String username)
	{
		if (encode)
		{
			super.setUsername(ConfigEncode.decryptData(username));
		}
		else
		{
			super.setUsername(username);
		}
		//System.err.println(this.getUsername());
	}

	@Override
	public void setPassword(String password)
	{
		if (encode)
		{
			super.setPassword(ConfigEncode.decryptData(password));
		}
		else
		{
			super.setPassword(password);
		}
		//System.err.println(this.getPassword());
	}
}
