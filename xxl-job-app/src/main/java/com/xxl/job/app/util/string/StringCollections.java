package com.xxl.job.app.util.string;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 字符串的集合处理工具类
 *
 * @author  DuebassLei
 * @version 1.0
 * @date    2021/6/11
 *
 */
public class StringCollections {
	public final static String DEFAULT_TOKEN = ",";

	/**
	 * 将一个字符串拆分为每一个字符组合成的列表
	 * 
	 * @param str
	 *            如：abcdef
	 * @return 包括{"a","b","c","d","e","f"}的列表
	 */
	public final static List<String> toLetterList(String str) {
		List<String> singles = new ArrayList<String>();
		if (StringUtils.isNotEmpty(str)) {
			for (int i = 0; i < str.length(); i++) {
				singles.add(str.substring(i, i + 1));
			}
		}
		return singles;
	}

	/**
	 * 将传入的字符串按token参数进行分割，并存储为String[]数组返回
	 * 
	 * @param str
	 * @param token
	 * @return
	 */
	public final static String[] toArray(String str, String token) {
		String[] array = null;
		if (str != null && str.length() > 0 && token != null
				&& token.length() > 0) {
			if (str.indexOf(token) > -1) {
				StringTokenizer st = new StringTokenizer(str, token);
				if (st != null && st.countTokens() > 0) {
					array = new String[st.countTokens()];
					int i = 0;
					while (st.hasMoreTokens()) {
						array[i++] = st.nextToken();
					}
				}
			} else {
				array = new String[1];
				array[0] = str;
			}
		}

		return array;
	}

	/**
	 * 将传入的字符串按英文逗号（默认）进行分割，并存储为String[]数组返回
	 * 
	 * @param str
	 * @return
	 */
	public final static String[] toArray(String str) {
		return toArray(str, DEFAULT_TOKEN);
	}

	/**
	 * 将List字符串集合转成String[]数组返回
	 * 
	 * @param list
	 * @return
	 */
	public final static String[] toArray(List<String> list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i).toString();
		}
		return array;
	}

	/**
	 * 将传入的字符串按token参数进行分割，存储为List<String>返回
	 * 
	 * @param str
	 * @param token
	 * @return
	 */
	public final static List<String> toList(String str, String token) {
		List<String> array = new ArrayList<String>();
		if (str != null && str.length() > 0 && token != null
				&& token.length() > 0) {
			if (str.indexOf(token) > -1) {
				StringTokenizer st = new StringTokenizer(str, token);
				while (st.hasMoreTokens()) {
					array.add(st.nextToken());
				}
			} else {
				array.add(str);
			}
		}
		return array;
	}

	/**
	 * 将字符串按token参数进行分割，然后每部分转成Integer，并存储为List<Integer>返回，注意该str参数必须符合规格（
	 * 每部分可以转成整型）。
	 * 
	 * @param str
	 * @param token
	 * @return
	 */
	public final static List<Integer> toListInteger(String str, String token) {
		List<Integer> array = new ArrayList<Integer>();
		if (str != null && str.length() > 0 && token != null
				&& token.length() > 0) {
			if (str.indexOf(token) > -1) {
				StringTokenizer st = new StringTokenizer(str, token);
				while (st.hasMoreTokens()) {
					array.add(StringConverter.toInteger(st.nextToken()));
				}
			} else {
				array.add(new Integer(str));
			}
		}
		return array;
	}

	/**
	 * 将字符串按token参数进行分割，然后每部分转成Integer，并存储为List<Integer>返回，注意该str参数必须符合规格（
	 * 每部分可以转成整型）。
	 * 
	 * @param str
	 * @param token
	 * @return
	 */
	public final static List<Long> toListLong(String str, String token) {
		List<Long> array = new ArrayList<Long>();
		if (str != null && str.length() > 0 && token != null
				&& token.length() > 0) {
			if (str.indexOf(token) > -1) {
				StringTokenizer st = new StringTokenizer(str, token);
				while (st.hasMoreTokens()) {
					array.add(StringConverter.toLong(st.nextToken()));
				}
			} else {
				array.add(new Long(str));
			}
		}
		return array;
	}

	/**
	 * 将传入的字符串按token1和token2进行分割，并按Name和Value形式存储为链接哈希表。
	 * 字符串的例子：name1#value1,name2#value2..... 对于这个例子，token1为：, token2为：#
	 * 
	 * @param options
	 * @param token1
	 * @param token2
	 * @return
	 */
	public final static LinkedHashMap<String, String> toMapByTokens(
			String options, String token1, String token2) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		String[] strArray = toArray(options, token1);
		for (int i = 0; i < strArray.length; i++) {
			String option = strArray[i];
			if (option.indexOf(token2) > -1) {
				String[] keyAndValue = toArray(option, token2);
				map.put(keyAndValue[0], keyAndValue[1]);
			} else {
				map.put(option, option);
			}
		}
		return map;
	}

	/**
	 * 将列表的每一个对象调用toString方法，然后每个字符串加上appendToken参数，将合并的字符串返回。
	 * 比如List{"a","b","c"}，传入的apendToken为","，那么返回的字符串为：a,b,c （appendToken只在内部出现）
	 * 
	 * @param list
	 * @param appendToken
	 * @return
	 */
	public final static String toString(List<?> list, String appendToken) {
		StringBuffer sb = new StringBuffer();
		int len = list.size();
		int last = len - 1;
		for (int i = 0; i < len; i++) {
			sb.append(list.get(i));
			if (i != last) {
				sb.append(appendToken);
			}
		}
		return sb.toString();
	}

	/**
	 * 将列表的每一个对象调用toString方法，然后每个字符串加上prefix和appendToken参数，将合并的字符串返回。
	 * 比如List{"a","b","c"}，传入的apendToken为","，prefix为"#"，那么返回的字符串为：#a,#b,#c
	 * （appendToken只在内部出现）
	 * 
	 * @param list
	 * @param appendToken
	 * @param prefix
	 * @return
	 */
	public final static String toString(List<String> list, String appendToken,
			String prefix) {
		StringBuffer sb = new StringBuffer();
		int len = list.size();
		int last = len - 1;
		for (int i = 0; i < len; i++) {
			sb.append(prefix + list.get(i));
			if (i != last) {
				sb.append(appendToken);
			}
		}
		return sb.toString();
	}

	/**
	 * 将字符串按splitToken进行分割，然后将每部分加上prefix，然后又加上splitToken和prefix合并为一个字符串
	 * 例如字符串为：a,b,c；splitToken为：,；prefix为：# 这样返回的结果为#a,#b,#c
	 * 主要是给jquery使用，也可以在其它合适的业务中使用。
	 * 
	 * @param str
	 * @param splitToken
	 * @param prefix
	 * @return
	 */
	public final static String toString(String str, String splitToken,
			String prefix) {
		List<String> list = toList(str, splitToken);
		return toString(list, splitToken, prefix);
	}
}
