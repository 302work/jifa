package com.dosola.core.dao;

import com.bstek.dorado.data.provider.Page;
import com.dosola.core.common.DosolaUtil;
import com.dosola.core.common.PojoKit;
import com.dosola.core.dao.base.AbstractDao;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.dosola.core.dao.interfaces.IPojo;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author june
 * 2015年04月26日 23:47
 */

public class MasterDao extends AbstractDao implements IMasterDao {


    @SuppressWarnings("unchecked")
	@Override
    public <T extends IPojo> T getObjectById(Class<T> clz, Serializable objectId){
        T result = null;
        Session session = null;
        try {
        	session = this.getSession();
        	result = (T) this.getSession().get(clz, objectId);
        }catch(Exception e){
            throw e;
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
        Session session = null;
        try {
        	session = this.getSession();
        	Query query = session.createQuery(hql);
    		query = this.setParameter(query, params);
            result = query.list();
        }catch(Exception e){
            throw e;
        }
        finally {
            if ( null != session ) {
                this.releaseSession(session);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Map<String, Object>> queryBySql(String sql, Map<String, Object> params) {
    	Session session = null;
        List<Map<String, Object>> resultList = null;
        try {
        	session = this.getSession();
        	SQLQuery query = session.createSQLQuery(sql);
            query = (SQLQuery) this.setParameter(query, params);
            query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            resultList =  query.list();
        }catch(Exception e){
            throw e;
        } finally {
            if (null != session) {
                this.releaseSession(session);
            }
        }
        return resultList;
    }

    @Override
    public <T extends IPojo> List<T> queryBySql(String sql, Map<String, Object> params, Class<T> clazz) {
        List<Map<String, Object>> list = queryBySql(sql,params);
        if(list!=null && list.size()>0){
            return PojoKit.build(clazz,list);
        }
        return new ArrayList<T>();
    }

    @SuppressWarnings("rawtypes")
	@Override
    public Page<?> pagingQuery(String hql, Integer pageIndex, Integer pageSize, Map<String, Object> params) {
        Page<?> page  = new Page(pageSize,pageIndex);
        pagingQuery(page,hql,params);
        return page;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Page<Map<String, Object>> pagingQueryBySql(String sql, Integer pageIndex, Integer pageSize, Map<String, Object> params) {
        Page<Map<String, Object>> page  = new Page(pageSize,pageIndex);
        pagingQueryBySql(page, sql, params);
        return page;
    }

    @Override
    public <T extends IPojo> Page<T> pagingQueryBySql(String sql, Integer pageIndex, Integer pageSize, Map<String, Object> params, Class<T> clazz) {
        Page<T> returnPage = new Page(pageSize,pageIndex);
        pagingQueryBySql(returnPage,sql,params,clazz);
        return returnPage;
    }

    @SuppressWarnings("unchecked")
	@Override
    public void pagingQuery(Page<?> page, String hql, Map<String, Object> params) {
        int pageIndex = page.getPageNo();
        int pageSize = page.getPageSize();
        int totalCount = 0;
        Session session = null;
        try {
            session = this.getSession();
            Query query = session.createQuery(hql);
            query = this.setParameter(query, params);
            query = this.setPageProperty(query, pageSize, pageIndex);
            totalCount = this.queryCount(hql, params);
            page.setEntities(query.list());
            page.setEntityCount(totalCount);
        }catch(Exception e){
            throw e;
        } finally {
            if ( null != session ) {
                this.releaseSession(session);
            }
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public void pagingQueryBySql(Page<Map<String, Object>> page, String sql, Map<String, Object> params) {
        int pageIndex = page.getPageNo();
        int pageSize = page.getPageSize();
        Session session = null;
        int totalCount = 0;
        try {
        	 session = this.getSession();
             SQLQuery query = session.createSQLQuery(sql);
             query = (SQLQuery) this.setParameter(query, params);
             query = (SQLQuery) this.setPageProperty(query, pageSize, pageIndex);
             query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
             totalCount = this.queryCountBySql(sql, params);
             page.setEntities(query.list());
             page.setEntityCount(totalCount);
        }catch(Exception e){
            throw e;
        } finally {
            if (null != session) {
                this.releaseSession(session);
            }
        }
    }

    @Override
    public <T extends IPojo> void pagingQueryBySql(Page<T> page, String sql, Map<String, Object> params, Class<T> clazz) {
        Page<Map<String, Object>> pageMap  = pagingQueryBySql(sql,page.getPageNo(),page.getPageSize(),params);
        if(pageMap==null || pageMap.getEntityCount()==0){
            return;
        }
        List<T> list = new ArrayList<T>();
        for(Map<String, Object> map : pageMap.getEntities()){
            list.add(PojoKit.build(clazz,map));
        }
        page.setEntities(list);
        page.setEntityCount(pageMap.getEntityCount());
    }

    @Override
    public int queryCount(String hql, Map<String, Object> params) {
        int objectCount =0;
        Session session = null;
        try {
        	session = this.getSession();
            String countHql =  DosolaUtil.getCountHQL(hql);
            Query query = session.createQuery(countHql);
            query = this.setParameter(query, params);
            objectCount =((Long)query.uniqueResult()).intValue();
        }catch(Exception e){
            throw e;
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
        	session = this.getSession();
            SQLQuery query = session.createSQLQuery("SELECT COUNT(*) a FROM ("+sql+") b");
            query = (SQLQuery) this.setParameter(query, params);
            totalCount = ((BigInteger) query.uniqueResult()).intValue();
        }catch(Exception e){
            throw e;
        } finally {
            if (null != session) {
                this.releaseSession(session);
            }
        }
        return totalCount;
    }

    @SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
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
            query = this.setParameter(query, params);
            query.executeUpdate();
        }catch(Exception e){
            throw e;
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
			session = this.getSession(true);
            SQLQuery query = session.createSQLQuery(sql);
            query = (SQLQuery) this.setParameter(query, params);
            query.executeUpdate();
        }catch(Exception e){
            throw e;
        } finally {
            if ( null != session ) {
            	session.flush();
            	this.releaseSession(session);
            }
        }

    }

}
