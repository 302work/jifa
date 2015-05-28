/**
 * 
 */
package com.dosola.core.common;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.Map;
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
}
