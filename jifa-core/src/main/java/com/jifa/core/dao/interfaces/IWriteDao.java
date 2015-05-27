package com.jifa.core.dao.interfaces;

import com.jifa.core.pojo.IPojo;

import java.util.List;
import java.util.Map;

/**
 * @author june
 *         2015年04月26日 23:49
 */
public interface IWriteDao {
    /**
     * 删除
     * @param <T>
     * @param t
     */
    <T extends IPojo> void delete(T... t);
    /**
     * 新增或者修改对象
     * @param <T>
     * @param t
     */
    <T extends IPojo> List<T> saveOrUpdate(T... t);  
    
    /**
     * 根据传入hql执行复杂数据库操作.
     * @param hql String 需执行hql语句
     * @param params 参数集
     */
    void executeHQL(String hql, Map<String, Object> params);

    /**
     * 根据传入sql执行复杂数据库操作.
     * @param sql String 需执行sql语句
     * @param params 参数集
     */
    void executeSQL(String sql, Map<String, Object> params);
}
