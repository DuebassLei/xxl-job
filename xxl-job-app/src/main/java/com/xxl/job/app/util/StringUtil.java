package com.xxl.job.app.util;

import com.xxl.job.app.util.string.StringPool;
import com.xxl.job.app.util.time.DateFormatUtil;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 字符串工具类
 *
 * @author  DuebassLei
 * @version 1.0
 * @date    2021/7/11
 *
 */
public class StringUtil {
	/**
	 * 将字符串里面的所有英文点符号转成空格（字符实体形式）
	 * 
	 * @param str
	 *            待处理的字符串
	 * @return 每一个点对应一串：&nbsp;&emsp;
	 */
	public static String convertPointToSpace(String str) {
		String space = "";
		if (StringUtils.isEmpty(str)) {
			return space;
		}
		String path[] = str.split("\\.");
		for (int i = 0; i < path.length - 1; i++) {
			space += "&nbsp;&emsp;";
		}
		return space;
	}
	
	/**
	 *  将输入流转utf-8字符串
	 * @param is 输入流
	 * @return 字符串
	 * @throws IOException
	 */
	public static String InputStreamToString(InputStream is) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}
		String str = result.toString(StandardCharsets.UTF_8.name());
		result.close();
		return str;
	}

	/**
	 * 把字符串数组转成带，的字符串
	 * 
	 * @param arr
	 * @return 返回字符串，格式如1,2,3
	 */
	public static String convertArrayToString(String[] arr) {
		return convertArrayToString(arr, StringPool.COMMA);
	}

	/**
	 * 
	 * 把字符串数组转成带，的字符串
	 * 
	 * @param arr
	 * @param split
	 * @return String 返回字符串，格式如1,2,3
	 * @since 1.0.0
	 */
	public static String convertArrayToString(String[] arr, String split) {
		if (arr == null || arr.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			sb.append(arr[i]);
			sb.append(split);
		}
		return sb.substring(0, sb.length() - split.length());
	}

	/**
	 * 把字符串类型的集合转成带，的字符串
	 * 
	 * @param strs
	 *            Collection<String> 适用于List、Set等。
	 * @return
	 */
	public static String convertCollectionAsString(Collection<String> strs) {
		return convertCollectionAsString(strs, StringPool.COMMA);
	}

	/**
	 * 
	 /** 把字符串类型的集合转成指定拆分字符串的字符串
	 * 
	 * @param strs
	 *            Collection<String> 适用于List、Set等。
	 * @param split
	 *            拆分字符串
	 * @return String
	 * @since 1.0.0
	 */
	public static String convertCollectionAsString(Collection<String> strs,
			String split) {
		if (strs == null || strs.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = strs.iterator();
		while (it.hasNext()) {
			sb.append(it.next().toString());
			sb.append(split);
		}
		return sb.substring(0, sb.length() - split.length());
	}

	/**
	 * 将人民币金额数字转成中文大写。
	 * 
	 * @param amount
	 * @return
	 */
	public static String convertToChineseNumeral(double amount) {
		char[] hunit = { '拾', '佰', '仟' }; // 段内位置表示
		char[] vunit = { '万', '亿' }; // 段名表示
		char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' }; // 数字表示
		long midVal = (long) (amount * 100); // 转化成整形
		String valStr = String.valueOf(midVal); // 转化成字符串

		String head = valStr.substring(0, valStr.length() - 2); // 取整数部分
		String rail = valStr.substring(valStr.length() - 2); // 取小数部分

		String prefix = ""; // 整数部分转化的结果
		String suffix = ""; // 小数部分转化的结果
		// 处理小数点后面的数
		if (rail.equals("00")) { // 如果小数部分为0
			suffix = "整";
		} else {
			suffix = digit[rail.charAt(0) - '0'] + "角"
					+ digit[rail.charAt(1) - '0'] + "分"; // 否则把角分转化出来
		}
		// 处理小数点前面的数
		char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组
		char zero = '0'; // 标志'0'表示出现过0
		byte zeroSerNum = 0; // 连续出现0的次数
		for (int i = 0; i < chDig.length; i++) { // 循环处理每个数字
			int idx = (chDig.length - i - 1) % 4; // 取段内位置
			int vidx = (chDig.length - i - 1) / 4; // 取段位置
			if (chDig[i] == '0') { // 如果当前字符是0
				zeroSerNum++; // 连续0次数递增
				if (zero == '0') { // 标志
					zero = digit[0];
				} else if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
					prefix += vunit[vidx - 1];
					zero = '0';
				}
				continue;
			}
			zeroSerNum = 0; // 连续0次数清零
			if (zero != '0') { // 如果标志不为0,则加上,例如万,亿什么的
				prefix += zero;
				zero = '0';
			}
			prefix += digit[chDig[i] - '0']; // 转化该数字表示
			if (idx > 0) {
				prefix += hunit[idx - 1];
			}
			if (idx == 0 && vidx > 0) {
				prefix += vunit[vidx - 1]; // 段结束位置应该加上段名如万,亿
			}
		}

		if (prefix.length() > 0) {
			prefix += '圆'; // 如果整数部分存在,则有圆的字样
		}
		return prefix + suffix; // 返回正确表示
	}

	/**
	 * 将传入字符串中的Html字符实体（character entities）转换成Html预留字符
	 * 
	 * @param content
	 *            待转换的字符串
	 * @return
	 */
	public static String convertCharEntityToHtml(String content) {
		content = content.replace("&apos;", "'").replace("&quot;", "\"")
				.replace("&gt;", ">").replace("&lt;", "<")
				.replace("&amp;", "&");

		int start = 0;
		int end = 0;
		final StringBuilder buffer = new StringBuilder();

		while (start > -1) {
			int system = 10;// 进制
			if (start == 0) {
				int t = content.indexOf("&#");
				if (start != t) {
					start = t;
				}
				if (start > 0) {
					buffer.append(content.substring(0, start));
				}
			}
			end = content.indexOf(";", start + 2);
			String charStr = "";
			if (end != -1) {
				charStr = content.substring(start + 2, end);
				// 判断进制
				char s = charStr.charAt(0);
				if (s == 'x' || s == 'X') {
					system = 16;
					charStr = charStr.substring(1);
				}
			}
			// 转换
			try {
				char letter = (char) Integer.parseInt(charStr, system);
				buffer.append(new Character(letter).toString());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			// 处理当前unicode字符到下一个unicode字符之间的非unicode字符
			start = content.indexOf("&#", end);
			if (start - end > 1) {
				buffer.append(content.substring(end + 1, start));
			}

			// 处理最后面的非unicode字符
			if (start == -1) {
				int length = content.length();
				if (end + 1 != length) {
					buffer.append(content.substring(end + 1, length));
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * 将传入的字符串的Html预留字符转换成Html字符实体（character entities）
	 * 
	 * @param content
	 *            待转换的字符串（一般为Html代码）
	 * @return
	 */
	public static String convertHtmlToCharEntity(String content) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);

			switch (c) {
			case 0x0A:
				sb.append(c);
				break;

			case '<':
				sb.append("&lt;");
				break;

			case '>':
				sb.append("&gt;");
				break;

			case '&':
				sb.append("&amp;");
				break;

			case '\'':
				sb.append("&apos;");
				break;

			case '"':
				sb.append("&quot;");
				break;

			default:
				if ((c < ' ') || (c > 0x7E)) {
					sb.append("&#x");
					sb.append(Integer.toString(c, 16));
					sb.append(';');
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 格式化带参数的字符串，如 /param/detail.ht?a=${0}&b=${1}
	 * 注意字符串的参数从0下标开始，字符串的参数数量和args数组的数量要一致。
	 * 
	 * @param message
	 * @param args
	 * @return
	 */
	public static String format(String message, Object... args) {
		for (int i = 0; i < args.length; i++) {
			message = message.replace("${" + i + "}", args[i].toString());
		}
		return message;
	}

	/**
	 * 格式化如下字符串 http://www.bac.com?a=${a}&b=${b}
	 * 
	 * @param message
	 * @param params
	 */
	public static String format(String message, Map<String, Object> params) {
		String result = message;
		if (params == null ||  params.isEmpty()) {
			return result;
		}
		Iterator<String> keyIts = params.keySet().iterator();
		while (keyIts.hasNext()) {
			String key = keyIts.next();
			Object value = params.get(key);
			if (value != null) {
				result = result.replace("${" + key + "}", value.toString());
			}
		}
		return result;
	}

	/**
	 * 简单的字符串格式化，性能较好。支持不多于10个占位符，从%1开始计算，数目可变。参数类型可以是字符串、Integer、Object，
	 * 甚至int等基本类型
	 * 、以及null，但只是简单的调用toString()，较复杂的情况用String.format()。每个参数可以在表达式出现多次。
	 * 
	 * @param msgWithFormat
	 * @param autoQuote
	 *            是否加中括号将结果括起来
	 * @param args
	 * @return
	 */
	public static StringBuilder format(CharSequence msgWithFormat,
			boolean autoQuote, Object... args) {
		int argsLen = args.length;
		boolean markFound = false;

		StringBuilder sb = new StringBuilder(msgWithFormat);

		if (argsLen > 0) {
			for (int i = 0; i < argsLen; i++) {
				String flag = "%" + (i + 1);
				int idx = sb.indexOf(flag);
				// 支持多次出现、替换的代码
				while (idx >= 0) {
					markFound = true;
					sb.replace(idx, idx + 2, toString(args[i], autoQuote));
					idx = sb.indexOf(flag);
				}
			}

			if (args[argsLen - 1] instanceof Throwable) {
				StringWriter sw = new StringWriter();
				((Throwable) args[argsLen - 1])
						.printStackTrace(new PrintWriter(sw));
				sb.append("\n").append(sw.toString());
			} else if (argsLen == 1 && !markFound) {
				sb.append(args[argsLen - 1].toString());
			}
		}
		return sb;
	}

	/**
	 * 判断指定的内容是否存在
	 * 
	 * @param content
	 *            内容
	 * @param beginStr
	 *            开始内容
	 * @param endStr
	 *            结束内容
	 * @return
	 */
	public static boolean isExist(String content, String beginStr, String endStr) {
		final boolean isExist = true;
		// 转成小写
		String lowContent = content.toLowerCase();
		String lowBeginStr = beginStr.toLowerCase();
		String lowEndStr = endStr.toLowerCase();

		int beginIndex = lowContent.indexOf(lowBeginStr);
		int endIndex = lowContent.indexOf(lowEndStr);
		if (beginIndex != -1 && endIndex != -1 && beginIndex < endIndex) {
			return isExist;
		}
		return !isExist;
	}

	/**
	 * 对字符串去掉前面的指定字符
	 * 
	 * @param content
	 *            待处理的字符串
	 * @param prefix
	 *            要去掉前面的指定字符串
	 * @return
	 */
	public static String trimPrefix(String content, String prefix) {
		String resultStr = content;
		while (resultStr.startsWith(prefix)) {
			resultStr = resultStr.substring(prefix.length());
		}
		return resultStr;
	}

	/**
	 * 对字符串去掉前面的指定字符
	 * 
	 * @param content
	 *            待处理的字符串
	 * @param suffix
	 *            要去掉后面的指定字符串
	 * @return
	 */
	public static String trimSuffix(String content, String suffix) {
		String resultStr = content;
		while (resultStr.endsWith(suffix)) {
			resultStr = resultStr.substring(0,
					resultStr.length() - suffix.length());
		}
		return resultStr;
	}

	/**
	 * 对字符串的前后均去掉前面的指定字符
	 * 
	 * @param content
	 * @param trimStr
	 * @return
	 */
	public static String trim(String content, String trimStr) {
		return trimSuffix(trimPrefix(content, trimStr), trimStr);
	}

	/**
	 * 把字符串的第一个字母转为大写
	 * 
	 * @param str
	 * @return
	 */
	public static String upperFirst(String str) {
		return toFirst(str, true);
	}
	
	/**
	 * 判断字符串非空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}
		if (str.trim().equals("")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断Long是否为空
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(Long value) {
		if (value == null) {
			return true;
		}
		if (value.longValue()==0) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 为空判断,0做空处理。
	 * <pre>
	 *这里判断：
	 *1.字符串为NULL
	 *2.字符串为空串。
	 *3.字符串为0。  
	 * </pre>
	 * @param tmp
	 * @return 
	 * boolean
	 */
	public static boolean isZeroEmpty(String tmp){
		boolean isEmpty=StringUtil.isEmpty(tmp);
		if(isEmpty) {
			return true;
		}
		return "0".equals(tmp);
	}
	
	/**
	 * 非空判断。
	 * @param tmp
	 * @return 
	 * boolean
	 */
	public static boolean isNotZeroEmpty(String tmp){
		return !isZeroEmpty(tmp);
	}

	/**
	 * 把字符串的第一个字母转为小写
	 * 
	 * @param str
	 * @return
	 */
	public static String lowerFirst(String str) {
		return toFirst(str, false);
	}

	/**
	 * 把字符串的第一个字母转为大写或者小写
	 * 
	 * @param str
	 *            字符串
	 * @param isUpper
	 *            是否大写
	 * @return
	 */
	public static String toFirst(String str, boolean isUpper) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		char first = str.charAt(0);
		String firstChar = new String(new char[] { first });
		firstChar = isUpper ? firstChar.toUpperCase() : firstChar.toLowerCase();
		return firstChar + str.substring(1);
	}

	/**
	 * 将content中所有{...}的替换为replace参数内容
	 * 
	 * @param content
	 *            待替换的字符串
	 * @param replace
	 *            替换的字符串
	 * @return 替换后的字符串，如content=abc{aa}{bb} ； replace ="ff"，结果就是abcffff
	 */
	public static String replaceVariable(String content, String replace) {
		return replaceVariable(content, replace, "\\{(.*?)\\}");
	}

	/**
	 * 将content中所有符合regular正则表达式的内容替换为replace参数内容
	 * 
	 * @param content
	 *            待替换的字符串
	 * @param replace
	 *            替换的字符串
	 * @param regular
	 *            正则表达式
	 * @return 替换后的字符串。 如content=abc{aa}{bb} ； replace
	 *         ="ff"，regular="\\{(.*?)\\}"；结果就是abcffff
	 */
	public static String replaceVariable(String content, String replace,
			String regular) {
		Pattern regex = Pattern.compile(regular);
		String result = content;
		Matcher regexMatcher = regex.matcher(result);
		while (regexMatcher.find()) {
			String toReplace = regexMatcher.group(0);
			result = result.replace(toReplace, replace);
			regexMatcher = regex.matcher(result);
		}
		return result;
	}

	/**
	 * 对传入的字符串（content）进行变量值替换（map） 采用默认的正则表达式：\\{(.*?)\\}
	 * 
	 * @param content
	 *            要处理的字符串
	 * @param map
	 *            替换参数和值的集合
	 * @return 替换后的字符串
	 * @throws Exception
	 */
	public static String replaceVariableMap(String content,
			Map<String, Object> map) throws Exception {
		return replaceVariableMap(content, map, "\\{(.*?)\\}");
	}

	/**
	 * 
	 * @param template
	 *            要处理的字符串
	 * @param map
	 *            替换参数和值的集合
	 * @param regular
	 *            正则表达式
	 * @return 替换后的字符串
	 * @throws Exception
	 *             如果template的某个
	 */
	public static String replaceVariableMap(String template,
			Map<String, Object> map, String regular) throws Exception {
		Pattern regex = Pattern.compile(regular);
		Matcher regexMatcher = regex.matcher(template);
		while (regexMatcher.find()) {
			String key = regexMatcher.group(1);
			String toReplace = regexMatcher.group(0);
			String value = (String) map.get(key);
			if (value != null) {
				template = template.replace(toReplace, value);
			} else {
				throw new Exception("没有找到[" + key + "]对应的变量值，请检查表变量配置!");
			}
		}

		return template;
	}

	/**
	 * 根据默认的特殊字符正则表达式去除特殊字符
	 * 
	 * @param str
	 * @return
	 */
	public static String removeSpecial(String str)
			throws PatternSyntaxException {
		return removeByRegEx(str, StringPool.SPECIAL_REG_EX);
	}

	/**
	 * 根据传入的字符串（参数str），通过正则表达式（参数regEx），去掉该表达式匹配的字符。
	 * 
	 * @param str
	 *            待处理的字符串
	 * @param regEx
	 *            正则表达式
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static String removeByRegEx(String str, String regEx)
			throws PatternSyntaxException {
		// 清除掉所有特殊字符
		Pattern p = Pattern.compile(StringPool.SPECIAL_REG_EX);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * String转Byte数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] stringToBytes(String str) {
		byte digest[] = new byte[str.length() / 2];
		for (int i = 0; i < digest.length; i++) {
			String byteString = str.substring(2 * i, 2 * i + 2);
			int byteValue = Integer.parseInt(byteString, 16);
			digest[i] = (byte) byteValue;
		}

		return digest;
	}

	/**
	 * Byte数组转String
	 * 
	 * @param b
	 * @return
	 */
	public static String bytesToString(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2) {
				plainText = "0" + plainText;
			}
			hexString.append(plainText);
		}
		return hexString.toString();
	}

	/**
	 * 将对象转成格式化的字符串输出
	 * 
	 * @param obj
	 *            任意对象
	 * @param autoQuote
	 *            是否加中括号将结果括起来
	 * @return
	 */
	public static String toString(Object obj, boolean autoQuote) {
		StringBuilder sb = new StringBuilder();
		if (obj == null) {
			sb.append("NULL");
		} else {
			if (obj instanceof Object[]) {
				for (int i = 0; i < ((Object[]) obj).length; i++) {
					sb.append(((Object[]) obj)[i]).append(", ");
				}
				if (sb.length() > 0) {
					sb.delete(sb.length() - 2, sb.length());
				}
			} else {
				sb.append(obj.toString());
			}
		}
		if (autoQuote
				&& sb.length() > 0
				&& !((sb.charAt(0) == '[' && sb.charAt(sb.length() - 1) == ']') || (sb
						.charAt(0) == '{' && sb.charAt(sb.length() - 1) == '}'))) {
			sb.insert(0, "[").append("]");
		}
		return sb.toString();
	}
	
	/**
	 * 字符串 编码转换
	 * 
	 * @param str
	 *            字符串
	 * @param from
	 *            原來的編碼
	 * @param to
	 *            轉換后的編碼
	 * @return
	 */
	public static String encodingString(String str, String from, String to) {
		String result = str;
		try {
			result = new String(str.getBytes(from), to);
		} catch (Exception e) {
			result = str;
		}
		return result;
	}
	
	 /**
     *
     * <p>获取最后被截取后后面的字符串</p>
     *
     * <pre>
     * StringUtils.substringAfterLast(null, *)        = null
     * StringUtils.substringAfterLast("", *)          = ""
     * StringUtils.substringAfterLast(*, "")          = ""
     * StringUtils.substringAfterLast(*, null)        = ""
     * StringUtils.substringAfterLast("abc", "a")     = "bc"
     * StringUtils.substringAfterLast("abcba", "b")   = "a"
     * StringUtils.substringAfterLast("abc", "c")     = ""
     * StringUtils.substringAfterLast("a", "a")       = ""
     * StringUtils.substringAfterLast("a", "z")       = ""
     * StringUtils.substringAfterLast("a.b.c.d", ".") = "d"
     * </pre>
     *
     */
    public static String substringAfterLast(String str, String separator) {
    	return StringUtils.substringAfterLast(
    			str, separator);
    }
    
    /**
     * <p>获取最后被截取后前面的字符串</p>
     * 
     * <pre>
     * StringUtils.substringBeforeLast(null, *)       = null
     * StringUtils.substringBeforeLast("", *)         = ""
     * StringUtils.substringBeforeLast("abcba", "b")  = "abc"
     * StringUtils.substringBeforeLast("abc", "c")    = "ab"
     * StringUtils.substringBeforeLast("a", "a")      = ""
     * StringUtils.substringBeforeLast("a", "z")      = "a"
     * StringUtils.substringBeforeLast("a", null)     = "a"
     * StringUtils.substringBeforeLast("a", "")       = "a"
     * StringUtils.substringAfterLast("a.b.c.d", ".") = "a.b.c"
     * </pre>
     *
     */
    public static String substringBeforeLast(String str, String separator){
    	return StringUtils.substringBeforeLast(
    			str, separator);
    }

	/**
	 * 删除后面指定的字符
	 * 
	 * @param toTrim
	 * @param trimStr
	 * @return
	 */
	public static String trimSufffix(String toTrim, String trimStr) {
		while (toTrim.endsWith(trimStr)) {
			toTrim = toTrim.substring(0, toTrim.length() - trimStr.length());
		}
		return toTrim;
	}
	
	/**
	 * 将数据库字段名转为DataGrid字段名
	 * isIgnoreFirst:是否忽略第一个字段不转大写
	 * @return 
	 */
	public static  String convertDbFieldToField(String dbField)
	{
		return convertDbFieldToField(dbField,"_",true);
	}
	
	/**
	 * 将数据库字段名转为DataGrid字段名,如 sys_data_ 转为sysData
	 * symbol:间隔符号
	 * isIgnoreFirst：是否忽略第一个单词的首字母转大写
	 * @return 
	 */
	public static  String convertDbFieldToField(String dbField,String symbol,boolean isIgnoreFirst)
	{
	   String result="";
	   if(dbField.startsWith(symbol)) {
		   dbField=dbField.substring(1);
	   }
	   if(dbField.endsWith(symbol)) {
		   dbField=dbField.substring(0,dbField.length()-1);
	   }
	   String[] arr=dbField.split(symbol);
	   for (int i=0;i<arr.length;i++)
	   {
		   String str=arr[i];
		   if(isIgnoreFirst&&i!=0)
		   {
		      char oldChar = str.charAt(0);
		      char newChar = (oldChar + "").toUpperCase().charAt(0);
		      str = newChar+str.substring(1);
		   }
		   result+=str;
	   }
	   return result;
	}
	
	/**
	 * 数组合并。
	 * @param vals
	 * @param separator
	 * @return
	 */
	public static String join(String[] vals,String separator){
		if(vals==null || vals.length==0) {
			return "";
		}
		String val="";
		for(int i=0;i<vals.length;i++){
			if(i==0){
				val+=vals[i];
			}
			else{
				val+=separator +vals[i];
			}
		}
		return val;
	}
	
	/**
	 * list合并。
	 * @param vals
	 * @param separator
	 * @return
	 */
	public static String join(List<String> vals,String separator){
		if(BeanUtils.isEmpty(vals)) {
			return "";
		}
		String val="";
		for(int i=0;i<vals.size();i++){
			if(i==0){
				val+=vals.get(i);
			}
			else{
				val+=separator +vals.get(i);
			}
		}
		return val;
	}
	
	/**
     * 把String 转换成指定的对象
     * @param obj
     * @param type
     * @return
     */
    public static Object parserObject(Object obj, String type) {
		if (obj==null) {
			return null;
		}
		Object val = obj;
		try {
			String str  =obj.toString();
			if (type.equalsIgnoreCase("string")) {
				val = str;
			} else if (type.equalsIgnoreCase("int")) {
				val = Integer.parseInt(str);
			} else if (type.equalsIgnoreCase("float")) {
				val = Float.parseFloat(str);
			} else if (type.equalsIgnoreCase("double")) {
				val = Double.parseDouble(str);
			} else if (type.equalsIgnoreCase("byte")) {
				val = Byte.parseByte(str);
			} else if (type.equalsIgnoreCase("short")) {
				val = Short.parseShort(str);
			} else if (type.equalsIgnoreCase("long")) {
				val = Long.parseLong(str);
			} else if (type.equalsIgnoreCase("boolean")) {
				if (StringUtils.isNumeric(str)) {
					val = (Integer.parseInt(str) == 1 ? true : false);
				}
				val = Boolean.parseBoolean(str);
			} else if (type.equalsIgnoreCase("date")) {
				val = DateFormatUtil.parse(str);
			} else {
				val = str;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}


	/**
	 * 根据一串逗号分隔的字符串取得字符串形数组
	 * @param str
	 * @author zhaoxy
	 * @return
	 */
	public static String[] getStringAryByStr(String str) {
		if (StringUtil.isEmpty(str)) {
			return null;
		}
		String[] aryId = str.split(",");
		String[] lAryId = new String[aryId.length];
		for (int i = 0; i < aryId.length; i++) {
			lAryId[i] = (aryId[i]);
		}
		return lAryId;
	}
	
	/**
	 * 根据一串逗号分隔的长整型字符串取得长整形数组
	 * 
	 * @param str 字符串参数
	 * @return    返回长整型数组
	 */
	public static Long[] getLongAryByStr(String str) {
		if (StringUtil.isEmpty(str)) {
			return null;
		}
		str = str.replace("[", "");
		str = str.replace("]", "");
		String[] aryId = str.split(",");
		Long[] lAryId = new Long[aryId.length];
		for (int i = 0; i < aryId.length; i++) {
			lAryId[i] = Long.parseLong(aryId[i]);
		}
		return lAryId;
	}
	
	
	/**
	 * string转map
	 * @param param
	 * @return
	 */
	public static Object getMapValue(String param) {
        Map map = new HashMap();
        String str = "";
        String key = "";
        Object value = "";
        char[] charList = param.toCharArray();
        boolean valueBegin = false;
        for (int i = 0; i < charList.length; i++) {
            char c = charList[i];
            if (c == '{') {
                if (valueBegin == true) {
                    value = getMapValue(param.substring(i, param.length()));
                    i = param.indexOf('}', i) + 1;
                    map.put(key, value);
                }
            } else if (c == '=') {
                valueBegin = true;
                key = str;
                str = "";
            } else if (c == ',') {
                valueBegin = false;
                value = str;
                str = "";
                map.put(key, value);
            } else if (c == '}') {
                if (str != "") {
                    value = str;
                }
                map.put(key, value);
                return map;
            } else if (c != ' ') {
                str += c;
            }
        }
        return map;
    }
	
	/**
	 * 值为List转为以逗号隔开并且以单引号括起来的字符串
	 * 
	 * @param listMap
	 * @return
	 */
	public static Map<String, String> getMapStringByMapList(Map<String, Set<String>> listMap){
		Map<String, String> map = new HashMap<String, String>();
		for (String key : listMap.keySet()){
			Set<String> list = listMap.get(key);
			if(list==null) {
				continue;
			}
			String valueString = convertListToSingleQuotesString(list);
			if(StringUtil.isNotEmpty(valueString)){
				map.put(key, valueString);
			}
		}
		return map;
	}
	
	/**
	 * List转成以单引号括起来字符串
	 * 
	 * @param set
	 * @return
	 */
	public static String convertListToSingleQuotesString(Set<String> set){
		if(set==null) {
			return "";
		}
		String ids = "";
		for (String value : set){
			ids = ids + "\'" + value + "\',";
		}
		ids = ids.equals("") ? "" : ids.substring(0, ids.length() - 1);
		return ids;
	}

	/**
	 * 向 StringBuffer 对象中插入数据，去除换行字符
	 * @param stringBuffer
	 * @param text  插入文本
	 * @param isEnd  是否是最后一行
	 * @param splitChar 文本分隔符
	 * */
	private static void insertFields(StringBuffer stringBuffer,String text,String splitChar,boolean isEnd) {
		if (text == null) {
			text = "";
		} else {
			text = getReplaceString(text);
		}
		if (isEnd) {
			stringBuffer.append(text);
		} else {
			stringBuffer.append(text + splitChar);
		}
	}

	/**
	 * 替换文本中的换行符
	 * @param text
	 * @return 处理后字符串
	 * */
	private static String getReplaceString(String text){
		text = text.replaceAll("(?:\r|\n|"+"|"+")", "");
		return text;
	}

}
