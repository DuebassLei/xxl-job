package com.xxl.job.app.util;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.beanutils.*;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * BeanUtils的等价类，只是将check exception改为uncheck exception
 *
 * @author  DuebassLei
 * @version 1.0
 * @date    2021/7/11
 *
 */
public class BeanUtils {
	private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);
	
	/**
	 * BeanUtil类型转换器
	 */
	public static ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();

	private static BeanUtilsBean beanUtilsBean = new BeanUtilsBean(
			convertUtilsBean, new PropertyUtilsBean());

	static {
		convertUtilsBean.register(new DateConverter(), Date.class);
		convertUtilsBean.register(new LongConverter(null), Long.class);
	}

	/**
	 * 可以用于判断 Map,Collection,String,Array,Long是否为空
	 * 
	 * @param o
	 *            java.lang.Object.
	 * @return boolean.
	 */
	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof String) {
			if (((String) o).trim().length() == 0) {
				return true;
			}
		} else if (o instanceof Collection) {
			if (((Collection<?>) o).size() == 0) {
				return true;
			}
		} else if (o.getClass().isArray()) {
			if (((Object[]) o).length == 0) {
				return true;
			}
		} else if (o instanceof Map) {
			if (((Map<?, ?>) o).size() == 0) {
				return true;
			}
		}
		else if(o instanceof Serializable) {
			return ((Serializable)o).toString().trim().length()==0;
		}
		else if(o instanceof ArrayNode) {
			ArrayNode an = (ArrayNode)o;
			return an.isEmpty(null);
		}
		return false;
	}

	/**
	 * 可以用于判断 Map,Collection,String,Array是否不为空
	 * 
	 * @param o
	 * @return
	 */
	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	/**
	 * 判断对象是否为数字
	 * 
	 * @param o
	 * @return
	 */
	public static boolean isNumber(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof Number) {
			return true;
		}
		if (o instanceof String) {
			try {
				Double.parseDouble((String) o);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}


	/**
	 * 根据指定的类名判定指定的类是否存在。
	 * 
	 * @param className
	 * @return
	 */
	public static boolean validClass(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * 判定类是否继承自父类
	 * 
	 * @param cls
	 *            子类
	 * @param parentClass
	 *            父类
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean isInherit(Class cls, Class parentClass) {
		return parentClass.isAssignableFrom(cls);
	}

	/**
	 * 输入基类包名，扫描其下的类，返回类的全路径
	 * 
	 * @param basePackages
	 *            如：com.hotent
	 * @return
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("all")
	public static List<String> scanPackages(String basePackages)
			throws IllegalArgumentException {

		ResourcePatternResolver rl = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
				rl);
		List result = new ArrayList();
		String[] arrayPackages = basePackages.split(",");
		try {
			for (int j = 0; j < arrayPackages.length; j++) {
				String packageToScan = arrayPackages[j];
				String packagePart = packageToScan.replace('.', '/');
				String classPattern = "classpath*:/" + packagePart
						+ "/**/*.class";
				Resource[] resources = rl.getResources(classPattern);
				for (int i = 0; i < resources.length; i++) {
					Resource resource = resources[i];
					MetadataReader metadataReader = metadataReaderFactory
							.getMetadataReader(resource);
					String className = metadataReader.getClassMetadata()
							.getClassName();
					result.add(className);
				}
			}
		} catch (Exception e) {
			new IllegalArgumentException("scan pakcage class error,pakcages:"
					+ basePackages);
		}

		return result;
	}

	/**
	 * java反射访问私有成员变量的值。
	 * 
	 * @param instance
	 * @param fieldName
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 *             Object
	 */
	public static Object getValue(Object instance, String fieldName)
			throws IllegalAccessException, NoSuchFieldException {

		Field field = getField(instance.getClass(), fieldName);
		// 参数值为true，禁用访问控制检查
		field.setAccessible(true);
		return field.get(instance);
	}

	/**
	 * 将字符串数据按照指定的类型进行转换。
	 * 
	 * @param typeName
	 *            实际的数据类型
	 * @param value
	 *            字符串值。
	 * @return Object
	 */
	public static Object convertByActType(String typeName, String value) {
		Object o = null;
		if ("int".equals(typeName)) {
			o = Integer.parseInt(value);
		} else if ("short".equals(typeName)) {
			o = Short.parseShort(value);
		} else if ("long".equals(typeName)) {
			o = Long.parseLong(value);
		} else if ("float".equals(typeName)) {
			o = Float.parseFloat(value);
		} else if ("double".equals(typeName)) {
			o = Double.parseDouble(value);
		} else if ("boolean".equals(typeName)) {
			o = Boolean.parseBoolean(value);
		} else if ("java.lang.String".equals(typeName)) {
			o = value;
		} else {
			o = value;
		}
		return o;
	}

	/**
	 * 根据类和成员变量名称获取成员变量。
	 * 
	 * @param thisClass
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 *             Field
	 */
	public static Field getField(Class<?> thisClass, String fieldName)
			throws NoSuchFieldException {

		if (fieldName == null) {
			throw new NoSuchFieldException("Error field !");
		}

		Field field = thisClass.getDeclaredField(fieldName);
		return field;
	}

	/**
	 * 合并两个对象。
	 * 
	 * @param srcObj
	 * @param desObj
	 *            void
	 */
	public static void mergeObject(Object srcObj, Object desObj) {
		if (srcObj == null || desObj == null) {
			return;
		}

		Field[] fs1 = srcObj.getClass().getDeclaredFields();
		Field[] fs2 = desObj.getClass().getDeclaredFields();
		for (int i = 0; i < fs1.length; i++) {
			try {
				fs1[i].setAccessible(true);
				Object value = fs1[i].get(srcObj);
				fs1[i].setAccessible(false);
				if (null != value) {
					fs2[i].setAccessible(true);
					fs2[i].set(desObj, value);
					fs2[i].setAccessible(false);
				}
			} catch (Exception e) {
				logger.error("mergeObject" + e.getMessage());
			}
		}
	}

	/**
	 * 数据列表去重
	 * @param list
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void removeDuplicate(List list)  {
	    HashSet h = new HashSet(list);
	    list.clear();
	    list.addAll(h);
	}
	


	public static <T> void listMapByPid(List<T> list, String pid,List<T> result){
		for(Map<String,Object> t: (List<Map<String,Object>>) list){
			//遍历出父id等于参数的id，add进子节点集合
			String id = (String)t.get("id");
			if (BeanUtils.isEmpty(t.get("parentId")) && pid.equals(id)){
				result.add((T) t);
			}
			if( BeanUtils.isNotEmpty(t.get("parentId")) && pid.equals(t.get("parentId"))){
				//递归遍历下一级
				listMapByPid(list,id,result);
				result.add((T) t);
			}
		}
	}

	/**
	 * 拷贝一个bean中的非空属性于另一个bean中
	 * 
	 * @param dest
	 *            目标对象
	 * @param orig
	 *            源对象
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void copyNotNullProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {

		// Validate existence of the specified beans
		if (dest == null) {
			logger.error("No destination bean specified");
			return;
		}
		if (orig == null) {
			logger.error("No origin bean specified");
			return;
		}

			// Copy the properties, converting as necessary
		if (orig instanceof DynaBean) {
			DynaProperty[] origDescriptors = ((DynaBean) orig)
					.getDynaClass().getDynaProperties();
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if (beanUtilsBean.getPropertyUtils().isReadable(orig, name)
						&& beanUtilsBean.getPropertyUtils().isWriteable(
								dest, name)) {
					Object value = ((DynaBean) orig).get(name);
					beanUtilsBean.copyProperty(dest, name, value);
				}
			}
		} else if (orig instanceof Map) {
			Iterator<?> entries = ((Map<?, ?>) orig).entrySet().iterator();
			while (entries.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) entries.next();
				String name = (String) entry.getKey();
				if (beanUtilsBean.getPropertyUtils()
						.isWriteable(dest, name)) {
					beanUtilsBean
							.copyProperty(dest, name, entry.getValue());
				}
			}
		} else /* if (orig is a standard JavaBean) */{
			PropertyDescriptor[] origDescriptors = beanUtilsBean
					.getPropertyUtils().getPropertyDescriptors(orig);
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if ("class".equals(name)) {
					continue; // No point in trying to set an object's class
				}
				if (beanUtilsBean.getPropertyUtils().isReadable(orig, name)
						&& beanUtilsBean.getPropertyUtils().isWriteable(
								dest, name)) {
					try {
						Object value = beanUtilsBean.getPropertyUtils()
								.getSimpleProperty(orig, name);
						if (value != null) {
							beanUtilsBean.copyProperty(dest, name, value);
						}
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
	
	/**
	 * 克隆对象
	 * 
	 * @param bean
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public static Object cloneBean(Object bean) throws Exception {
			return beanUtilsBean.cloneBean(bean);
	}
	
	/**
	 * 通过反射设置对象的值。
	 * 
	 * @param bean
	 * @param name
	 * @param value
	 *            void
	 */
	public static void setProperty(Object bean, String name, Object value) {
		try {
			beanUtilsBean.setProperty(bean, name, value);
		} catch (Exception e) {
			handleReflectionException(e);
		}
	}
	
	private static void handleReflectionException(Exception e) {
		ReflectionUtils.handleReflectionException(e);
	}
}
