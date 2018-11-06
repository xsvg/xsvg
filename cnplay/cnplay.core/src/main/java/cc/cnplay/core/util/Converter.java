package cc.cnplay.core.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cc.cnplay.core.annotation.Memo;

public abstract class Converter
{
	protected static Logger log = Logger.getLogger(Converter.class);

	public static void main(String[] args) throws Exception
	{
		SM3 sm3 = new SM3();

		System.out.println(sm3.encoding("111111"));
		String str = "测试123azAZ# 　⒈＃";
		String asc = Converter.toHexString(str);
		System.out.println(asc);
		System.out.println(Converter.hexToString(asc));
		test();
	}

	public static boolean checkSUM(String hexString)
	{
		byte[] buffer = Converter.hexToBytes(hexString);
		byte sum = 0;
		for (int i = 0; i < buffer.length - 1; i++)
		{
			sum = (byte) ((sum + buffer[i]) % 256);
		}
		if (buffer.length > 0)
		{
			return (sum == buffer[buffer.length - 1]);
		}
		return false;
	}

	public static String nextNumber(String lastNumberCode)
	{
		String nextLevelCode = "";
		String num = "";
		int len = 18;
		if (lastNumberCode.length() > len)
		{
			nextLevelCode = lastNumberCode.substring(0, lastNumberCode.length() - len);
			num = lastNumberCode.substring(lastNumberCode.length() - len, lastNumberCode.length());
			num = (Converter.parseLong(num) + 1) + "";
			num = StringUtils.leftPad(num, len, '0');
		}
		else
		{
			num = lastNumberCode;
			num = (Converter.parseLong(num) + 1) + "";
		}
		nextLevelCode = nextLevelCode + num;
		return nextLevelCode;
	}

	public static String createId(String name)
	{
		String hex = StringUtils.leftPad(Converter.toHexString(Converter.getBytes(name), ""), 32, "0");
		return hex.substring(hex.length() - 32);
	}

	public static void test()
	{
		try
		{
			// byte[] bfa = Converter.hexStringToBytes("fffe0102");
			byte[] bf = hexToBytes("ff fe 01 02");
			log.info("toHexString：" + toHexString(bf));
			log.info("Integer.parseInt：" + Integer.parseInt(toHexString(new byte[] { 0, 0, 01, 02 }).replace(" ", ""), 16));
			log.info("Integer.parseInt：" + Integer.parseInt(toHexString(new byte[] { 0, 0, (byte) 0xFF, (byte) 0xFF }).replace(" ", ""), 16));
			log.info("Converter.ToInt32：" + ToInt32(new byte[] { 01, 02, 0, 0 }, 0));
			int ia = (int) (Math.random() * (Math.random() * 100000));
			int ib = Converter.ToInt32(Converter.GetBytes(ia), 0);
			log.info("Converter.ToInt32：" + Converter.toHexString(Converter.GetBytes(ia)) + " & " + ia + "==" + ib + " " + (ia == ib));
			short sa = (short) (Math.random() * (Math.random() * 10000));
			short sb = Converter.ToInt16(Converter.GetBytes(sa), 0);
			log.info("Converter.ToInt16：" + Converter.toHexString(Converter.GetBytes(sa)) + " & " + sa + "==" + sb + " " + (sa == sb));
			long la = (long) (Math.random() * (Math.random() * 10000000));
			long lb = Converter.ToInt64(Converter.GetBytes(la), 0);
			log.info("Converter.ToInt64：" + Converter.toHexString(Converter.GetBytes(la)) + " & " + la + "==" + lb + " " + (la == lb));
			String tmp = "中文ABC";
			byte[] bytes = GetBytes(tmp);
			log.info("Converter：" + tmp + "=" + Converter.toHexString(bytes));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	public static String Encoding = "GBK";

	// public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public final static String dateFormat = "yyyy-MM-dd";

	public final static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

	public static byte[] readFile(String fileName)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileInputStream fis = new FileInputStream(fileName);
			int bytesRead = 0;
			int offSet = 0;
			byte[] buffer = new byte[100];
			while ((bytesRead = fis.read(buffer, offSet, buffer.length)) > 0)
			{
				baos.write(buffer, 0, bytesRead);
			}
			buffer = baos.toByteArray();
			fis.close();
			baos.close();
			return buffer;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return new byte[0];
	}

	@Memo("16进制字符串转为字符串")
	public static String hex2String(String hex)
	{
		return Converter.toString(Converter.hexToBytes(hex)).trim();
	}

	@Memo("16进制字符串转为byte数组 fe f0 ff")
	public static byte[] hexToBytes(String hexString)
	{
		hexString = hexString.replaceAll("0x", "");
		hexString = hexString.replaceAll(" ", "");
		// String[] tmps = hexString.split(" ");
		String[] tmps = new String[hexString.length() / 2];
		for (int i = 0; i < tmps.length; i++)
		{
			tmps[i] = hexString.substring(i * 2, i * 2 + 2);
		}
		ByteList list = new ByteList();
		for (String tmp : tmps)
		{
			if (tmp.trim().length() == 2)
			{
				list.add(Integer.valueOf(tmp, 16).byteValue());
			}
		}
		return list.ToArray();
	}

	@Memo("byte数组转为16进制字符串")
	public static String toHexString(byte[] bytes, String separator)
	{
		return toHexString(bytes, 0, bytes.length, separator);
	}

	@Memo("byte数组转为16进制字符串")
	public static String toHexString(byte[] bytes)
	{
		return toHexString(bytes, 0, bytes.length, " ");
	}

	@Memo("byte数组转为16进制字符串")
	public static String toHexString(byte[] bytes, int startIndex, int length)
	{
		return toHexString(bytes, startIndex, length, " ");
	}

	@Memo("byte数组转为16进制字符串")
	public static String toHexString(byte[] bytes, int startIndex, int length, String separator)
	{
		if (bytes == null || bytes.length == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		String tmp = "";
		for (int i = 0; i < length; i++)
		{
			tmp = Integer.toHexString(bytes[i + startIndex]).toUpperCase(Locale.getDefault());
			if (tmp.length() > 2)
			{
				tmp = tmp.substring(tmp.length() - 2, tmp.length());
			}
			else if (tmp.length() < 2)
			{
				tmp = "0" + tmp;
			}
			sb.append(separator + tmp);
		}
		return sb.toString();
	}

	@Memo("左边补字节0")
	public static byte[] leftPad(byte[] buffer, int size)
	{
		return leftPad(buffer, size, (byte) 0);
	}

	@Memo("左边补字节")
	public static byte[] leftPad(byte[] buffer, int size, byte b)
	{
		int len = buffer.length;
		if (len > size)
		{
			len = size;
		}
		byte[] bytes = new byte[size];
		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = b;
		}
		System.arraycopy(buffer, 0, bytes, size - len, len);
		return bytes;
	}

	@Memo("右边补字节0")
	public static byte[] rightPad(byte[] buffer, int size)
	{
		return rightPad(buffer, size, (byte) 0);
	}

	@Memo("右边补字节")
	public static byte[] rightPad(byte[] buffer, int size, byte b)
	{
		int len = buffer.length;
		if (len > size)
		{
			len = size;
		}
		byte[] bytes = new byte[size];
		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = b;
		}
		System.arraycopy(buffer, 0, bytes, 0, len);
		return bytes;
	}

	/**
	 * 全角空格为12288，半角空格为32 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248 转全角的函数(SBC case)
	 * 
	 * @param input
	 *            任意字符串
	 * @return 全角字符串
	 */
	public static String ToSBC(String input)
	{
		/**
		 * 半角转全角：
		 */
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++)
		{
			if (c[i] == 32)
			{
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] < 127)
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

	/**
	 * 转半角的函数(DBC case) 全角空格为12288，半角空格为32 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
	 * 
	 * @param input
	 *            任意字符串
	 * @return 半角字符串
	 */
	public static String ToDBC(String input)
	{
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++)
		{
			if (c[i] == 12288)
			{
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static byte[] GetBytes(byte[] bytes)
	{
		byte[] bf = new byte[bytes.length];
		for (int i = 0; i < bf.length; i++)
		{
			bf[i] = bytes[i];
		}
		return bf;
	}

	public static byte[] GetBytes(short num)
	{
		return GetBytes(num, 2);
	}

	public static byte[] GetBytes(int num)
	{
		return GetBytes(num, 4);
	}

	public static byte[] GetBytes(long num)
	{
		return GetBytes(num, 8);
	}

	/**
	 * 低位在前高位在后
	 * 
	 * @param num
	 * @param length
	 * @return
	 */
	public static byte[] GetBytes(long num, int length)
	{
		byte[] bytes = new byte[length];
		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = (byte) (num >> (i * 8));
		}
		return bytes;
	}

	/**
	 * 低位在前高位在后 默认方式
	 * 
	 * @param num
	 * @param length
	 * @return
	 */
	private static long ToInt64(byte[] bytes, int index, int length)
	{
		long num = 0;
		for (int i = 0; i < length; i++)
		{
			long l = (bytes[index + i]) & 0xFF;
			num |= (l << (i * 8));
		}
		return num;
	}

	/**
	 * 高位在前低位在后
	 * 
	 * @param bytes
	 * @param index
	 * @param length
	 * @return
	 */
	public static long toInt64(byte[] bytes, int index, int length)
	{
		long num = 0;
		for (int i = 0; i < length; i++)
		{
			long l = (bytes[index + i]) & 0xFF;
			num |= (l << ((length - 1 - i) * 8));
		}
		return num;
	}

	/**
	 * 高位在前低位在后
	 * 
	 * @param num
	 * @param length
	 * @return
	 */
	public static byte[] getBytes(long num, int length)
	{
		byte[] b = new byte[length];
		for (int i = 0; i < b.length; i++)
		{
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((num >>> offset) & 0xFF);
		}
		return b;
	}

	public static short ToInt16(byte[] bytes, int index)
	{
		return (short) ToInt64(bytes, index, 2);
	}

	public static int ToInt32(byte[] bytes, int index)
	{
		return (int) ToInt64(bytes, index, 4);
	}

	public static long ToInt64(byte[] bytes, int index)
	{
		return ToInt64(bytes, index, 8);
	}

	public static byte[] getBytes(String name)
	{
		try
		{
			if (name == null)
			{
				return new byte[0];
			}
			return name.getBytes(Encoding);
		}
		catch (UnsupportedEncodingException e)
		{
			log.error(e.getMessage(), e);
			return new byte[0];
		}
	}

	public static byte[] GetBytes(String name) throws UnsupportedEncodingException
	{
		if (name == null)
		{
			return new byte[0];
		}
		return name.getBytes(Encoding);
	}

	@Memo("16进制字符串转为byte数组 fe f0 ff")
	public static byte[] hexStringToBytes(String hexString)
	{
		if (hexString == null || hexString.equals(""))
		{
			return new byte[0];
		}
		hexString = hexString.replaceAll("0x", "");
		hexString = hexString.replaceAll(" ", "");
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++)
		{
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}

	private static byte charToByte(char c)
	{
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static byte[] getBytes(String name, int len)
	{
		byte[] buffer = getBytes(name);
		if (buffer.length > len)
		{
			ByteList bl = new ByteList();
			bl.addRange(buffer);
			bl.RemoveRange(len, buffer.length - len);
			buffer = bl.ToArray();
		}
		else if (buffer.length < len)
		{
			ByteList bl = new ByteList();
			bl.addRange(buffer);
			bl.addRange(new byte[len - buffer.length]);
			buffer = bl.ToArray();
		}
		return buffer;
	}

	public static String toString(byte[] bytes)
	{
		try
		{
			if (bytes == null)
			{
				return null;
			}
			return new String(bytes, Encoding);
		}
		catch (UnsupportedEncodingException e)
		{
			log.error(e.getMessage(), e);
			return "";
		}
	}

	public static byte[] asByteArray(UUID uuid)
	{
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		byte[] buffer = new byte[16];
		for (int i = 0; i < 8; i++)
		{
			buffer[i] = (byte) (msb >>> 8 * (7 - i));
		}
		for (int i = 8; i < 16; i++)
		{
			buffer[i] = (byte) (lsb >>> 8 * (7 - i));
		}
		return buffer;
	}

	public static UUID toUUID(byte[] buffer)
	{
		return toUUID(buffer, 0);
	}

	public static UUID toUUID(byte[] buffer, int startIndex)
	{
		long msb = 0;
		long lsb = 0;
		for (int i = startIndex; i < startIndex + 8; i++)
			msb = (msb << 8) | (buffer[i] & 0xff);
		for (int i = startIndex + 8; i < startIndex + 16; i++)
			lsb = (lsb << 8) | (buffer[i] & 0xff);
		UUID result = new UUID(msb, lsb);
		return result;
	}

	public static int parseInt(String text)
	{
		try
		{
			return Integer.parseInt(text);
		}
		catch (Exception ex)
		{
			return 0;
		}
	}

	public static long parseLong(String text)
	{
		try
		{
			return Long.parseLong(text);
		}
		catch (Exception ex)
		{
			return 0;
		}
	}

	public static String throwableToString(Throwable tr)
	{
		if (tr == null)
		{
			return "";
		}
		// tr.printStackTrace();
		Throwable t = tr;
		while (t != null)
		{
			if (t instanceof UnknownHostException)
			{
				return "";
			}
			t = t.getCause();
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		return sw.toString();
	}

	public static String toHexString(String text)
	{
		byte[] bytes;
		try
		{
			bytes = text.getBytes(Encoding);
			return toHexString(bytes, "");
		}
		catch (Exception e)
		{
			log.warn(e.getMessage());
			return "";
		}
	}

	public static String hexToString(String hex) throws UnsupportedEncodingException
	{
		byte[] bytes = hexStringToBytes(hex);
		return new String(bytes, Encoding);
	}
}
