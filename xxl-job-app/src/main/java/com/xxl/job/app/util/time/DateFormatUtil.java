package com.xxl.job.app.util.time;


import com.xxl.job.app.util.BeanUtils;
import com.xxl.job.app.util.string.StringPool;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
/**
 * 日期格式工具类
 *
 * @author  DuebassLei
 * @version 1.0
 * @date    2021/7/11
 *
 */
public class DateFormatUtil {
	/**
	 * yyyy-MM-dd 时间格式
	 */
	public static final DateTimeFormatter DATE_FORMAT_DATE = DateTimeFormatter.ofPattern(
			StringPool.DATE_FORMAT_DATE);
	/**
	 * yyyy-MM-dd HH:mm:ss 时间格式
	 */
	public static final DateTimeFormatter DATE_FORMAT_DATETIME = DateTimeFormatter.ofPattern(
			StringPool.DATE_FORMAT_DATETIME);
	/**
	 * yyyy-MM-dd HH:mm 时间格式
	 */
	public static final DateTimeFormatter DATE_FORMAT_DATETIME_NOSECOND = DateTimeFormatter.ofPattern(
			StringPool.DATE_FORMAT_DATETIME_NOSECOND);

	/**
	 * yyyy-MM-dd HH 时间格式
	 */
	public static final DateTimeFormatter DATE_FORMAT_DATETIME_NOMINUTE = DateTimeFormatter.ofPattern(
			StringPool.DATE_FORMAT_DATETIME_NOMINUTE);
	/**
	 * HH:mm:ss 时间格式
	 */
	public static final DateTimeFormatter DATE_FORMAT_TIME = DateTimeFormatter.ofPattern(
			StringPool.DATE_FORMAT_TIME);
	/**
	 * HH:mm 时间格式
	 */
	public static final DateTimeFormatter DATE_FORMAT_TIME_NOSECOND = DateTimeFormatter.ofPattern(
			StringPool.DATE_FORMAT_TIME_NOSECOND);
	/**
	 * yyyy-MM-dd HH:mm:ss.SSS 时间格式
	 */
	public static final DateTimeFormatter DATE_FORMAT_TIMESTAMP = DateTimeFormatter.ofPattern(
			StringPool.DATE_FORMAT_TIMESTAMP);

	private static final Log logger = LogFactory.getLog(DateFormatUtil.class);

	/**
	 * 根据日期字符串是否含有时间决定转换为日期还是日期时间还是时间
	 * 
	 * @param dateString
	 *            时间字符串
	 * @return 格式化的时间
	 * @throws ParseException
	 */
	public static LocalDateTime parse(String dateString) throws ParseException {
		if (dateString.trim().indexOf(StringPool.SPACE) > 0
				&& dateString.trim().indexOf(StringPool.DOT) > 0) {
			return LocalDateTime.parse(dateString, DATE_FORMAT_TIMESTAMP);
		} else if (dateString.trim().indexOf(StringPool.SPACE) > 0) {
			if (dateString.trim().indexOf(StringPool.COLON) > 0) {
				// 如果有两个:，则有时分秒,一个冒号只有时分
				if (dateString.trim().indexOf(StringPool.COLON) != dateString
						.trim().lastIndexOf(StringPool.COLON)) {
					return LocalDateTime.parse(dateString, DATE_FORMAT_DATETIME);
				} else {
					return LocalDateTime.parse(dateString, DATE_FORMAT_DATETIME_NOSECOND);
				}
			} else {
				return LocalDateTime.parse(dateString, DATE_FORMAT_DATETIME_NOMINUTE);
			}
		} else if (dateString.indexOf(StringPool.COLON) > 0) {
			// 如果有两个:，则有时分秒,一个冒号只有时分
			if (dateString.trim().indexOf(StringPool.COLON) != dateString
					.trim().lastIndexOf(StringPool.COLON)) {
				return LocalDateTime.parse(dateString, DATE_FORMAT_TIME);
			} else {
				return LocalDateTime.parse(dateString, DATE_FORMAT_TIME_NOSECOND);
			}
		}
		return LocalDateTime.parse(dateString, DATE_FORMAT_DATE);
	}


	/**
	 * 按指定的格式输出string到date
	 * 
	 * @param dateString
	 *            时间字符串
	 * @param style
	 *            格式化参数 （请使用{@link StringPool}的变量）
	 * @return 格式化的时间
	 * @throws ParseException
	 */
	public static LocalDateTime parse(String dateString, String style)
			throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(style);
		if("yyyy-MM-dd".equals(style)){
			LocalDate date = LocalDate.parse(dateString, formatter);
			 LocalDateTime localDateTime = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0);
			return localDateTime;
		}else{
			return LocalDateTime.parse(dateString, formatter);
		}
	}
	
	/**
	 * Oracle不支持LocalDateTime直接插入，所以用Date
	 * @param dateString
	 * @param style
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String dateString, String style)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(style);
		return sdf.parse(dateString);
	}
	
	/**
	 * 按指定的格式输出string到date
	 * 返回类型为LocalDate
	 * 
	 * @param dateString
	 *            时间字符串
	 * @param style
	 *            格式化参数 （请使用{@link StringPool}的变量）
	 * @return 格式化的时间
	 * @throws ParseException
	 */
	public static LocalDate dateParse(String dateString, String style)
			throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(style);
		return LocalDate.parse(dateString, formatter);
	}

	/**
	 * 将日期字符串转成日期对象，该字符串支持的格式是传入的格式数组
	 * 
	 * @param dateString
	 *            日期字符串
	 * @param style
	 *            日期格式数组（请使用{@link StringPool}的变量）
	 * @return
	 */
	public static LocalDateTime parse(String dateString, String... style) {
		LocalDateTime date = null;
		if (StringUtils.isEmpty(dateString)) {
			return date;
		}
		try {
			Date udate = DateUtils.parseDate(dateString, style);
			Instant instant = udate.toInstant();
		    ZoneId zoneId = ZoneId.systemDefault();
		    date = instant.atZone(zoneId).toLocalDateTime();
		} catch (Exception ex) {
			logger.error("Pase the Date(" + dateString + ") occur errors:"
					+ ex.getMessage());
		}
		return date;
	}
	
	/**
	 *  将Date类型日志转化为LocalDateTime类型
	 * @param udate
	 * @return
	 */
	public static LocalDateTime parse(Date udate) {
		LocalDateTime date = null;
		if(BeanUtils.isNotEmpty(udate)){
			try {
				Instant instant = udate.toInstant();
			    ZoneId zoneId = ZoneId.systemDefault();
			    date = instant.atZone(zoneId).toLocalDateTime();
			} catch (Exception ex) {
				try {
					String format = StringPool.DATE_FORMAT_DATETIME;
					String time = TimeUtil.getFormatString(udate.getTime(), format);
					return DateFormatUtil.parse(time, format);
				} catch (Exception e) {
					logger.error("Pase the Date(" + udate + ") occur errors:"
							+ ex.getMessage());
				}
			}
		}
		
		return date;
	}
	
	/**
	 * 将LocalDateTime转为Date
	 * @param date
	 * @return
	 */
	public static Date parse(LocalDateTime date) {
		ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = date.atZone(zoneId);
        return Date.from(zdt.toInstant());
	}

	/**
	 * 格式化输出date到string
	 * 
	 * @param date
	 *            时间
	 * @param style
	 *            格式化参数
	 * @return
	 */
	public static String format(LocalDateTime date, String style) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(style);
		return date.format(dateFormat);
	}

	/**
	 * 将string(yyyy-MM-dd)转化为日期
	 * 
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static LocalDateTime parseDate(String dateString) throws ParseException {
		return LocalDateTime.parse(dateString,DATE_FORMAT_DATE);
	}

	/**
	 * 按格式(yyyy-MM-dd)输出date到string
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(LocalDateTime date) {
		return DATE_FORMAT_DATE.format(date);
	}

	/**
	 * 将string(yyyy-MM-dd HH:mm:ss)转化为日期
	 * 
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static LocalDateTime parseDateTime(String dateString) throws ParseException {
		return LocalDateTime.parse(dateString,DATE_FORMAT_DATETIME);
	}

	/**
	 * 按格式(yyyy-MM-dd HH:mm:ss )输出date到string
	 * 
	 * @param date
	 * @return
	 */
	public static String formaDatetTime(LocalDateTime date) {
		return DATE_FORMAT_DATETIME.format(date);
	}

	/**
	 * 按格式(yyyy-MM-dd HH:mm )输出date到string
	 * 
	 * @param date
	 * @return
	 */
	public static String formatTimeNoSecond(LocalDateTime date) {
		return DATE_FORMAT_DATETIME_NOSECOND.format(date);
	}

	/**
	 * 按格式(yyyy-MM-dd HH:mm )输出 string到date
	 * 
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static LocalDateTime parseTimeNoSecond(String dateString)
			throws ParseException {
		
		return LocalDateTime.parse(dateString, DATE_FORMAT_DATETIME_NOSECOND);
	}

	/**
	 * 根据长整型毫秒数返回时间形式。 日期格式为yyyy-MM-dd HH:mm:ss，例子:2007-01-23 13:45:21
	 * 
	 * @param millisecond
	 *            毫秒数
	 * @return
	 */
	public static String format(long millisecond) {
		Instant instant = Instant.ofEpochMilli(millisecond);
	    ZoneId zone = ZoneId.systemDefault();
		LocalDateTime date = LocalDateTime.ofInstant(instant, zone);
		return format(date, StringPool.DATE_FORMAT_DATETIME);
	}

	/**
	 * 根据长整型毫秒数和指定的时间格式返回时间字符串。
	 * 
	 * @param millisecond
	 *            毫秒数
	 * @param style
	 *            指定格式
	 * @return
	 */
	public static String format(long millisecond, String style) {
		Instant instant = Instant.ofEpochMilli(millisecond);
	    ZoneId zone = ZoneId.systemDefault();
	    LocalDateTime date = LocalDateTime.ofInstant(instant, zone);
		return format(date, style);
	}
	
	/**
	 * 取当前系统日期，并按指定格式或者是默认格式返回
	 * 
	 * @param style
	 * @return
	 */
	public static String getNowByString(String style) {
		if (null == style || "".equals(style)) {
			style = "yyyy-MM-dd HH:mm:ss";
		}
		return format(LocalDateTime.now(), style);
	}
	
	/**
	 * 将String类型 日期格式的数据转化成指定 格式的String 
	 * 如yyyy-MM-dd HH:mm:ss 转化成yyyyMMdd
	 * @param dateString
	 * @param style
	 * @return
	 * @throws ParseException
	 */
	public static String dateStringToString(String dateString,String style) throws ParseException {
		LocalDateTime newDate = parseDateTime(dateString);
		return format(newDate, "yyyyMMdd");
	}
}
