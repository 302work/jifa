package com.dosola.core.dao.base;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * 
 * 基础dao
 * 
 * @author june 2015年06月07日 10:47
 * 
 */
public abstract class AbstractDao extends HibernateDaoSupport {

	/**
	 * 设置检索参数
	 * @param query
	 * @param params
	 * @return
	 */
	protected Query setParameter(Query query, Map<String, Object> params) {
		if (params != null) {
			Set<String> keySet = params.keySet();
			for (String string : keySet) {
				Object obj = params.get(string);
				if (obj instanceof Collection<?>) {
					query.setParameterList(string, (Collection<?>) obj);
				} else if (obj instanceof Object[]) {
					query.setParameterList(string, (Object[]) obj);
				} else {
					query.setParameter(string, obj);
				}
			}
		}
		return query;
	} 

	/**
	 * 设置分页检索参数
	 * @param query
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	protected Query setPageProperty(Query query, int pageSize, int pageIndex) {
		if (pageIndex != 0 && pageSize != 0) {
			query.setFirstResult((pageIndex - 1) * pageSize);
			query.setMaxResults(pageSize);
		}
		return query;
	}
}
