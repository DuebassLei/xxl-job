package com.xxl.job.app.util.time;


import com.xxl.job.app.util.string.StringPool;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
/**
 * 日期转换、计算工具类
 *
 * @author  DuebassLei
 * @version 1.0
 * @date    2021/7/11
 *
 */
public class DateUtil {

	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(DateUtil.class);

	public static Calendar toCalendar(LocalDateTime date) {
		Calendar c = Calendar.getInstance();
		ZoneId zone = ZoneId.systemDefault();
	    Instant instant = date.atZone(zone).toInstant();
	    Date udate = Date.from(instant);
		c.setTime(udate);
		return c;
	}
	/**
	 * 将传入时间初始化为当天的最初时间（即00时00分00秒）
	 * 
	 * @param date
	 *            时间
	 * @return 当天最初时间
	 */
	public static LocalDateTime setAsBegin(LocalDateTime date) {
		LocalDateTime ndate = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0);
		return ndate;
	}

	/**
	 * 将传入时间初始化为当天的结束时间（即23时59分59秒）
	 * 
	 * @param date
	 *            时间
	 * @return 当天结束时间
	 */
	public static LocalDateTime setAsEnd(LocalDateTime date) {
		LocalDateTime ndate = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 23, 59, 59);
		return ndate;
	}

	/**
	 * 取当前系统日期，并按指定格式或者是默认格式返回
	 * 
	 * @param style
	 *            指定格式
	 * @return 当前系统日期（字符串）
	 */
	public static String getCurrentTime(String style) {
		if (StringUtils.isEmpty(style)) {
			style = StringPool.DATE_FORMAT_DATETIME;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(style);
		return LocalDateTime.now().format(formatter);
	}

	/**
	 * 取当前系统日期，并按指定格式（yyyy-MM-dd HH:mm:ss ）
	 * 
	 * @return 当前系统日期
	 */
	public static String getCurrentTime() {
		return getCurrentTime("");
	}

	/**
	 * 取当前系统日期（日期格式）
	 * 
	 * @return 当前系统日期（日期）
	 */
	public static LocalDateTime getCurrentDate() {
		return LocalDateTime.now();
	}

	/**
	 * 取当前系统日期
	 * 
	 * @return 当前系统日期（Long格式）
	 */
	public static long getCurrentTimeInMillis() {
		return Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * 得到两日期间所有日期，含起始和结束日期
	 * 
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return
	 * @throws Exception
	 */
	public static LocalDateTime[] getDaysBetween(LocalDateTime startDate, LocalDateTime endDate) {
		// 计算之间有多少天
		long day = (startDate.toInstant(ZoneOffset.of("+8")).toEpochMilli() - endDate.toInstant(ZoneOffset.of("+8")).toEpochMilli())
				/ (24 * 60 * 60 * 1000) > 0 ? (startDate.toInstant(ZoneOffset.of("+8")).toEpochMilli() - endDate
				.toInstant(ZoneOffset.of("+8")).toEpochMilli()) / (24 * 60 * 60 * 1000)
				: (endDate.toInstant(ZoneOffset.of("+8")).toEpochMilli() - startDate.toInstant(ZoneOffset.of("+8")).toEpochMilli())
						/ (24 * 60 * 60 * 1000);
		// 声明日期数组
		LocalDateTime[] dateArr = new LocalDateTime[Integer.valueOf(String.valueOf(day + 1))];
		// 将日期加进去
		for (int i = 0; i < dateArr.length; i++) {
			if (i == 0) {
				dateArr[i] = setAsBegin(startDate);
			} else {
				LocalDateTime nextDay = startDate.plusDays(1);
				startDate = DateUtil.setAsBegin(nextDay);
				dateArr[i] = startDate;
			}
		}
		return dateArr;
	}

	/**
	 * 取得指定年月的天数
	 * 
	 * @param year
	 *            实际年份
	 * @param month
	 *            实际月份 数值范围是1~12
	 * @return
	 */
	public static int getDaysOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);

	}

	/**
	 * 取得某月第一天为星期几。<br>
	 * 星期天为1。 星期六为7。
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getWeekDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 1);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 比较两个时间大小，结束时间是否大于开始时间 传入的是字符串，会转成日期对象在进行比较。
	 * 
	 * @param beginDateStr
	 * @param endDateStr
	 * @return
	 */
	public static boolean compare(String beginDateStr, String endDateStr) {
		try {
			LocalDateTime beginDate = DateFormatUtil.parse(beginDateStr);
			LocalDateTime endDate = DateFormatUtil.parse(endDateStr);
			return beginDate.compareTo(endDate) < 0;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 比较两个时间大小，结束时间是否大于开始时间 传入的是字符串，会转成日期对象在进行比较。
	 * 
	 * @param beginDateStr
	 * @param endDateStr
	 * @return
	 */
	public static int compareTo(String beginDateStr, String endDateStr) {
		try {
			LocalDateTime beginDate = DateFormatUtil.parse(beginDateStr);
			LocalDateTime endDate = DateFormatUtil.parse(endDateStr);
			return beginDate.compareTo(endDate);
		} catch (Exception e) {
			return -2;
		}
	}

	/**
	 * 取得日期。
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param date
	 *            日
	 * @return
	 */
	public static LocalDateTime getDate(int year, int month, int date) {
		return getDate(year, month, date, 0, 0, 0);
	}

	/**
	 * 取得日期。
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param date
	 *            日
	 * @param hourOfDay
	 *            小时
	 * @param minute
	 *            分钟
	 * @param second
	 *            秒
	 * @return
	 */
	public static LocalDateTime getDate(int year, int month, int date, int hourOfDay,
			int minute, int second) {
		LocalDateTime dateTime = LocalDateTime.of(year, month , date, hourOfDay, minute, second);
		return dateTime;
	}

	/**
	 * 获取开始和结束时间的毫秒差
	 * 
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	public static long getTime(LocalDateTime startTime, LocalDateTime endTime) {
		ZoneId zone = ZoneId.systemDefault();
	    Instant sinstant = startTime.atZone(zone).toInstant();
	    Instant einstant = endTime.atZone(zone).toInstant();
		return einstant.toEpochMilli() - sinstant.toEpochMilli();
	}

	/**
	 * 获取指定时间到系统时间的持续时间
	 * 
	 * @param date
	 *            指定时间
	 * @return
	 */
	public static String getDurationTime(LocalDateTime date) {
		return getDurationTime(date, LocalDateTime.now());
	}

	/**
	 * 获取持续时间
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String getDurationTime(LocalDateTime startTime, LocalDateTime endTime) {
		if (startTime == null || endTime == null) {
			return "";
		}
		Long millseconds = getTime(startTime, LocalDateTime.now());
		return getTime(millseconds);
	}

	/**
	 * 根据长整形的毫秒数返回字符串类型的时间段
	 * 
	 * @param millseconds
	 *            毫秒数
	 * @return
	 */
	public static String getTime(Long millseconds) {
		StringBuffer time = new StringBuffer();
		if (millseconds == null) {
			return "";
		}
		int days = (int) (long) millseconds / 1000 / 60 / 60 / 24;
		if (days > 0) {
			time.append(days).append("天");
		}
		long hourMillseconds = millseconds - days * 1000 * 60 * 60 * 24;
		int hours = (int) hourMillseconds / 1000 / 60 / 60;
		if (hours > 0) {
			time.append(hours).append("小时");
		}
		long minuteMillseconds = millseconds - days * 1000 * 60 * 60 * 24
				- hours * 1000 * 60 * 60;
		int minutes = (int) minuteMillseconds / 1000 / 60;
		if (minutes > 0) {
			time.append(minutes).append("分钟");
		}
		return time.toString();
	}
	
	/**
     * 判断时间是否在时间段内
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(LocalDateTime nowTime, LocalDateTime beginTime, LocalDateTime endTime) {
        Calendar date = Calendar.getInstance();
        ZoneId zone = ZoneId.systemDefault();
	    Instant nowinstant = nowTime.atZone(zone).toInstant();
	    Date nowdate = Date.from(nowinstant);
        date.setTime(nowdate);

        Instant begininstant = beginTime.atZone(zone).toInstant();
	    Date begindate = Date.from(begininstant);
        Calendar begin = Calendar.getInstance();
        begin.setTime(begindate);

        Instant endinstant = endTime.atZone(zone).toInstant();
	    Date enddate = Date.from(endinstant);
        Calendar end = Calendar.getInstance();
        end.setTime(enddate);

        if ((date.after(begin)||nowinstant.toEpochMilli()==begininstant.toEpochMilli()) 
        		&& (date.before(end)||nowinstant.toEpochMilli()==endinstant.toEpochMilli())) {
            return true;
        } else {
            return false;
        }
    }
}
