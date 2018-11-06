package cc.cnplay.core.util;

import java.io.File;
import java.io.FilePermission;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

import cc.cnplay.core.annotation.Memo;

/**
 * 
 * @author peixere@qq.com
 * 
 */
public class LoaderUtils
{
	private static final Logger log = Logger.getLogger(LoaderUtils.class);
	private static final Method addURL = initAddMethod();

	private static final URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

	/**
	 * 初始化addUrl 方法.
	 * 
	 * @return 可访问addUrl方法的Method对象
	 */
	private static Method initAddMethod()
	{
		try
		{
			Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
			add.setAccessible(true);
			return add;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * 加载jar classpath。
	 * 
	 * @param filepath
	 */
	public static void addClasspath(String filepath)
	{
		File file = new File(filepath);
		loopFiles(file);
	}

	/**
	 * 资源文件只加载路径
	 * 
	 * @param filepath
	 */
	public static void addResourceDir(String filepath)
	{
		File file = new File(filepath);
		loopDirs(file);
	}

	/**
	 * 循环遍历目录，找出所有的资源路径。
	 * 
	 * @param file
	 *            当前遍历文件
	 */
	private static void loopDirs(File file)
	{
		// 资源文件只加载路径
		if (file.isDirectory())
		{
			addURL(file);
			File[] tmps = file.listFiles();
			if (tmps != null)
				for (File tmp : tmps)
				{
					loopDirs(tmp);
				}
		}
	}

	/**
	 * 循环遍历目录，找出所有的jar包。
	 * 
	 * @param file
	 *            当前遍历文件
	 */
	private static void loopFiles(File file)
	{
		if (file.isDirectory())
		{
			File[] tmps = file.listFiles();
			if (tmps != null)
				for (File tmp : tmps)
				{
					loopFiles(tmp);
				}
		}
		else
		{
			if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip"))
			{
				addURL(file);
			}
		}
	}

	/**
	 * 通过filepath加载文件到classpath。
	 * 
	 * @param filePath
	 *            文件路径
	 * @return URL
	 * @throws Exception
	 *             异常
	 */
	private static void addURL(File file)
	{
		try
		{
			addURL.invoke(classloader, new Object[] { file.toURI().toURL() });
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
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
		if (SystemType.Windows == SystemType.current())
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

	/**
	 * 从iClazz所在jar中取出iClazz的子类
	 * 
	 * @param iClazz
	 * @param fs
	 * @return
	 */
	public static <T> List<Class<T>> classLoader(Class<T> iClazz)
	{
		String jarPath = getPath(iClazz);
		try
		{
			return classLoader(iClazz, new JarFile(jarPath));
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			return new ArrayList<Class<T>>();
		}
	}

	/**
	 * 从jar中取出iClazz的子类
	 * 
	 * @param iClazz
	 * @param fs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<Class<T>> classLoader(Class<T> iClazz, JarFile... jfs)
	{
		List<Class<T>> clazzList = new ArrayList<Class<T>>();

		for (JarFile jar : jfs)
		{
			Enumeration<?> enumJar = jar.entries();
			while (enumJar.hasMoreElements())
			{
				JarEntry entry = (JarEntry) enumJar.nextElement();
				if (!entry.isDirectory())
				{
					String className = entry.getName();
					if (!isNullOrEmpty(className) && className.endsWith(".class") && className.indexOf("$") == -1)
					{
						className = className.substring(0, className.lastIndexOf(".class")).replaceAll("/", ".");
						try
						{
							Class<?> clazz = Class.forName(className);
							if (!Modifier.isAbstract(clazz.getModifiers()) && isAssignableFrom(clazz, iClazz))
							{
								clazzList.add((Class<T>) clazz);
							}
						}
						catch (NoClassDefFoundError e)
						{
							log.warn(e.getClass().getName() + ": " + e.getMessage());
						}
						catch (UnsatisfiedLinkError e)
						{
							log.warn(e.getClass().getName() + ": " + e.getMessage());
						}
						catch (Throwable ex)
						{
							log.error(ex.getMessage(), ex);
						}
					}
				}
			}
		}
		return clazzList;
	}

	private static boolean isNullOrEmpty(String str)
	{
		if (str == null || str.trim().equals(""))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private static boolean isAssignableFrom(Class<?> clazz, Class<?> iClazz)
	{
		if (iClazz.isAssignableFrom(clazz))
		{
			return true;
		}
		else if (clazz.getSuperclass() != null)
		{
			if (iClazz.isAssignableFrom(clazz.getSuperclass()))
			{
				return true;
			}
			else
			{
				return isAssignableFrom(clazz.getSuperclass(), iClazz);
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * 从dir目录中取出iClazz的子类
	 * 
	 * @param iClazz
	 * @param fs
	 * @return
	 * @throws Exception
	 */
	public static <T> List<Class<T>> classLoader(Class<T> iClazz, String dir)
	{
		List<Class<T>> clazzList = new ArrayList<Class<T>>();
		try
		{
			classLoader(clazzList, iClazz, new File(dir));
		}
		catch (Throwable e)
		{
			log.error(e.getMessage(), e);
		}
		return clazzList;
	}

	private static <T> void classLoader(List<Class<T>> clazzList, Class<T> iClazz, File dir) throws Exception
	{
		if (dir.isDirectory())
		{
			File[] files = dir.listFiles();
			if (files != null)
			{
				for (File f : files)
				{
					if (f.isFile())
					{
						if (f.getAbsolutePath().endsWith(".jar"))
						{
							// addURL(f);
							clazzList.addAll(classLoader(iClazz, new JarFile(f)));
						}
					}
					else if (f.isDirectory())
					{
						classLoader(clazzList, iClazz, f);
					}
				}
			}
		}
		else
		{
			if (dir.isFile() && dir.getAbsolutePath().endsWith(".jar"))
			{
				clazzList.addAll(classLoader(iClazz, new JarFile(dir)));
			}
		}
	}

	@Memo("实例化类，异常返回空")
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz)
	{
		return (T) newInstance(clazz.getName());
	}

	@Memo("实例化类，异常返回空")
	public static Object newInstance(String clazzName)
	{
		try
		{
			return Class.forName(clazzName).newInstance();
		}
		catch (Throwable ex)
		{
			log.error(null, ex);
			return null;
		}
	}

	public static void main(String[] args)
	{
		// File fs = new File("c:\\core.jar");
		System.out.println(getDomainPath(java.util.Date.class));
	}
}
