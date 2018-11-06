package cc.cnplay.platform;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import cc.cnplay.core.annotation.Memo;

/**
 * 常量接口
 * 
 * @author icc
 *
 */
public final class Constants
{

	@Memo("应用配置信息")
	public static ResourceBundle appBundle;

	@Memo("功能权限分隔符")
	public final static String RIGHT_SEPARATOR = "/";
	@Memo("extjs 插件相对路径")
	public final static String EXTJSPLUGINSPATH = File.separator + "plugins";

	public static String webappAbsolutePath;
	public static int FINGER_NUM_MAX = 4; // 用户保存最大指纹数

	public static String PASSWORD_DEFAULT = "111111";
	public static int AUTY_DEFAULT = 3;

	public static String LOGIN_TYPE_KEY = "loginByWeb";

	public static String RightFilter = "cnplay.platform.rightfilter";

	public static int Installed = 0;

	public static String InstalledMessage = "程序正在启动中...";

	public static final String expReportFmtConf = "cnplay.expReportFmtConf";
	
	public static String expReportFmt = ".pdf";
	
	public static int expReportMax = 50000;

	@Memo("extjs 插件绝对路径")
	public static String extjsPluginsAbsolutePath;

	public static ExecutorService executorService;

	static
	{
		try
		{
			executorService = Executors.newCachedThreadPool();
			appBundle = ResourceBundle.getBundle("application", Locale.ROOT);
			expReportFmt = getConfig(expReportFmtConf, expReportFmt);
//			ClassLoader cl = SystemLogAdvice.class.getClassLoader();
//			InputStream inputStream = null;
//			if (cl != null)
//			{
//				inputStream = cl.getResourceAsStream("application.properties");
//			}
//			else
//			{
//				inputStream = ClassLoader.getSystemResourceAsStream("application.properties");
//			}
//			java.util.logging.LogManager logManager = java.util.logging.LogManager.getLogManager();
//			logManager.readConfiguration(inputStream);
//			Properties properties = new Properties();
//			ResourceBundle bundle = Constants.appBundle;
//			Enumeration<String> keys = bundle.getKeys();
//			while (keys.hasMoreElements())
//			{
//				String key = keys.nextElement();
//				properties.put(key, bundle.getObject(key));
//			}
//			PropertyConfigurator.configure(properties);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	@Memo("取application配置信息")
	public static String getConfig(String key)
	{
		if (appBundle.containsKey(key))
		{
			return appBundle.getString(key);
		}
		return null;
	}

	@Memo("取application配置信息")
	public static String getConfig(String key, String defaultValue)
	{
		String value = "";
		if (appBundle.containsKey(key))
		{
			value = appBundle.getString(key);
		}
		if (StringUtils.isEmpty(value))
		{
			value = defaultValue;
		}
		return value;
	}
}
