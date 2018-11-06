package cc.cnplay.core.util;


public enum SystemType
{
	Windows, SunOS, Linux, Android, MacOS;

	public static SystemType current()
	{
		String name = System.getProperty("os.name");
		if (name.toLowerCase().indexOf("windows") > -1)
		{
			return SystemType.Windows;
		}
		else if (name.toLowerCase().indexOf("sunos") > -1)
		{
			return SystemType.SunOS;
		}
		else if (name.toLowerCase().indexOf("android") > -1)
		{
			return SystemType.Android;
		}
		else if (name.toLowerCase().indexOf("macos") > -1)
		{
			return SystemType.Android;
		}
		else
		{
			return SystemType.Linux;
		}
	}
}
