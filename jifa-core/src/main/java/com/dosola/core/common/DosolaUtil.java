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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 公用方法
 * 
 * @author june 2014年11月28日 下午4:36:40
 * 
 */
public class DosolaUtil {
	/**
	 * 把map的值更新到model里
	 * 
	 * @param objMap
	 * @param t
	 * @return
	 */
	public static <T extends Model<?>> T updateModelByMap(
			Map<String, Object> objMap, T t) {
		if (objMap == null || objMap.isEmpty() || t == null) {
			return null;
		}
		Table table = TableMapping.me().getTable(t.getClass());
		Map<String, Class<?>> columnTypeMap = table.getColumnTypeMap();
		Iterator<String> keys = columnTypeMap.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			t.set(key, objMap.get(key));
		}
		return t;
	}

	/**
	 * 校验手机号
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		if (StringUtils.isEmpty(mobiles)) {
			return false;
		}
		Pattern p = Pattern.compile(DosolaConstants.PhoneRegExp);
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 校验验证码是否为6位数字
	 * 
	 * @param code
	 * @return
	 */
	public static boolean isCodeNO(String code) {
		if (StringUtils.isEmpty(code)) {
			return false;
		}
		Pattern p = Pattern.compile(DosolaConstants.CodeRegExp);
		Matcher m = p.matcher(code);
		return m.matches();
	}
	
	/**
	 * 将一个 Map 对象转化为一个 JavaBean
	 * 
	 * @param obj
	 *            要转化的对象
	 * @param map
	 *            包含属性值的 map
	 * @return 转化出来的 JavaBean 对象
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InstantiationException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */
	@SuppressWarnings("rawtypes")
	public static Object convertMap(Object obj, Map map) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException{
		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass()); // 获取类属性
		// 给 JavaBean 对象的属性赋值
		PropertyDescriptor[] propertyDescriptors = beanInfo
				.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
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
	 * 将一个 Map 对象转化为一个 JavaBean
	 * 
	 * @param type
	 *            要转化的类型
	 * @param map
	 *            包含属性值的 map
	 * @return 转化出来的 JavaBean 对象
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InstantiationException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */
	@SuppressWarnings("rawtypes")
	public static Object convertMap(Class type, Map map)
			throws IntrospectionException, IllegalAccessException,
			InstantiationException, InvocationTargetException {
		Object obj = type.newInstance(); // 创建 JavaBean 对象
		return convertMap(obj, map);
	}

	/**
	 * 将一个 JavaBean 对象转化为一个 Map
	 * 
	 * @param bean
	 *            要转化的JavaBean 对象
	 * @return 转化出来的 Map 对象
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map convertBean(Object bean) throws IntrospectionException,
			IllegalAccessException, InvocationTargetException {
		Class type = bean.getClass();
		Map returnMap = new HashMap();
		BeanInfo beanInfo = Introspector.getBeanInfo(type);

		PropertyDescriptor[] propertyDescriptors = beanInfo
				.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
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

	/**
	 * 通用获取查询数量的hql
	 * @param hql
	 * @return
	 */
	public static String getCountHQL(String hql) {
		if (StringUtils.isEmpty(hql)) {
			return null;
		}
		hql = removeDistinct(hql);
		hql = removeByKey(hql, "group by");
		hql = removeByKey(hql, "order by");
		hql = removeSubSelect(hql);
		int from = hql.toLowerCase().indexOf("from");
		return "select count(*) " + hql.substring(from);
	}

	/**
	 * 移除key指定的子句，如order by,group by
	 */
	private static String removeByKey(String hql, String key) {
		int index = hql.toLowerCase().indexOf(key.toLowerCase());
		if (index == -1) {
			return hql;
		}
		return hql.replace(hql.substring(index), "");
	}

	/**
	 * 移除distinct关键字
	 */
	private static String removeDistinct(String hql) {
		if (hql.toLowerCase().indexOf("distinct") == -1) {
			return hql;
		}
		return hql.replace("distinct", "");
	}

	/**
	 * 移除select部分子查询
	 * 
	 * @param hql
	 *            传入的hql语句
	 */
	private static String removeSubSelect(String hql) {
		String regex = "[^in\\s]+\\([^)]*([^(^)]*?)\\)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(hql);
		while (m.find()) {
			String sub = m.group();
			hql = hql.replace(sub, "");
		}
		return hql;
	}
	 public static String replaceFormatSqlOrderBy(String sql) {
        sql = sql.replaceAll("(\\s)+", " ");
        int index = sql.toLowerCase().lastIndexOf("order by");
        if (index > sql.toLowerCase().lastIndexOf(")")) {
            String sql1 = sql.substring(0, index);
            String sql2 = sql.substring(index);
            sql2 = sql2.replaceAll("[oO][rR][dD][eE][rR] [bB][yY] [\u4e00-\u9fa5a-zA-Z0-9_.]+((\\s)+(([dD][eE][sS][cC])|([aA][sS][cC])))?(( )*,( )*[\u4e00-\u9fa5a-zA-Z0-9_.]+(( )+(([dD][eE][sS][cC])|([aA][sS][cC])))?)*", "");
            return sql1 + sql2;
        }
        return sql;
    }
	public static void main(String[] args) {
		String sql = "SELECT tmp1.id, tmp1.sendid, tmp1.senduser, tmp1.receiveid, tmp1.receiveuser, tmp1.message, tmp1.createtime, CASE WHEN tmp2.newtotal IS NULL THEN 0 ELSE tmp2.newtotal END AS newtotal FROM ( SELECT * FROM haierapp_ucenter_msg a WHERE receiveuser = 'maidixiaojun'   AND '2012-03-19 15:36:48'< createtime AND id = ( SELECT max(id) FROM haierapp_ucenter_msg WHERE senduser = a.senduser and receiveuser = 'maidixiaojun' ) UNION select * from (SELECT * FROM haierapp_ucenter_msg WHERE senduser = 'maidixiaojun' AND sendstatus = 1 AND '2012-03-19 15:36:48'< createtime AND receiveuser NOT IN ( SELECT senduser FROM haierapp_ucenter_msg WHERE receiveuser = 'maidixiaojun'   AND '2012-03-19 15:36:48'< createtime) order by id desc) a GROUP BY receiveuser ) tmp1 LEFT JOIN ( SELECT senduser, sum(isnew) AS newtotal FROM haierapp_ucenter_msg a WHERE receiveuser = 'maidixiaojun' GROUP BY senduser ) tmp2 ON tmp1.senduser = tmp2.senduser ORDER BY id DESC";
		System.out.println(replaceFormatSqlOrderBy(sql));
	}
   
}
