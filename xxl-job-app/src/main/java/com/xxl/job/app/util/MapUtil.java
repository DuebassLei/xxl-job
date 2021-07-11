package com.xxl.job.app.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Map工具类
 *
 * @author  DuebassLei
 * @version 1.0
 * @date    2021/6/11
 *
 */
public class MapUtil {
	/**
	 * 忽略大小写
	 * @param map
	 * @param key
	 * @param defaultVal :默认值
	 * @return 
	 * Object
	 * @exception 
	 * @since  1.0.0
	 */
	public static Object getIgnoreCase(Map<String,Object> map, String key,Object defaultVal) {
		for (String k : map.keySet()) {
			if (key.equalsIgnoreCase(k)) {
				return map.get(k);
			}
		}
		return defaultVal;
	}
	
	public static Object getIgnoreCase(Map<String,Object> map, String key) {
		return getIgnoreCase(map, key, null);
	}
	
	public static Map<String,Object> buildMap(String key,Object val){
		 Map<String, Object> map = new HashMap<String, Object>();
		 map.put(key, val);
		 return map;
	}
	
	/**
	 * 获取Map中的字符串
	 * @param map
	 * @param key
	 * @return
	 */
	public static String getString(Map<String,Object> map, String key){
		Object object = map.get(key);
		if(object==null) {
			return null;
		}
		return object.toString();
	}
	
	public static <T> void delByStartKey(Map<String,T> map,String key){
		if(key == null){
			map.remove(null);
			return ;
		}
		Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            // 如果k刚好在要排除的key的范围中
            if ( StringUtil.isNotEmpty(k) && k.startsWith(key) ) {
                iterator.remove();
                map.remove(k);
            }
        }

	}
}
