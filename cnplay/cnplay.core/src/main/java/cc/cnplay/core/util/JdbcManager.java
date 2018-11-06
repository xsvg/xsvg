package cc.cnplay.core.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class JdbcManager
{

	protected final static Logger log = Logger.getLogger(JdbcManager.class);
	public static final ThreadLocal<Connection> connectionPool = new ThreadLocal<Connection>();
	protected static JdbcConfig config;
	static
	{
		try
		{
			config = new JdbcConfig("jdbc");
		}
		catch (Exception ex)
		{
			log.warn(ex.getMessage());
		}
	}

	/**
	 * 创建当前线程连接
	 * 
	 * @return
	 */
	public static Connection currentConnection()
	{
		Connection conn = connectionPool.get();
		try
		{
			if (null == conn || conn.isClosed())
			{
				conn = createConnection(config);
				connectionPool.set(conn);
				log.debug("[" + Thread.currentThread().getName() + "]Open：" + conn);
			}
		}
		catch (SQLException ex)
		{
			log.error("打开数据库连接失败！" + ex.getMessage());
			conn = null;
		}
		return conn;
	}

	/**
	 * 关闭当前线程连接
	 */
	public static void closeCurrent()
	{
		Connection conn = connectionPool.get();
		if (null != conn)
		{
			connectionPool.set(null);
			try
			{
				log.debug("[" + Thread.currentThread().getName() + "]Close：" + conn);
				conn.close();
			}
			catch (SQLException ex)
			{
				log.error(ex.getMessage(), ex);
			}
		}
	}

	public static synchronized Connection connection() throws SQLException
	{
		try
		{
			log.debug(config.getDriver());
			Class.forName(config.getDriver());
		}
		catch (Exception e)
		{
			throw new SQLException(e.getMessage(), e.getCause());
		}
		return DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
	}

	public static synchronized Connection createConnection(JdbcConfig JdbcConfig) throws SQLException
	{
		try
		{
			log.debug(JdbcConfig.getDriver());
			Class.forName(JdbcConfig.getDriver());
		}
		catch (Exception e)
		{
			throw new SQLException(e.getMessage(), e.getCause());
		}
		return DriverManager.getConnection(JdbcConfig.getUrl(), JdbcConfig.getUsername(), JdbcConfig.getPassword());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = JdbcManager.currentConnection();
			stmt = conn.createStatement();
			stmt.execute("use mysql");
			rs = stmt.executeQuery("select User,Password from user");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			JdbcManager.closeCurrent();
		}
	}

	public static JdbcConfig getConfig()
	{
		return config;
	}

	public static void setConfig(JdbcConfig config)
	{
		JdbcManager.config = config;
	}
	
	
}
