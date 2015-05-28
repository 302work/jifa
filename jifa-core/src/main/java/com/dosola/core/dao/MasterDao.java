package com.dosola.core.dao;

import com.bstek.dorado.data.provider.Page;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.dosola.core.dao.interfaces.IPojo;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;


/**
 * @author june
 *         2015年04月26日 23:47
 */

public class MasterDao extends HibernateDaoSupport implements IMasterDao {


    @Override
    public <T extends IPojo> T getObjectById(Class<T> clz, Serializable objectId){
        T result = null;
        Session session = this.getSession();
        try {
            result = (T) this.getSession().get(clz, objectId);
        } finally {
            if ( null != session ) {
                this.releaseSession(session);
            }
        }
        return result;
    }

    @Override
    public List<?> query(String hql, Map<String, Object> params) {
        List <?> result = null;
        Session session = this.getSession();
        try {
            Query query = session.createQuery(hql);
            String[] paramNames = query.getNamedParameters();
            if ( !ArrayUtils.isEmpty(paramNames) && params!=null && !params.isEmpty()) {
                for ( String paramName : paramNames ) {
                    Object value = params.get(paramName);
                    query.setParameter(paramName, value);
                }
            }
            result = query.list();
        } finally {
            if ( null != session ) {
                this.releaseSession(session);
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> queryBySql(String sql, Map<String, Object> params) {
        Session session = null;
        List<Map<String, Object>> resultList = null;
        try {
            session = this.getHibernateTemplate().getSessionFactory()
                    .getCurrentSession();
            SQLQuery query = session.createSQLQuery(sql);
            if (!CollectionUtils.isEmpty(params)) {
                for(String param:params.keySet()){
                    query.setParameter(param, params.get(param));
                }
            }
            query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            resultList =  query.list();
        } finally {
            if (null != session) {
                this.releaseSession(session);
            }
        }
        return resultList;
    }

    @Override
    public Page<?> pagingQuery(String hql, Integer pageIndex, Integer pageSize, Map<String, Object> params) {
        Page<?> page  = new Page(pageSize,pageIndex);
        pagingQuery(page,hql,params);
        return page;
    }

    @Override
    public Page<Map<String, Object>> pagingQueryBySql(String sql, Integer pageIndex, Integer pageSize, Map<String, Object> params) {
        Page<Map<String, Object>> page  = new Page(pageSize,pageIndex);
        pagingQueryBySql(page, sql, params);
        return page;
    }

    @Override
    public void pagingQuery(Page<?> page, String hql, Map<String, Object> params) {
        int pageIndex = page.getPageNo();
        int pageSize = page.getPageSize();
        int beginIndex, totalCount = 0;
        Session session = null;
        try {
            session = this.getSession();
            Query  searchQuery;
            searchQuery = session.createQuery(hql);
            String[] queryParams = searchQuery.getNamedParameters();
            if ( !ArrayUtils.isEmpty(queryParams) && !CollectionUtils.isEmpty(params)) {
                for ( String queryParam : queryParams ) {
                    Object value = params.get(queryParam);
                    searchQuery.setParameter(queryParam, value);
                }
            }
            totalCount = this.queryCount(hql, params);
            beginIndex = Integer.valueOf((pageIndex-1) * pageSize);
            searchQuery.setFirstResult(beginIndex);
            searchQuery.setMaxResults(pageSize);
            page.setEntities(searchQuery.list());
            page.setEntityCount(totalCount);
        } finally {
            if ( null != session ) {
                this.releaseSession(session);
            }
        }
    }

    @Override
    public void pagingQueryBySql(Page<Map<String, Object>> page, String sql, Map<String, Object> params) {
        int pageIndex = page.getPageNo();
        int pageSize = page.getPageSize();
        Session session = null;
        int beginIndex = 0, totalCount = 0;
        try {
            session = this.getHibernateTemplate().getSessionFactory()
                    .getCurrentSession();
            SQLQuery query = session.createSQLQuery(sql);
            if (!CollectionUtils.isEmpty(params)) {
                for(String param:params.keySet()){
                    query.setParameter(param, params.get(param));
                }
            }
            totalCount = this.queryCountBySql(sql,params);
            beginIndex = Integer.valueOf((pageIndex-1) * pageSize);
            query.setFirstResult(beginIndex);
            query.setMaxResults(pageSize);
            query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            page.setEntities(query.list());
            page.setEntityCount(totalCount);
        } finally {
            if (null != session) {
                this.releaseSession(session);
            }
        }
    }

    @Override
    public int queryCount(String hql, Map<String, Object> params) {
        int objectCount =0;
        Session session = null;
        try {
            session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();
            Query countQuery;
            String countSB = "SELECT COUNT(*) " + hql;
            countQuery = session.createQuery(countSB.toString());
            String[] queryParams = countQuery.getNamedParameters();
            if ( !ArrayUtils.isEmpty(queryParams) && !CollectionUtils.isEmpty(params)) {
                for ( String queryParam : queryParams ) {
                    countQuery.setParameter(queryParam, params.get(queryParam));
                }
            }
            objectCount =((Long)countQuery.uniqueResult()).intValue();
        } finally {
            if ( null != session ) {
                this.releaseSession(session);
            }
        }
        return objectCount;
    }

    @Override
    public int queryCountBySql(String sql, Map<String, Object> params) {
        Session session = null;
        int totalCount = 0;
        try {
            session = this.getHibernateTemplate().getSessionFactory().getCurrentSession();
            SQLQuery query = session.createSQLQuery("SELECT COUNT(*) a FROM ("+sql+") b");
            if (!CollectionUtils.isEmpty(params)) {
                for(String param:params.keySet()){
                    query.setParameter(param, params.get(param));
                }
            }
            totalCount = ((BigInteger) query.uniqueResult()).intValue();
        } finally {
            if (null != session) {
                this.releaseSession(session);
            }
        }
        return totalCount;
    }

    @Override
    public <T extends IPojo> void delete(T... ts) {
        for ( IPojo t : ts ) {
            if ( null == t ) {
                continue;
            }
            this.getHibernateTemplate().clear();
            this.getHibernateTemplate().delete(t);
            this.getHibernateTemplate().flush();
        }
    }

	@Override
    public <T extends IPojo> List<T> saveOrUpdate(T... ts) {
        List<T> returnObjs = new ArrayList < T > ();
        for ( T t : ts ) {
            if ( null == t ) {
                continue;
            }
            this.getHibernateTemplate().clear();
            returnObjs.add(this.getHibernateTemplate().merge(t));
            this.getHibernateTemplate().flush();
        }
        return returnObjs;
    }

    @Override
    public void executeHQL(String hql, Map<String, Object> params) {
        Session session = null;
        try {
            session = this.getSession(true);
            Query query = session.createQuery(hql);
            String[] paramNames = query.getNamedParameters();
            if (!CollectionUtils.isEmpty(params)) {
                for ( String paramName : paramNames ) {
                    query.setParameter(paramName, params.get(paramName));
                }
            }
            query.executeUpdate();
        } finally {
        	if ( null != session ) {
                session.flush();
        		this.releaseSession(session);
            }
        }
    }

    @Override
    public void executeSQL(String sql, Map<String, Object> params) {
        Session session = null;
        try {
//			session = this.getHibernateTemplate().getSessionFactory()
//					.getCurrentSession();
            //更改为支持多线程调用
            session = this.getHibernateTemplate().getSessionFactory().openSession();
            SQLQuery query = session.createSQLQuery(sql);
            if (!CollectionUtils.isEmpty(params)) {
                for(String param:params.keySet()){
                    query.setParameter(param, params.get(param));
                }
            }
            query.executeUpdate();
        } finally {
            if ( null != session ) {
            	session.flush();
            	this.releaseSession(session);
            }
        }

    }

}
