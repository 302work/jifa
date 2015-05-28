/**
 * 
 */
package com.dorado.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 构建sql结果
 * @author june
 * 2015年1月21日 下午3:07:20
 * 
 */
public class ParseResult {
	
	private Map<String,Object> valueMap = new LinkedHashMap<String,Object>();
	
	private StringBuffer assemblySql = new StringBuffer();
	//所有的参数值
	private List<Object> valueList = new ArrayList<Object>();
	
	public Map<String, Object> getValueMap() {
		return valueMap;
	}
	
	public void setValueMap(Map<String, Object> valueMap) {
		this.valueMap = valueMap;
	}
	/**
	 * 构建的sql
	 * @return
	 */
	public StringBuffer getAssemblySql() {
		return assemblySql;
	}
	public void setAssemblySql(StringBuffer assemblySql) {
		this.assemblySql = assemblySql;
	}
	
	/**
	 * 获取所有的参数值
	 * @return
	 */
	public List<Object> getValueList() {
		return valueList;
	}

	public void setValueList(List<Object> valueList) {
		this.valueList = valueList;
	}
	
}
