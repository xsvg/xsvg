package cc.cnplay.core.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

public abstract class SystemProperty
{

	public final static SystemType os = SystemType.current();
	public final static String os_arch = System.getProperty("os.arch");
	public final static String arch_data_model = System.getProperty("sun.arch.data.model");
	public final static String java_home = System.getProperty("java.home");
	public final static String class_path = System.getProperty("java.class.path");
	public final static String user_home = System.getProperty("user.home");
	public final static String file_separator = System.getProperty("file.separator");

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println(os);
		System.out.println(os_arch);
		System.out.println(arch_data_model);
		System.out.println(java_home);
		System.out.println(class_path);
		System.out.println(user_home);
		System.out.println(file_separator);
		// Map<?, ?> map = System.getProperties();
		// for (Object key : map.keySet())
		// {
		// System.out.println(key + "=" + map.get(key));
		// }
	}

	public static String getLocalMac() throws UnknownHostException
	{
		InetAddress ia = InetAddress.getLocalHost();
		return getMacAddress(ia);
	}

	public static String getMacAddress(InetAddress ia)
	{
		String mac = "";
		StringBuffer sb = new StringBuffer();
		try
		{
			NetworkInterface ni = NetworkInterface.getByInetAddress(ia);
			byte[] macs = ni.getHardwareAddress();
			for (int i = 0; i < macs.length; i++)
			{
				mac = Integer.toHexString(macs[i] & 0xFF);
				if (mac.length() == 1)
				{
					mac = '0' + mac;
				}
				sb.append(mac + "-");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		mac = sb.toString();
		mac = mac.substring(0, mac.length() - 1);
		return mac;
	}
}
