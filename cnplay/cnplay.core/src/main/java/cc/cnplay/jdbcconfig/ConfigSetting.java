package cc.cnplay.jdbcconfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class ConfigSetting
{
	static Boolean encode = false;
	static Boolean useNavigate = false;

	public static void main(String[] args)
	{
		System.out.println("动态密码锁系统，数据源配置工具，请按提示操作，输入[quit]退出。");
		System.out.println("是否使用设置向导 Y/N");
		byte[] buf = new byte[1000];
		// byte[] readbuf=null;
		int inputcount;
		String settings = "";
		try
		{
			inputcount = System.in.read(buf);
			if (inputcount >= 0)
			{
				String value = getInputValue(buf, inputcount);
				if (value.equals("Y") || value.equals("y"))
				{
					useNavigate = true;
				}
				else if (value.equals("quit"))
				{
					return;
				}
			}
			if (useNavigate)
				System.out.println("启用设置向导");
			else
				System.out.println("启用手动设置");
			System.out.println("是否加密用户登录信息 Y/N");
			inputcount = System.in.read(buf);
			if (inputcount >= 0)
			{
				String value = getInputValue(buf, inputcount);
				if (value.equals("Y") || value.equals("y"))
				{
					encode = true;
				}
				else if (value.equals("quit"))
				{
					return;
				}
			}

			if (encode)
				System.out.println("启用参数加密");
			else
				System.out.println("参数不加密");
			if (useNavigate)
			{
				// 启用设置向导
				settings = NavigattionSet();
			}
			else
			{
				// 手动配置
				settings = HandSet();
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		if (!settings.isEmpty())
		{
			// 保存配置
			if (writeJDBCFile(settings))
			{

				System.out.println("配置保存成功");
			}
			else
				System.out.println("配置保存失败");
		}

	}

	// 全手动配置
	static String HandSet()
	{
		StringBuilder sb = new StringBuilder();
		int step = 0;
		// Boolean setcompleted = false;
		byte[] buf = new byte[1000];
		String value = "";
		String[] configs = new String[] { "hibernate.show_sql", "hibernate.hbm2ddl.auto", "hibernate.temp.use_jdbc_metadata_defaults", "hibernate.default_schema", "jdbc.validationQuery", "jdbc.driver", "jdbc.url", "jdbc.username", "jdbc.password", "jpa.dialect" };
		try
		{
			if (encode)
			{
				sb.append("jdbc.encode=true" + "\r\n");
			}
			else
				sb.append("jdbc.encode=false" + "\r\n");
			for (int i = 0; i < configs.length; i++)
			{
				System.out.println(configs[i]);
				int inputcount;
				inputcount = System.in.read(buf);
				if (inputcount >= 0)
				{
					value = getInputValue(buf, inputcount);
					if (configs[i].equals("jdbc.username") || configs[i].equals("jdbc.password"))
					{
						if (encode)
							sb.append(configs[i] + "=" + ConfigEncode.encryptData(value) + "\r\n");
						else
							sb.append(configs[i] + "=" + value + "\r\n");
					}
					else
						sb.append(configs[i] + "=" + value + "\r\n");
					step++;
					if (value.equals("quit"))
					{
						sb.delete(0, sb.length());
						break;
					}
				}
			}
		}
		catch (IOException e)
		{
			System.err.println(step);
			e.printStackTrace();
		}
		return sb.toString();
	}

	static String NavigattionSet()
	{
		ConfigSettingEnum step;
		step = ConfigSettingEnum.InputDbType;
		byte[] buf = new byte[1000];
		Boolean settingCompleted = false;
		String dbType = "";
		String dbuser = "";
		String dbpassword = "";
		String serverip = "";
		String dbname = "";
		int serverport = 0;
		// 输入设置数据
		while (true)
		{
			if (settingCompleted)
				break;
			switch (step)
			{
			case InputDbType:
				System.out.println("请选择数据库类型");
				System.out.println("1.MSSQL");
				System.out.println("2.Sybase");
				System.out.println("3.Oracle");
				System.out.println("4.DB2");
				System.out.println("5.MySQL");
				try
				{
					int inputcount = System.in.read(buf);
					if (inputcount >= 0)
					{
						String value = getInputValue(buf, inputcount);
						if (value.equals("1") || value.equals("2") || value.equals("3") || value.equals("4") || value.equals("5"))
						{
							dbType = value;
							step = ConfigSettingEnum.InputServerIP;
						}
						else if (value.equals("quit"))
						{
							return "";
						}
						else
							System.out.println("数据库类型错误，请重新输入");
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			case InputServerIP:
				System.out.println("请输入数据库服务器IP[192.168.1.100]");
				try
				{
					int inputcount = System.in.read(buf);
					if (inputcount >= 0)
					{
						String value = getInputValue(buf, inputcount);
						serverip = value;
						if (value.equals("quit"))
						{
							return "";
						}
						step = ConfigSettingEnum.InputServerPort;
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			case InputServerPort:
				System.out.println("请输入数据库服务器端口");
				System.out.println("MS SQL[1433]");
				System.out.println("Sybase[5000]");
				System.out.println("Oracle[1521]");
				System.out.println("DB2[60006]");
				System.out.println("MySQL[3306]");
				try
				{
					int inputcount = System.in.read(buf);
					if (inputcount >= 0)
					{
						String value = getInputValue(buf, inputcount);
						serverport = Integer.parseInt(value);
						step = ConfigSettingEnum.DbName;
						if (value.equals("quit"))
						{
							return "";
						}
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			case DbName:
				System.out.println("请输入数据库名称");
				if (dbType.equals("3"))
				{
					System.out.println("Orcle默认为[orcl]");
					dbname = "orcl";
				}
				try
				{
					int inputcount = System.in.read(buf);
					if (inputcount > 0)
					{
						dbname = getInputValue(buf, inputcount);
						if (dbname.equals("quit"))
						{
							return "";
						}
						step = ConfigSettingEnum.InputDBUser;
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			case InputDBUser:
				System.out.println("请输入数据库登录用户名");
				try
				{
					int inputcount = System.in.read(buf);
					if (inputcount >= 0)
					{
						dbuser = getInputValue(buf, inputcount);
						if (dbuser.equals("quit"))
						{
							return "";
						}
						step = ConfigSettingEnum.InputDBPassword;
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			case InputDBPassword:
				System.out.println("请输入数据库登录密码");
				try
				{
					int inputcount = System.in.read(buf);
					if (inputcount >= 0)
					{
						dbpassword = getInputValue(buf, inputcount);
						if (dbname.equals("quit"))
						{
							return "";
						}
						settingCompleted = true;
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
		String settings = "";
		if (settingCompleted)
			settings = getConfigSetting(dbType, dbuser, dbpassword, serverip, serverport, dbname);
		return settings;
	}

	static String getConfigSetting(String dbType, String dbuser, String dbpassword, String serverip, int serverport, String dbname)
	{
		String strconfig = getConfigInfo(dbType, dbuser, dbpassword, serverip, serverport, dbname);
		return strconfig;
	}

	static String getConfigInfo(String dbType, String dbuser, String dbpassword, String serverip, int serverport, String dbname)
	{
//		"hibernate.show_sql",
//		"hibernate.hbm2ddl.auto",
//		"hibernate.temp.use_jdbc_metadata_defaults",
//		"hibernate.default_schema",
//		"jdbc.validationQuery",
//		"jdbc.driver",
//		"jdbc.url",
//		"jdbc.username",
//		"jdbc.password",
//		"jpa.dialect"

		DBTypeEnum db = getDBTypeByString(dbType);
		StringBuilder sb = new StringBuilder();
		if (encode)
		{
			dbuser = ConfigEncode.encryptData(dbuser);
			dbpassword = ConfigEncode.encryptData(dbpassword);
			sb.append("jdbc.encode=true" + "\r\n");
		}
		else
			sb.append("jdbc.encode=false" + "\r\n");
		sb.append("hibernate.show_sql=false" + "\r\n");
		sb.append("hibernate.hbm2ddl.auto=update" + "\r\n");
		sb.append("hibernate.temp.use_jdbc_metadata_defaults=false" + "\r\n");
		sb.append("hibernate.default_schema=" + "\r\n");

		switch (db)
		{
		case MySQL:
			sb.append("jdbc.validationQuery=select 1" + "\r\n");
			sb.append("jdbc.driver=com.mysql.jdbc.Driver" + "\r\n");
			sb.append("jdbc.url=jdbc:mysql://" + serverip + ":" + serverport + "/" + dbname + "?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&createDatabaseIfNotExist=true" + "\r\n");
			sb.append("jpa.dialect=org.hibernate.dialect.MySQL5Dialect\r\n");
			break;
		case Sybase:
			sb.append("jdbc.validationQuery=select 1" + "\r\n");
			sb.append("jdbc.driver=net.sourceforge.jtds.jdbc.Driver\r\n");
			sb.append("jdbc.url=jdbc:jtds:sybase://" + serverip + ":" + serverport + "/" + dbname + "\r\n");
			sb.append("jpa.dialect=org.hibernate.dialect.SybaseDialect\r\n");
			break;
		case MSSQL:
			sb.append("jdbc.validationQuery=select 1" + "\r\n");
			sb.append("jdbc.driver=net.sourceforge.jtds.jdbc.Driver\r\n");
			sb.append("jdbc.url=jdbc:jtds:sqlserver://" + serverip + ":" + serverport + "/" + dbname + "\r\n");
			sb.append("jpa.dialect=org.hibernate.dialect.SQLServerDialect\r\n");
			break;
		case Oracle:
			sb.append("jdbc.validationQuery=select 1 from dual\r\n");
			sb.append("jdbc.driver=oracle.jdbc.driver.OracleDriver\r\n");
			sb.append("jdbc.url=jdbc:oracle:thin:@" + serverip + ":" + serverport + ":" + dbname + "\r\n");
			sb.append("jpa.dialect=org.hibernate.dialect.OracleDialect\r\n");
			break;
		case DB2:
			sb.append("jdbc.validationQuery=select 1 from sysibm.sysdummy1" + "\r\n");
			sb.append("jdbc.driver=com.ibm.db2.jcc.DB2Driver\r\n");
			sb.append("jdbc.url=jdbc:db2://" + serverip + ":" + serverport + "/" + dbname + "\r\n");
			sb.append("jpa.dialect=org.hibernate.dialect.DB2Dialect\r\n");
			break;
		}
		sb.append("jdbc.username=" + dbuser + "\r\n");
		sb.append("jdbc.password=" + dbpassword + "\r\n");
		return sb.toString();
	}

	/**
	 * 取类clazz所在的程序根目录
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getDomainPath(Class<?> clazz)
	{
		String domainPath = getPath(clazz);
		File file = new File(domainPath);
		if (!file.isDirectory())
		{
			domainPath = domainPath.substring(0, domainPath.length() - file.getName().length());
		}
		if (domainPath.endsWith("/") || domainPath.endsWith("\\"))
		{
			domainPath = domainPath.substring(0, domainPath.length() - 1);
		}
		String name = System.getProperty("os.name");
		if (name.toLowerCase().indexOf("windows") > -1)
		{
			domainPath = domainPath.substring(1, domainPath.length());
		}
		return domainPath;
	}

	/**
	 * 取clazz所在的目录或jar文件路径
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getPath(Class<?> clazz)
	{
		String path = "";
		Enumeration<Permission> permissions = clazz.getProtectionDomain().getPermissions().elements();
		while (permissions.hasMoreElements())
		{
			Permission permission = permissions.nextElement();
			if (permission instanceof FilePermission)
			{
				path = permission.getName();
				break;
			}
		}
		if (path.endsWith("-"))
		{
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	// 文件操作写入配置文件
	static Boolean writeJDBCFile(String config)
	{
		String path = getDomainPath(ConfigSetting.class);
		if (path.endsWith("lib"))
		{
			path = path.subSequence(0, path.length() - 3) + "classes";
		}
		String filepath = path + "/jdbc.properties";
		System.out.println("配置已经保存到：" + filepath);
		Boolean ret = false;
		// 文件是否存在
		File file = new File(filepath);
		if (file.exists())
		{
			// 如果存在则备份该文件。
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			String backfilename = filepath + format.format(new Date());
			File backfile = new File(backfilename);
			if (!file.renameTo(backfile))
			{
				System.out.println("备份文件失败：" + backfile);
			}
			else
			{
				System.out.println("备份文件成功：" + backfile);
			}
			file.delete();
		}
		try
		{
			if (!file.createNewFile())
			{
				System.out.println("新建配置文件失败");
			}
			else
			{

				FileOutputStream fos = new FileOutputStream(file);
				fos.write(config.getBytes());
				fos.close();
				ret = true;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			System.out.println("回车退出");
			System.in.read();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		return ret;
	}

	public static DBTypeEnum getDBTypeByString(String value)
	{
		value = value.toLowerCase();
		DBTypeEnum db = DBTypeEnum.MSSQL;
		if (value.equals("1"))
			db = DBTypeEnum.MSSQL;
		else if (value.equals("2"))
			db = DBTypeEnum.Sybase;
		else if (value.equals("3"))
			db = DBTypeEnum.Oracle;
		else if (value.equals("4"))
			db = DBTypeEnum.DB2;
		else if (value.equals("5"))
			db = DBTypeEnum.MySQL;
		return db;
	}

	static String getInputValue(byte[] buf, int len)
	{
		String retvalue = "";
		byte[] validbuf = new byte[len];
		for (int i = 0; i < len; i++)
			validbuf[i] = buf[i];
		try
		{
			retvalue = new String(validbuf, "utf-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		retvalue = retvalue.replace("\r", "");
		retvalue = retvalue.replace("\n", "");
		return retvalue;
	}
}
