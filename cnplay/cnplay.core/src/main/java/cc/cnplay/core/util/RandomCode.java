package cc.cnplay.core.util;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Random;

public class RandomCode
{
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	protected static final String DIGITS62 = "0123456789ABCDEF";

	public static String getCode()
	{
		String code = doCode();
		while (code.length() != decode(code).length())
		{
			code = doCode();
		}
		return code;
	}

	private static String doCode()
	{
		Random r = new Random();
		String rd = "" + (int) (r.nextDouble() * 100000);
		while (rd.length() < 8)
		{
			rd = rd + "0";
		}
		int start = 0;
		while (start == 0)
		{
			start = (int) (r.nextDouble() * 10);
		}
		int end = start;
		while (start == end || end == 0)
		{
			end = (int) (r.nextDouble() * 10);
		}
		rd = start + rd + end;
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		StringBuffer sb = new StringBuffer(rd);
		int a = hour % 10;
		sb.insert(start, a);
		int b = hour / 10;
		sb.insert(end, b);
		return sb.toString();
	}

	public static String decode(String code)
	{
		int start = Integer.parseInt(code.substring(0, 1));
		String tmp = code;
		while (start-- > 0)
		{
			tmp = encode(tmp);
		}
		return tmp;
	}

	protected static String decodeMd5(String code)
	{
		int start = Integer.parseInt(code.substring(0, 1));
		String tmp = code;
		while (start-- > 0)
		{
			tmp = md5(tmp);
		}
		return tmp;
	}

	public static boolean check(String code)
	{
		int hour = getHour(code);
		if (hour < 0 || hour > 23)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public static int getHour(String code)
	{
		try
		{
			int start = Integer.parseInt(code.substring(0, 1));
			int end = Integer.parseInt(code.substring(11, 12));
			if (end < start)
			{
				start = start + 1;
			}
			int hour = Integer.parseInt(code.substring(end, end + 1) + code.substring(start, start + 1));
			return hour;
		}
		catch (Throwable ex)
		{
			System.out.println(ex.getMessage());
			return -1;
		}
	}

	protected static void println(Object hour)
	{
		System.out.println(hour);
	}

	public static boolean verifyCode(String code, String decode)
	{
		return verify(code, decode) >= 0;
	}

	public static int verify(String code, String decode)
	{
		try
		{
			int hour = getHour(code);
			if (hour < 0 || hour > 23)
			{
				return -1;
			}
			int hourCurrent = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			int hourValid = hourCurrent - hour;
			if (hourValid == 0 || hourValid == 1 || hourValid == -23)
			{
				int start = Integer.parseInt(code.substring(0, 1));
				String tmp = code;
				while (start-- > 0)
				{
					tmp = encode(tmp);
				}
				if (decode.equals(tmp))
				{
					return hour;
				}
				else
				{
					start = Integer.parseInt(code.substring(0, 1));
					String temp = code;
					while (start-- > 0)
					{
						temp = md5(temp);
					}
					if (decode.equals(temp))
					{
						return hour;
					}
					else
					{
						return -3;
					}
				}
			}
			else
			{
				return -2;
			}
		}
		catch (Throwable ex)
		{
			System.out.println(ex.getMessage());
			return -1;
		}
	}

	private static String getFormattedText(final byte[] bytes)
	{
		final StringBuilder buf = new StringBuilder(bytes.length * 2);

		for (int j = 0; j < bytes.length; j++)
		{
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}

	private static String encode(String code)
	{
		int start = Integer.parseInt(code.substring(0, 1));
		long codeNum = Long.parseLong(code);
		String tmp = code;
		long hex = Long.parseLong(tmp, 16);
		while (start-- > 0 && hex != 0)
		{
			tmp = "" + (((hex % Long.parseLong(tmp)) | codeNum) & codeNum);
			hex = Long.parseLong(tmp, 16);
		}
		return tmp;
	}

	private static String md5(String code)
	{
		String tmp = code;
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(code.getBytes());
			tmp = getFormattedText(messageDigest.digest());
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		return tmp;
	}

	public static void main(String args[])
	{
		int i = 1;
		while (i-- > 0)
		{
			String code = RandomCode.getCode();
			System.err.println("en=" + code);
			String decode = RandomCode.decode(code);
			System.err.println("de=" + decode);
			System.err.println(RandomCode.verify(code, decode));
			decode = RandomCode.decodeMd5(code);
			System.err.println("de=" + decode);
			System.err.println(RandomCode.verify(code, decode));
		}
	}
}
