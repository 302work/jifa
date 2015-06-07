/**
 * 
 */
package com.dorado.common;

import com.bstek.dorado.data.provider.*;
import com.bstek.dorado.data.provider.filter.FilterOperator;
import com.bstek.dorado.data.provider.filter.SingleValueFilterCriterion;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 构建sql相关的辅助类
 * 
 * @author june 2015年1月21日 下午3:05:47
 * 
 */
public class SqlKit {
	/**
	 * @param criteria
	 *            要解析的目标Criteria对象
	 * @param useParameterName
	 *            在接装查询条件时是否采用参数名
	 * @param alias
	 *            别名字符串
	 * @return 
	 *         ParseResult对象，其中包含解析生成SQL拼装对象以及查询条件的值对象Map,其中key为查询字段名,value为具体条件值
	 */
	public static ParseResult parseCriteria(Criteria criteria,
			boolean useParameterName, String alias) {
		int parameterNameCount = 0;
		if (criteria == null || criteria.getCriterions().size() == 0) {
			return null;
		}
		ParseResult result = new ParseResult();
		StringBuffer sb = result.getAssemblySql();
		Map<String, Object> valueMap = result.getValueMap();
		List<Object> valueList = result.getValueList();
		
		int count = 0;
		for (Criterion c : criteria.getCriterions()) {
			if (count > 0) {
				sb.append(" and ");
			}
			count++;
			parameterNameCount = buildCriterion(sb, c, valueMap,valueList,
					parameterNameCount, useParameterName, alias);
		}
		return result;
	}

	private static int buildCriterion(StringBuffer sb, Criterion c,
			Map<String, Object> valueMap,List<Object> valueList, int parameterNameCount,
			boolean useParameterName, String alias) {
		
		if (c instanceof SingleValueFilterCriterion) {
			parameterNameCount++;
			SingleValueFilterCriterion fc = (SingleValueFilterCriterion) c;
			String operator = buildOperator(fc.getFilterOperator());
			String propertyName = fc.getProperty();
			if (StringUtils.isNotEmpty(alias)) {
				sb.append(" " + alias + "." + propertyName);
			} else {
				sb.append(" " + propertyName);
			}
			sb.append(" " + processLike(operator) + " ");
			String paramName = fc.getProperty();
			if (paramName.indexOf(".") != -1) {
				paramName = paramName.replaceAll("\\.", "_");
			}
			String prepareName = paramName + "_" + parameterNameCount + "_";
			if (useParameterName) {
				sb.append(" :" + prepareName + " ");
			} else {
				sb.append(" ? ");
			}
			if (operator.equals("like")) {
				valueMap.put(prepareName, "%" + fc.getValue() + "%");
				valueList.add("%" + fc.getValue() + "%");
			} else if (operator.startsWith("*")) {
				valueMap.put(prepareName, "%" + fc.getValue());
				valueList.add("%" + fc.getValue());
			} else if (operator.endsWith("*")) {
				valueMap.put(prepareName, fc.getValue() + "%");
				valueList.add(fc.getValue() + "%");
			} else {
				valueMap.put(prepareName, fc.getValue());
				valueList.add(fc.getValue());
			}
		}
		if (c instanceof Junction) {
			Junction jun = (Junction) c;
			String junction = " and ";
			if (jun instanceof Or) {
				junction = " or ";
			}
			int count = 0;
			Collection<Criterion> criterions = jun.getCriterions();
			if (criterions != null) {
				sb.append(" ( ");
				for (Criterion criterion : criterions) {
					if (count > 0) {
						sb.append(junction);
					}
					count++;
					parameterNameCount = buildCriterion(sb, criterion,
							valueMap,valueList, parameterNameCount, useParameterName,
							alias);
				}
				sb.append(" ) ");
			}
		}
		return parameterNameCount;
	}

	protected static String buildOperator(FilterOperator filterOperator) {
		String operator = "like";
		if (filterOperator != null) {
			operator = filterOperator.toString();
		}
		return operator;
	}

	private static String processLike(String operator) {
		String result = operator;
		if (operator.endsWith("*")) {
			result = operator.substring(0, operator.length() - 1);
		}
		if (operator.startsWith("*")) {
			result = operator.substring(1, operator.length());
		}
		return result;
	}

	/**
	 * 获取排序sql
	 * 
	 * @param criteria
	 * @param alias
	 * @return
	 */
	public static String buildOrderSql(Criteria criteria, String alias) {
		if (criteria == null || criteria.getOrders().size() == 0) {
			return "";
		}
		StringBuffer orderSb = new StringBuffer();
		int num = 0;
		orderSb.append(" order by ");
		for (Order order : criteria.getOrders()) {
			if (num > 0) {
				orderSb.append(" , ");
			}
			if (StringUtils.isNotEmpty(alias)) {
				orderSb.append(" " + alias + ".`" + order.getProperty() + "` "
						+ (order.isDesc() ? "desc" : "asc") + " ");
			} else {
				orderSb.append(" `" + order.getProperty() + "` "
						+ (order.isDesc() ? "desc" : "asc") + " ");
			}

		}
		return orderSb.toString();
	}
	
	/**
	 * 获取排序hql
	 * 
	 * @param criteria
	 * @param alias
	 * @return
	 */
	public static String buildOrderHql(Criteria criteria, String alias) {
		if (criteria == null || criteria.getOrders().size() == 0) {
			return "";
		}
		StringBuffer orderSb = new StringBuffer();
		int num = 0;
		orderSb.append(" order by ");
		for (Order order : criteria.getOrders()) {
			if (num > 0) {
				orderSb.append(" , ");
			}
			if (StringUtils.isNotEmpty(alias)) {
				orderSb.append(" " + alias + "." + order.getProperty() + ""
						+ (order.isDesc() ? "desc" : "asc") + " ");
			} else {
				orderSb.append(" " + order.getProperty() + " "
						+ (order.isDesc() ? "desc" : "asc") + " ");
			}

		}
		return orderSb.toString();
	}


	/**
	 * 替换前台传来的属性名称
	 * 
	 * @param propertyMap
	 * @param criteria
	 * @return
	 */
	public static void replaceProperty(Criteria criteria,
			Map<String, String> propertyMap) {
		if (criteria == null
				|| (criteria.getCriterions().size() == 0 && criteria
						.getOrders().size() == 0) || propertyMap == null
				|| propertyMap.isEmpty()) {
			return;
		}
		for (Criterion criterion : criteria.getCriterions()) {
			if (criterion instanceof SingleValueFilterCriterion) {
				SingleValueFilterCriterion fc = (SingleValueFilterCriterion) criterion;
				String propertyName = fc.getProperty();
				String newPropertyName = propertyMap.get(propertyName);
				if (StringUtils.isNotEmpty(newPropertyName)) {
					fc.setProperty(newPropertyName);
				}
			}
		}
		for (Order order : criteria.getOrders()) {
			String property = order.getProperty();
			String newProperty = propertyMap.get(property);
			if (StringUtils.isNotEmpty(newProperty)) {
				order.setProperty(newProperty);
			}
		}
	}
	
	/**
	 * 解析检索参数
	 * @param modelClass 实体类的class
	 * @param parameter 检索参数
	 * @param alias 别名
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static ParseResult parseParameterMap(Class<? extends Model> modelClass,Map<String,Object> parameter, String alias) {
		if(parameter==null || parameter.isEmpty()){
			return null;
		}
		Table table = TableMapping.me().getTable(modelClass);
		
		Map<String, Class<?>> columnTypeMap = table.getColumnTypeMap();
		Iterator<String> keys = parameter.keySet().iterator();
		
		ParseResult result = new ParseResult();
		StringBuffer sb = result.getAssemblySql();
		Map<String, Object> valueMap = result.getValueMap();
		List<Object> valueList = result.getValueList();
		int count = 0;
		while(keys.hasNext()){
			String key = keys.next();
			Object value = parameter.get(key);
			//空值不处理
			if(value==null || StringUtils.isEmpty(value.toString())){
				continue;
			}
			// 判断是否有重名字段(重名字段：表名 + “-” + 字段名)
			if(key.toString().contains("-")){
				String[] str = key.toString().split("-");
				if(table.getName().equals(str[0])){
					key = str[1];
				}else{
					continue;
				}
			}
			//该字段的类型
			Class classType = columnTypeMap.get(key);
			if(classType==null){
				continue;
			}
			if (count > 0) {
				sb.append(" and ");
			}
			String colClassName = classType.getName();
			if(StringUtils.isNotEmpty(alias)){
				sb.append(" "+alias+".`"+key+"`");
			}else{
				sb.append(" `"+key+"`");
			}
			if ("java.lang.String".equals(colClassName)) {
				sb.append(" like ? ");
				valueMap.put(key, "%" + value.toString() + "%");
				valueList.add("%" +value.toString() + "%");
			}
			else if ("java.sql.Date".equals(colClassName) || "java.sql.Timestamp".equals(colClassName)) {
				sb.append(" like ? ");
				valueMap.put(key, "%" + value.toString() + "%");
				valueList.add("%" +value.toString() + "%");
			}else{
				sb.append(" = ? ");
				valueMap.put(key, value);
				valueList.add(value);
			}
			count++;
		}
		return result;
	}
	
	/**
	 * 解析检索参数
	 * @param modelClasses 实体类集合的classes
	 * @param parameter 检索参数
	 * @param alias 别名数组
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static ParseResult parseParameterMap(ArrayList<Class<? extends Model>> modelClasses,Map<String,Object> parameter, String[] alias) {
		int count = 0;
		StringBuffer sb = new StringBuffer();
		List<Object> paramList = new ArrayList<Object>();
		ParseResult result = new ParseResult();
		for(Class<? extends Model> modelClass : modelClasses){
			ParseResult mapResult = SqlKit.parseParameterMap(modelClass, parameter, alias[count]);
			if(mapResult!=null && StringUtils.isNotEmpty(mapResult.getAssemblySql().toString())){
				sb.append(" and ");
				sb.append(mapResult.getAssemblySql());
				paramList.addAll(mapResult.getValueList());
			}
			count++;
		}
		result.setAssemblySql(sb);
		result.setValueList(paramList);
		return result;
	}
}
