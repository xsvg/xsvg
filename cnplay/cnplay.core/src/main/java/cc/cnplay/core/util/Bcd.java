package cc.cnplay.core.util;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import cc.cnplay.core.annotation.Memo;

public class Bcd
{
	public static void main(String[] args)
	{
		byte[] buffer = toWeekBCD(new Date());
		System.out.println(Converter.toHexString(buffer));
		buffer = toBCD(new Date());
		System.out.println(Converter.toHexString(buffer));
	}

	/**
	 * 转换为BCD码
	 * 
	 * @param n
	 * @return
	 */
	private static byte toBCD(int n)
	{
		n = (n % 100);
		return (byte) (((n / 10) << 4) | (n % 10));
	}

	/**
	 * 转换 BCD 码为字节码
	 * 
	 * @param value
	 *            BCD码
	 * @return 返回转换后的字节码
	 */
	private static byte BCDToByte(byte value)
	{
		return (byte) (value / 0x10 * 10 + value % 0x10);
	}

	@Memo("转换为BCD码")
	public static byte[] str2BCD(String s)
	{
		if (s.length() % 2 != 0)
		{
			s = "0" + s;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		char[] cs = s.toCharArray();
		for (int i = 0; i < cs.length; i += 2)
		{
			int high = cs[i] - 48;
			int low = cs[i + 1] - 48;
			baos.write(high << 4 | low);
		}
		return baos.toByteArray();
	}

	@Memo("BCD码转换为字符串")
	public static String bcd2String(byte[] b)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
		{
			int h = ((b[i] & 0xff) >> 4) + 48;
			sb.append((char) h);
			int l = (b[i] & 0x0f) + 48;
			sb.append((char) l);
		}
		return sb.toString();
	}

	@Memo("BCD码转换为日期")
	public static Date BCDToDateTime(byte[] buffer) throws ParseException
	{
		GregorianCalendar clc = new GregorianCalendar();
		clc.setTime(new Date());
		int year = (clc.get(Calendar.YEAR) - clc.get(Calendar.YEAR) % 100) + BCDToByte(buffer[0]);
		int month = BCDToByte(buffer[1]);
		int date = BCDToByte(buffer[2]);
		int hour = BCDToByte(buffer[3]);
		int minute = BCDToByte(buffer[4]);
		int second = BCDToByte(buffer[5]);
		clc.set(year, month - 1, date, hour, minute, second);
		String time = DateUtil.format(clc.getTime(), "yyyy-MM-dd HH:mm:ss");
		return DateUtil.parse(time, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 时间转为BCD码：年，月，日，时，分，秒
	 * 
	 * @param dateTime
	 *            时间
	 * @return BCD码：年，月，日，时，分，秒
	 */
	@Memo("时间转为BCD码：年，月，日，时，分，秒")
	public static byte[] toBCD(Date date)
	{
		ByteList bytes = _toWeekBCD(date);
		bytes.remove(0);
		return bytes.ToArray();
	}

	/**
	 * 时间转为BCD码：星期，年，月，日，时，分，秒
	 * 
	 * @param date
	 *            时间
	 * @return BCD码：星期，年，月，日，时，分，秒
	 */
	@Memo("时间转为BCD码：星期，年，月，日，时，分，秒")
	public static byte[] toWeekBCD(Date date)
	{
		return _toWeekBCD(date).ToArray();
	}

	private static ByteList _toWeekBCD(Date date)
	{
		GregorianCalendar clc = new GregorianCalendar();
		clc.setTime(date);
		ByteList buffer = new ByteList();
		buffer.add(toBCD(clc.get(Calendar.DAY_OF_WEEK) - 1));
		buffer.add(toBCD(clc.get(Calendar.YEAR)));
		buffer.add(toBCD(clc.get(Calendar.MONTH) + 1));
		buffer.add(toBCD(clc.get(Calendar.DATE)));
		buffer.add(toBCD(clc.get(Calendar.HOUR_OF_DAY)));
		buffer.add(toBCD(clc.get(Calendar.MINUTE)));
		buffer.add(toBCD(clc.get(Calendar.SECOND)));
		return buffer;
	}

	public static String getWeekDay(Date date)
	{
		try
		{
			SimpleDateFormat formatD = new SimpleDateFormat("E", Locale.ROOT);// "E"表示"day in week"
			return formatD.format(date);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
}
