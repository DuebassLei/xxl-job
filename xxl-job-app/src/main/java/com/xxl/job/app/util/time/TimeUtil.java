package com.xxl.job.app.util.time;



import com.xxl.job.app.util.BeanUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
/**
 * 时间操作类
 *
 * @author  DuebassLei
 * @version 1.0
 * @date    2021/7/11
 *
 */
public class TimeUtil {
	public final static int SECOND = 0;
	public final static int MINUTE = 1;
	public final static int HOUR = 2;
	public final static int DAY = 3;
	public final static int MONTH = 4;

	/**
	 * 将字符串转固定格式的日期类型
	 * 
	 * @param value
	 * @param format
	 * @return
	 */
	public static LocalDateTime convertString(String value, String format) {
		DateTimeFormatter sdf = DateTimeFormatter.ofPattern(format);
		if (value == null) {
			return null;
		}
		try {
			return LocalDateTime.parse(value, sdf);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将字符串转日期类型，格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @param value
	 * @return
	 */
	public static LocalDateTime convertString(String value) {
		return convertString(value, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间，例如"2002-11-06 17:08:59"
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return LocalDateTime.now().format(formatter);
	}

	/**
	 * 根据长整型毫秒数返回时间形式:如:2007-01-23 13:45:21
	 * 
	 * @param millseconds
	 * @return
	 */
	public static String getDateTimeString(long millseconds) {

		return getDate(millseconds, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 取得yyyyMMdd格式的时间
	 * 
	 * @param time
	 * @return
	 */
	public static String getDayDate(long time) {
		return getDate(time, "yyyyMMdd");
	}

	public static String getDate(long time, String format) {
		SimpleDateFormat formater = new SimpleDateFormat(format);
		return formater.format(new Date(time));
	}

	/**
	 * 取得时间
	 *
	 * @param date
	 * @return
	 */
	public static String getDateTimeString(LocalDateTime date) {
		return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * 取得时间
	 *
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDateTimeString(LocalDateTime date, String format) {
		return date.format(DateTimeFormatter.ofPattern(format));
	}

	/**
	 * 比较两个时间大小 结束时间是否大于开始时间
	 *
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean isTimeLarge(String startTime, String endTime) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			ParsePosition pos1 = new ParsePosition(0);
			Date dt1 = formatter.parse(startTime, pos);
			Date dt2 = formatter.parse(endTime, pos1);
			long lDiff = dt2.getTime() - dt1.getTime();
			return lDiff > 0;
		} catch (Exception e) {

			return false;
		}
	}

	/**
	 * 获取两个时间段的间隔 ms
	 * @param endTime 结束时间
	 * @param startTime 开始时间
	 * @return
	 */
	public static long getTime( LocalDateTime endTime,LocalDateTime startTime) {
		ZoneId zone = ZoneId.systemDefault();
		Instant sinstant = startTime.atZone(zone).toInstant();
		Instant einstant = endTime.atZone(zone).toInstant();
		return einstant.toEpochMilli() - sinstant.toEpochMilli();
	}

	/**
	 * 比较两个时间取得两个时间差
	 *
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String getTimeDiff(String startTime, String endTime) {
		try {
			String tmp = "";
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			ParsePosition pos1 = new ParsePosition(0);
			Date dt1 = formatter.parse(startTime, pos);
			Date dt2 = formatter.parse(endTime, pos1);
			long lDiff = dt2.getTime() - dt1.getTime();
			int days = (int) (lDiff / (1000 * 60 * 60 * 24));
			if (days > 0) {
				lDiff = lDiff - days * 1000 * 60 * 60 * 24;
				tmp += days + "天";
			}
			int hours = (int) (lDiff / (1000 * 60 * 60));
			if (hours > 0) {
				lDiff = lDiff - hours * 1000 * 60 * 60;
			}
			tmp += hours + "小时";
			int minute = (int) (lDiff / (1000 * 60));
			tmp += minute + "分钟";
			return tmp;
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
	}

	/**
	 * 根据长整形的毫秒数返回字符串类型的时间段
	 *
	 * @param millseconds
	 * @return
	 */
	public static String getTime(Long millseconds) {
		String time = "";
		if (millseconds == null) {
			return "";
		}
		int days = (int) (long) millseconds / 1000 / 60 / 60 / 24;
		if (days > 0) {
			time += days + "天";
		}
		long hourMillseconds = millseconds - days * 1000 * 60 * 60 * 24;
		int hours = (int) hourMillseconds / 1000 / 60 / 60;
		if (hours > 0) {
			time += hours + "小时";
		}
		long minuteMillseconds = millseconds - days * 1000 * 60 * 60 * 24
				- hours * 1000 * 60 * 60;
		int minutes = (int) minuteMillseconds / 1000 / 60;
		if (minutes > 0) {
			time += minutes + "分钟";
		}
		return time;
	}

	/**
	 * 取得日期
	 *
	 * @param date
	 * @return
	 */
	public static String getDateString(LocalDateTime date) {
		if (date != null) {
			return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		return "";
	}

	/**
	 * 取得日期
	 *
	 * @param date
	 * @return
	 */
	public static String getDateString(LocalDateTime date, String format) {
		if (date != null) {
			return date.format(DateTimeFormatter.ofPattern(format));
		}
		return "";
	}

	public static String getCurrentDate(String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.now().format(formatter);
	}

	/**
	 * 得到当前日期，例如"2002-11-06"
	 *
	 * @return
	 */
	public static String getCurrentDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return LocalDateTime.now().format(formatter);
	}

	/**
	 * 根据长整型毫秒数返回日期形式:如:2007-01-23
	 *
	 * @param millseconds
	 * @return
	 */
	public static String getDateString(long millseconds) {

		Instant instant = Instant.ofEpochMilli(millseconds);
		ZoneId zone = ZoneId.systemDefault();
		return LocalDateTime.ofInstant(instant, zone).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	/**
	 * 根据指定格式取得日期字符格式。
	 *
	 * @param formater
	 * @return
	 */
	public static String getDateString(String formater) {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	/**
	 * 取得当前日期的毫秒数
	 *
	 * @return
	 */
	public static long getMillsByToday() {
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nowDate = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0, 0);
	    Instant instant = nowDate.atZone(zone).toInstant();
	    return instant.toEpochMilli();
	}

	/**
	 * 取得多少天后的数据。
	 *
	 * @param days
	 * @return
	 */
	public static long getNextDays(int days) {
		LocalDateTime nextDate = LocalDateTime.now().plusDays(days);
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = nextDate.atZone(zone).toInstant();
		return instant.toEpochMilli();
	}

	/**
	 * 取得下一天。
	 *
	 * @param date
	 * @param days
	 * @return
	 */
	public static LocalDateTime getNextDays(LocalDateTime date, int days) {
		return date.plusDays(days);
	}

	/**
	 * 根据给出的日期字符串返回该日期的长整型毫秒数(字符串前后空格不计) 如果strDate的格式不正确将返回0
	 *
	 * @param strDate
	 *            形如:2007-01-23的日期字符串
	 * @return
	 */
	public static long getMillsByDateString(String strDate) {
		Calendar cal = Calendar.getInstance();
		if (strDate != null && strDate.trim().length() > 9) {
			strDate = strDate.trim();
			try {
				int year = Integer.parseInt(strDate.substring(0, 4));
				int month = Integer.parseInt(strDate.substring(5, 7)) - 1;
				int date = Integer.parseInt(strDate.substring(8, 10));
				cal.set(year, month, date, 0, 0, 0);
				String str = String.valueOf(cal.getTimeInMillis());
				return Long
						.parseLong((str.substring(0, str.length() - 3) + "000"));

			} catch (Exception e) {

			}
		}

		return 0;
	}

	/**
	 * 根据给出的时间字符串返回该时间的长整型毫秒数(字符串前后空格不计) 如果strDateTime的格式不正确将返回0
	 *
	 * @param strDateTime
	 *            形如:2007-01-23 13:45:21的时间字符串
	 * @return
	 */
	public static long getMillsByDateTimeString(String strDateTime) {
		Calendar cal = Calendar.getInstance();
		if (strDateTime != null && strDateTime.trim().length() > 18) {
			strDateTime = strDateTime.trim();
			try {
				int year = Integer.parseInt(strDateTime.substring(0, 4));
				int month = Integer.parseInt(strDateTime.substring(5, 7)) - 1;
				int date = Integer.parseInt(strDateTime.substring(8, 10));
				int hour = Integer.parseInt(strDateTime.substring(11, 13));
				int minute = Integer.parseInt(strDateTime.substring(14, 16));
				int second = Integer.parseInt(strDateTime.substring(17, 19));
				cal.set(year, month, date, hour, minute, second);
				return cal.getTimeInMillis();
			} catch (Exception e) {

			}
		}

		return 0;
	}

	/**
	 * 根据长整型毫秒数返回时间形式:
	 *
	 * @param millseconds
	 * @return
	 */
	public static String getFormatString(long millseconds, String format) {
		if (format == null || format.trim().length() == 0) {
			format = "yyyy-MM-dd";
		}
		format = format.trim();
		return new SimpleDateFormat(format).format(new Date(
				millseconds));
	}

	/**
	 * 得到当前的毫秒时间
	 *
	 * @return
	 */
	public static long getCurrentTimeMillis() {
		Calendar c = Calendar.getInstance();
		return c.getTimeInMillis();
	}

	/**
	 * 得到日期的毫秒时间
	 *
	 * @return
	 */
	public static long getTimeMillis(LocalDateTime date) {
		if(BeanUtils.isEmpty(date)){
			return 0;
		}
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = date.atZone(zone).toInstant();
		return instant.toEpochMilli();
	}

	/**
	 * 根据millsecond时间取得日期
	 *
	 * @param mills
	 * @return
	 * @throws Exception
	 */
	public static LocalDateTime getLocalDateTimeByMills(long mills) throws Exception {
		try {
			if (mills == 0) {
				return null;
			}
			Instant instant = Instant.ofEpochMilli(mills);
			ZoneId zone = ZoneId.systemDefault();
			return LocalDateTime.ofInstant(instant, zone);
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * 根据millsecond时间取得 如 yyyy-MM-dd HH:mm:ss 的时间,如果时分秒都为0就返回 yyyy-MM-dd
	 *
	 * @param mills
	 * @return
	 * @throws Exception
	 */
	public static String getTimeByMills(long mills) throws Exception {
		try {
			if (mills == 0) {
				return "-";
			}
			Date date = null;
			Calendar ca = Calendar.getInstance();
			ca.setTimeInMillis(mills);
			date = ca.getTime();
			SimpleDateFormat myformat;

			if (ca.get(Calendar.HOUR_OF_DAY) == 0 && ca.get(Calendar.MINUTE) == 0
					&& ca.get(Calendar.SECOND) == 0) {
				myformat = new SimpleDateFormat("yyyy-MM-dd");
			} else {
				myformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}
			return myformat.format(date);
		} catch (Exception e) {
			return "-";
		}
	}

	/**
	 * 将长整型的日期转换成为yyyy-MM-dd格式的日期。
	 * 
	 * @param mills
	 * @return
	 * @throws Exception
	 */
	public static String formatDate(long mills) throws Exception {
		try {
			if (mills == 0) {
				return "-";
			}
			Date date = null;
			Calendar ca = Calendar.getInstance();
			ca.setTimeInMillis(mills);
			date = ca.getTime();
			SimpleDateFormat myformat;

			myformat = new SimpleDateFormat("yyyy-MM-dd");

			return myformat.format(date);
		} catch (Exception e) {
			return "-";
		}
	}

	/**
	 * 将长整型的日期转换成为yyyy-MM-dd HH:mm:ss格式的日期。
	 * 
	 * @param mills
	 * @return
	 * @throws Exception
	 */
	public static String formatTime(long mills) throws Exception {
		try {
			if (mills == 0) {
				return "-";
			}
			Instant instant = Instant.ofEpochMilli(mills);
		    ZoneId zone = ZoneId.systemDefault();
		    LocalDateTime date = LocalDateTime.ofInstant(instant, zone);
			return date.format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		} catch (Exception e) {
			return "-";
		}
	}

	/**
	 * 根据时间格式如 yyyy-MM-dd HH:mm:ss 的时间取得 UTC 时间
	 * 
	 * @param strTime
	 *            格式如的字符串yyyy-MM-dd HH:mm:ss 或者 yyyy-mm-dd
	 * @return long Utc Millisecond 时间
	 */
	public static long getMillsByTime(String strTime) throws Exception {
		try {
			int year, month, day, hour, minute, second;
			if (strTime.length() != 19 && strTime.length() != 10) {
				throw new Exception("the time string is wrong.");
			}

			year = Integer.parseInt(strTime.substring(0, 4));
			month = Integer.parseInt(strTime.substring(5, 7)) - 1;
			day = Integer.parseInt(strTime.substring(8, 10));

			if (year < 1000 || year > 3000) {
				throw new Exception("the year is wrong.");
			}

			if (month < 0 || month > 12) {
				throw new Exception("the month is wrong.");
			}

			if (day < 1 || day > 31) {
				throw new Exception("the day is wrong");
			}

			Calendar ca = Calendar.getInstance();
			if (strTime.length() == 19) {
				hour = Integer.parseInt(strTime.substring(11, 13));
				minute = Integer.parseInt(strTime.substring(14, 16));
				second = Integer.parseInt(strTime.substring(17, 19));

				if (hour < 0 || hour > 24) {
					throw new Exception("the hour is wrong.");
				}

				if (minute < 0 || minute > 60) {
					throw new Exception("the minute is wrong.");
				}

				if (second < 0 || second > 60) {
					throw new Exception("the second is wrong.");
				}

				ca.set(year, month, day, hour, minute, second);
			} else if (strTime.length() == 10) {
				ca.set(year, month, day, 0, 0, 0);
			}
			return ca.getTimeInMillis();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 根据时间单位，时间间隔，当前时间计算出指定时间加上时间间隔后的时间
	 * 
	 * @param timeUnit
	 *            请使用TimeUtil的静态常量
	 * @param interval
	 *            可为负值
	 * @param timeMill
	 * @return
	 */
	public static long getNextTime(int timeUnit, int interval, long timeMill) {
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(timeMill);
		switch (timeUnit) {
		case TimeUtil.SECOND:
			ca.add(Calendar.SECOND, interval);
			break;
		case TimeUtil.MINUTE:
			ca.add(Calendar.MINUTE, interval);
			break;
		case TimeUtil.HOUR:
			ca.add(Calendar.HOUR, interval);
			break;
		case TimeUtil.DAY:
			ca.add(Calendar.DATE, interval);
			break;
		case TimeUtil.MONTH:
			ca.add(Calendar.MONTH, interval);
			break;
		default:
			return 0;
		}
		return ca.getTimeInMillis();
	}

	/**
	 * 根据时间字符串返回date类型
	 * 
	 * @return
	 * @throws ParseException
	 */
	public static LocalDateTime getDateTimeByTimeString(String timeString) {
		LocalDateTime date = LocalDateTime.now();
		try {
			date = DateFormatUtil.parse(timeString, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 根据日期字符串返回date类型。
	 * 
	 * @param timeString
	 * @return
	 */
	public static LocalDateTime getDateByDateString(String timeString) {
		LocalDateTime date = LocalDateTime.now();
		try {
			date = DateFormatUtil.parse(timeString, "yyyy-MM-dd");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 取得日期。
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @return
	 */
	public static LocalDate getDate(int year, int month, int date) {
		return LocalDate.of(year, month, date);
	}
	
	/**
	 * 取得日期。
	 * @param year
	 * @param month
	 * @param date
	 * @param hourOfDay
	 * @param minute
	 * @param second
	 * @return
	 */
	public static LocalDateTime getDate(int year, int month, int date, int hourOfDay,
			int minute,int second) {
		return LocalDateTime.of(year, month, date, hourOfDay, minute, second);
	}

	/**
	 * 取得结束时间和开始时间的秒数。
	 * 
	 * @param endTime
	 *            结束时间
	 * @param startTime
	 *            开始时间
	 * @return
	 */
	public static int getSecondDiff(LocalDateTime endTime, LocalDateTime startTime) {
		ZoneId zone = ZoneId.systemDefault();
		Instant sinstant = startTime.atZone(zone).toInstant();
		Instant einstant = endTime.atZone(zone).toInstant();
		long start = sinstant.toEpochMilli();
		long end = einstant.toEpochMilli();
		return (int) ((end - start) / 1000);
	}

	/**
	 * 取得一个月的天数
	 * 
	 * @param year
	 *            实际年份
	 * @param mon
	 *            实际月份 1到12月
	 * @return
	 */
	public static int getDaysOfMonth(int year, int mon) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, mon - 1);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);

	}

	/**
	 * 取得某月第一天为星期几。<br>
	 * 星期天为1。 星期六为7。
	 * 
	 * @param year
	 * @param mon
	 * @return
	 */
	public static int getWeekDayOfMonth(int year, int mon) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, mon - 1, 1);
		return cal.get(Calendar.DAY_OF_WEEK);
	} 

	/**
	 * 获取持续时间
	 * 
	 * @param time
	 * @return
	 */
	public static String getDurationTime(LocalDateTime time) {
		if (time==null) {
			return "";
		}
		Long millseconds = getTime(time, LocalDateTime.now());
		return getTime(millseconds);
	}
/*******************新加日期工具****************************************************/
	/**
	 * 过去6个月份遍历
	 * @return
	 */
	public static String[] getPassMonth() {
		String[] last6Months = new String[6];
		Calendar cal = Calendar.getInstance();
		//如果当前日期大于二月份的天数28天或者29天会导致计算月份错误，会多出一个三月份，故设置一个靠前日期解决此问题
		cal.set(Calendar.DAY_OF_MONTH, 1);
		for (int i = 0; i < 6; i++) {
			int str = cal.get(Calendar.MONTH) + 1;
			last6Months[5 - i] = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1);
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1); //逐次往前推1个月
		}
		return last6Months;
	}
	/**
	 * //获取本月的开始时间
	 *
	 * @return
	 */
	public static String getBeginDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(getNowYear(), getNowMonth() - 1, 1);
		//System.out.println(getDayStartTime(calendar.getTime()));
		// return getDayStartTime(calendar.getTime());
		return String.valueOf(getDayStartTime(calendar.getTime()));

	}
	/**
	 * //获取本月的结束时间
	 *
	 * @return
	 */
	public static String getEndDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(getNowYear(), getNowMonth() - 1, 1);
		int day = calendar.getActualMaximum(5);
		calendar.set(getNowYear(), getNowMonth() - 1, day);
		//   System.out.println(getDayStartTime(calendar.getTime()));
		//return getDayEndTime(calendar.getTime());
		return String.valueOf(getDayEndTime(calendar.getTime()));

	}
	//获取某个日期的结束时间
	public static String getDayEndTime(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		if (null != d) {
			calendar.setTime(d);
		}
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return sdf.format(new Date(calendar.getTimeInMillis()));
	}
	//获取今年是哪一年
	public static Integer getNowYear() {
		Date date = new Date();
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		return Integer.valueOf(gc.get(1));
	} //获取本月是哪一月
	public static int getNowMonth() {
		Date date = new Date();
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		return gc.get(2) + 1;
	}
	/**
	 * 获取某个日期间开始时间
	 *
	 * @param d
	 * @return
	 */
	public static String getDayStartTime(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		if (null != d) {
			calendar.setTime(d);
		}
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return sdf.format(new Date(calendar.getTimeInMillis()));
	}
	/**
	 * 获取某个日期间开始时间
	 *
	 * @param d
	 * @return
	 */
	public static String getDayStartTime2(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Calendar calendar = Calendar.getInstance();
		if (null != d) {
			calendar.setTime(d);
		}
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return sdf.format(new Date(calendar.getTimeInMillis()));
	}
	/**
	 * 获取指定日期所在月份结束的时间戳
	 * @param date 指定日期
	 * @return
	 */
	public static Date getMonthEnd(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		//设置为当月最后一天
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		//将小时至23
		c.set(Calendar.HOUR_OF_DAY, 23);
		//将分钟至59
		c.set(Calendar.MINUTE, 59);
		//将秒至59
		c.set(Calendar.SECOND,59);
		//将毫秒至999
		c.set(Calendar.MILLISECOND, 999);
		// 获取本月最后一天的时间戳
		return c.getTime();
	}
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = "2016-10";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Date v = sdf.parse(str);
		System.out.println(
				sdf.format(getMonthEnd(v))
		);
	}

}