/**
 * 
 */
package com.dosola.core.common;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import org.apache.commons.lang.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公用方法
 * @author june
 * 2014年11月28日 下午4:36:40
 * 
 */
public class DosolaUtil {
	/**
	 * 把map的值更新到model里
	 * @param objMap
	 * @param t
	 * @return
	 */
	public static <T extends Model<?>> T updateModelByMap(Map<String,Object> objMap,T t){
		if(objMap==null || objMap.isEmpty() || t==null){
			return null;
		}
		Table table = TableMapping.me().getTable(t.getClass());
		Map<String, Class<?>> columnTypeMap = table.getColumnTypeMap();
		Iterator<String> keys = columnTypeMap.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			t.set(key, objMap.get(key));
		}
		return t;
	}
	/**
	 * 校验手机号
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles){  
		if(StringUtils.isEmpty(mobiles)){
			return false;
		}
		Pattern p = Pattern.compile(DosolaConstants.PhoneRegExp);  
		Matcher m = p.matcher(mobiles);  
		return m.matches();  
	} 
	/**
	 * 校验验证码是否为6位数字
	 * @param code
	 * @return
	 */
	public static boolean isCodeNO(String code){
		if(StringUtils.isEmpty(code)){
			return false;
		}
		Pattern p = Pattern.compile(DosolaConstants.CodeRegExp);  
		Matcher m = p.matcher(code);  
		return m.matches();  
	}

	/**
	 * 将一个 Map 对象转化为一个 JavaBean
	 * @param type 要转化的类型
	 * @param map 包含属性值的 map
	 * @return 转化出来的 JavaBean 对象
	 * @throws IntrospectionException 如果分析类属性失败
	 * @throws IllegalAccessException 如果实例化 JavaBean 失败
	 * @throws InstantiationException 如果实例化 JavaBean 失败
	 * @throws InvocationTargetException 如果调用属性的 setter 方法失败
	 */
	@SuppressWarnings("rawtypes")
	public static Object convertMap(Class type, Map map)
			throws IntrospectionException, IllegalAccessException,
			InstantiationException, InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
		Object obj = type.newInstance(); // 创建 JavaBean 对象

		// 给 JavaBean 对象的属性赋值
		PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
		for (int i = 0; i< propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();

			if (map.containsKey(propertyName)) {
				// 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
				Object value = map.get(propertyName);

				Object[] args = new Object[1];
				args[0] = value;

				descriptor.getWriteMethod().invoke(obj, args);
			}
		}
		return obj;
	}

	/**
	 * 将一个 JavaBean 对象转化为一个  Map
	 * @param bean 要转化的JavaBean 对象
	 * @return 转化出来的  Map 对象
	 * @throws IntrospectionException 如果分析类属性失败
	 * @throws IllegalAccessException 如果实例化 JavaBean 失败
	 * @throws InvocationTargetException 如果调用属性的 setter 方法失败
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map convertBean(Object bean)
			throws IntrospectionException, IllegalAccessException, InvocationTargetException {
		Class type = bean.getClass();
		Map returnMap = new HashMap();
		BeanInfo beanInfo = Introspector.getBeanInfo(type);

		PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
		for (int i = 0; i< propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!propertyName.equals("class")) {
				Method readMethod = descriptor.getReadMethod();
				Object result = readMethod.invoke(bean, new Object[0]);
				if (result != null) {
					returnMap.put(propertyName, result);
				} else {
					returnMap.put(propertyName, "");
				}
			}
		}
		return returnMap;
	}

}
