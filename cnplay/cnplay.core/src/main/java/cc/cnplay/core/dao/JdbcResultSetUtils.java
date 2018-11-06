package cc.cnplay.core.dao;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import cc.cnplay.core.util.JdbcManager;

public class JdbcResultSetUtils extends JdbcManager
{

	protected final static Logger log = Logger.getLogger(JdbcResultSetUtils.class);

	public static Map<String, Vector<String>> toMap(ResultSet rs) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		Map<String, Vector<String>> map = new HashMap<String, Vector<String>>();
		while (rs.next())
		{
			for (int i = 0; i < rsmd.getColumnCount(); i++)
			{
				Vector<String> rows = map.get(rsmd.getColumnName(i + 1));
				if (rows == null)
				{
					rows = new Vector<String>();
					map.put(rsmd.getColumnName(i + 1), rows);
				}
				rows.add(rs.getString(i + 1));
			}
		}
		return map;
	}

	public static List<?> toList(Class<?> cls, List<Map<String, Object>> mapList) throws SQLException
	{
		List<Object> eList = new ArrayList<Object>();
		if (mapList == null)
		{
			return eList;
		}
		for (Map<String, Object> map : mapList)
		{
			Object entity = toEntity(cls, map);
			if (entity != null)
			{
				eList.add(entity);
			}
		}
		return eList;
	}

	public static Object toEntity(Class<?> cls, Map<String, Object> map) throws SQLException
	{
		Object entity = null;
		try
		{
			entity = cls.newInstance();
		}
		catch (Exception e)
		{
			throw new SQLException(e.getMessage(), e.fillInStackTrace());
		}
		PropertyDescriptor[] ps = null;
		try
		{
			ps = Introspector.getBeanInfo(entity.getClass()).getPropertyDescriptors();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		for (String column : map.keySet())
		{
			Object value = map.get(column);
			invoke(entity, ps, column, value);
		}
		return entity;

	}

	private static <T> void invoke(T entity, PropertyDescriptor[] ps, String column, Object value)
	{
		if (value != null && ps != null)
		{
			for (PropertyDescriptor pd : ps)
			{
				if (pd.getName().equals(column) && pd.getWriteMethod() != null)
				{
					try
					{
						pd.getWriteMethod().invoke(entity, value);
					}
					catch (Exception ex)
					{
						if (!invokeByEntityPropertyType(entity, column, value, pd))
						{
							log.warn(ex.getMessage());
						}
					}
				}
			}
		}
	}

	private static <T> boolean invokeByEntityPropertyType(T entity, String column, Object value, PropertyDescriptor pd)
	{
		try
		{
			Class<?> type = PropertyUtils.getPropertyType(entity, column);
			if (value instanceof Number)
			{
				if (type.equals(Integer.class) || type.equals(int.class))
				{
					pd.getWriteMethod().invoke(entity, ((Number) value).intValue());
				}
				else if (type.equals(Short.class) || type.equals(short.class))
				{
					pd.getWriteMethod().invoke(entity, ((Number) value).shortValue());
				}
				else if (type.equals(Long.class) || type.equals(long.class))
				{
					pd.getWriteMethod().invoke(entity, ((Number) value).longValue());
				}
				else if (type.equals(Float.class) || type.equals(float.class))
				{
					pd.getWriteMethod().invoke(entity, ((Number) value).floatValue());
				}
				else if (type.equals(Double.class) || type.equals(double.class))
				{
					pd.getWriteMethod().invoke(entity, ((Number) value).doubleValue());
				}
			}
			else if (type.equals(Boolean.class) || type.equals(boolean.class))
			{
				pd.getWriteMethod().invoke(entity, Boolean.parseBoolean(value.toString()));
			}
			else if (type.equals(String.class))
			{
				pd.getWriteMethod().invoke(entity, value.toString());
			}
			return true;
		}
		catch (Exception ex)
		{
			log.warn(ex.getMessage());
			return false;
		}
	}

	public static List<?> toList(Class<?> cls, ResultSet rs) throws SQLException
	{
		List<Object> eList = new ArrayList<Object>();
		if (rs == null)
		{
			return eList;
		}
		ResultSetMetaData rsmd = rs.getMetaData();
		PropertyDescriptor[] ps = null;
		try
		{
			ps = Introspector.getBeanInfo(cls).getPropertyDescriptors();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		while (rs.next())
		{
			Object entity = null;
			try
			{
				entity = cls.newInstance();
			}
			catch (Exception e)
			{
				throw new SQLException(e.getMessage(), e.fillInStackTrace());
			}
			for (int i = 0; i < rsmd.getColumnCount(); i++)
			{
				String column = rsmd.getColumnName(i + 1);
				Object value = rs.getObject(column);
				invoke(entity, ps, column, value);
			}
			eList.add(entity);

		}
		return eList;
	}

	public static <T> void setProperty(ResultSet rs, T entity, String column, Object value)
	{
		try
		{
			if (value != null)
			{
				Class<?> type = PropertyUtils.getPropertyType(entity, column);
				if (value instanceof Date)
				{
					BeanUtils.setProperty(entity, column, rs.getDate(column));
				}
				else if (type != null)
				{
					if (type.equals(value.getClass()))
					{
						BeanUtils.setProperty(entity, column, value);
					}
					else if (type.equals(Integer.TYPE) || type.equals(Integer.class))
					{
						BeanUtils.setProperty(entity, column, rs.getInt(column));
					}
					else if (type.equals(Long.TYPE) || type.equals(Long.class))
					{
						BeanUtils.setProperty(entity, column, rs.getLong(column));
					}
					else if (type.equals(Float.TYPE) || type.equals(Float.class))
					{
						BeanUtils.setProperty(entity, column, rs.getFloat(column));
					}
					else if (type.equals(Double.TYPE) || type.equals(Double.class))
					{
						BeanUtils.setProperty(entity, column, rs.getDouble(column));
					}
					else
					{
						log.warn("Data type is invalid {" + column + "=" + value + "} " + type.toString() + " != " + value.getClass().toString());
						BeanUtils.setProperty(entity, column, rs.getString(column));
					}
				}
				else
				{
					log.warn(entity.getClass().getName() + " not found column=" + column);
				}
			}
		}
		catch (Exception e)
		{
			log.error("{" + column + "=" + value + "} Exception： " + e.getMessage());
			e.printStackTrace();
		}
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
			conn = JdbcResultSetUtils.currentConnection();
			stmt = conn.createStatement();
			stmt.execute("use mysql");
			rs = stmt.executeQuery("select User,Password from user");
			Map<String, Vector<String>> map = toMap(rs);
			System.out.println(map);
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
			JdbcResultSetUtils.closeCurrent();
		}
	}
}
