package cc.cnplay.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import cc.cnplay.core.annotation.Memo;

/**
 * 日期工具类
 * 
 * @author icc
 *
 */
public class DateUtil
{
	private final static Logger log = Logger.getLogger(DateUtil.class);

	public final static String DATE_FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";

	public final static String DATE_FORMAT_SHORT = "yyyy-MM-dd";

	public final static String DATE_FORMAT_TIME = "HH:mm:ss";

	public static long formatToLong(Date date, String formatStr)
	{
		if (date == null || formatStr == null)
		{
			return 0;
		}
		return Long.parseLong(new SimpleDateFormat(formatStr).format(date));
	}

	public static String format(Date date, DateFormat formatter)
	{
		if (date == null || formatter == null)
		{
			return null;
		}
		return formatter.format(date);
	}

	public static String format(Date date, String formatter)
	{
		if (date == null || formatter == null || formatter.isEmpty())
		{
			return null;
		}
		DateFormat format = new SimpleDateFormat(formatter);
		return format.format(date);
	}

	public static String format(Date date)
	{
		return format(date, new SimpleDateFormat(DATE_FORMAT_LONG));
	}

	public static String formatShort(Date date)
	{
		return format(date, new SimpleDateFormat(DATE_FORMAT_SHORT));
	}

	public static String formatTime(Date date)
	{
		return format(date, new SimpleDateFormat(DATE_FORMAT_TIME));
	}

	public static String format(long timeMillis, DateFormat formatter)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		return format(calendar.getTime(), formatter);
	}

	public static String format(long timeMillis)
	{
		return format(timeMillis, new SimpleDateFormat(DATE_FORMAT_LONG));
	}

	public static String formatShort(long timeMillis)
	{
		return format(timeMillis, new SimpleDateFormat(DATE_FORMAT_SHORT));
	}

	public static String formatTime(long timeMillis)
	{
		return format(timeMillis, new SimpleDateFormat(DATE_FORMAT_TIME));
	}

	public static Date parse(String dateStr, DateFormat formatter) throws ParseException
	{
		if (dateStr == null || dateStr.isEmpty() || formatter == null)
		{
			return null;
		}
		return formatter.parse(dateStr);
	}

	public static Date parsetDateTime(String dateStr)
	{
		try
		{
			return parse(dateStr, new SimpleDateFormat(DATE_FORMAT_LONG));
		}
		catch (ParseException e)
		{
			return new Date();
		}
	}

	public static Date parsetDate(String dateStr) throws ParseException
	{
		return parse(dateStr, new SimpleDateFormat(DATE_FORMAT_LONG));
	}

	public static Date parsetShortDate(String dateStr) throws ParseException
	{
		return parse(dateStr, new SimpleDateFormat(DATE_FORMAT_SHORT));
	}

	public static Date parseShortDate(String dateStr)
	{
		return parseDate(dateStr, new SimpleDateFormat(DATE_FORMAT_SHORT));
	}

	public static Date parseDate(String dateStr, String formatStr)
	{
		try
		{
			return parse(dateStr, new SimpleDateFormat(formatStr));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Date parseDate(String dateStr, DateFormat format)
	{
		try
		{
			return parse(dateStr, format);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Date parsetShortDate(String dateStr, DateFormat format) throws ParseException
	{
		return parse(dateStr, format);
	}

	public static Date parse(String dateStr, String format) throws ParseException
	{
		if (format == null || format.isEmpty())
		{
			return null;
		}
		DateFormat formatter = new SimpleDateFormat(format);
		return parse(dateStr, formatter);
	}

	public static long getTimeInMillis(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getTimeInMillis();
	}

	public static long getTimeInMillis()
	{
		return getTimeInMillis(new Date());
	}

	public static long getTimeMinus(Date arg1, Date arg2)
	{
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(arg1);
		calendar2.setTime(arg2);
		return calendar1.getTimeInMillis() - calendar2.getTimeInMillis();
	}

	public static boolean isTheSameDay(Date arg1, Date arg2)
	{
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(arg1);
		calendar2.setTime(arg2);
		if (calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR))
		{
			return false;
		}
		if (calendar1.get(Calendar.MONTH) != calendar2.get(Calendar.MONTH))
		{
			return false;
		}
		if (calendar1.get(Calendar.DATE) != calendar2.get(Calendar.DATE))
		{
			return false;
		}
		return true;
	}

	@Memo("db查询时，>=yyyy-MM-dd 00:00:00")
	public static Date dateStart(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	@Memo("db查询时，<=yyyy-MM-dd 23:59:59")
	public static Date dateEnd(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		return c.getTime();
	}

	@Memo("db查询时，>=yyyy-MM-dd 00:00:00")
	public static Date dateGreater(String dataStr)
	{
		String dStr = dataStr.substring(0, 10) + " 00:00:00";
		try
		{
			return parsetDate(dStr);
		}
		catch (ParseException e)
		{
			log.error("", e);
			return new Date();
		}
	}

	@Memo("db查询时，<=yyyy-MM-dd 23:59:59")
	public static Date dateLess(String dataStr)
	{
		String dStr = dataStr.substring(0, 10) + " 23:59:59";
		try
		{
			return parsetDate(dStr);
		}
		catch (ParseException e)
		{
			log.error("", e);
			return new Date();
		}
	}

	@Memo("date时间过去了day周")
	public static boolean isPassesDay(Date date, int day)
	{
		return isPassesDate(date, day * 7);
	}

	@Memo("date时间过去了dateNum天")
	public static boolean isPassesDate(Date date, int dateNum)
	{
		return isPassesHour(date, dateNum * 24);
	}

	@Memo("date时间过去了hour小时")
	public static boolean isPassesHour(Date date, int hour)
	{
		return isPassesMinutes(date, hour * 60);
	}

	@Memo("date时间过去了minutes分钟")
	public static boolean isPassesMinutes(Date date, int minutes)
	{
		return isPassesSecond(date, minutes * 60);
	}

	@Memo("date时间过去了second钞")
	public static boolean isPassesSecond(Date date, int second)
	{
		return (System.currentTimeMillis() - date.getTime()) > (second * 1000);
	}

	public static void main(String[] args)
	{

	}
}
