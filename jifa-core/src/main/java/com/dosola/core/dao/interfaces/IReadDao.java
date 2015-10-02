package com.dosola.core.dao.interfaces;

import com.bstek.dorado.data.provider.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author june
 *         2015年04月26日 23:48
 */
public interface IReadDao {

    <T extends IPojo> T getObjectById(Class<T> clz, Serializable objectId);
    /**
     * 根据传入HQL语句,查询所有结果.
     * @param hql String hql语句, hql语句参数应以名称形式而不是以位置对应.
     * @param params 参数键值对
     * @return List 结果集.
     */
    List< ? > query(String hql, Map<String, Object> params);

    /**
     * 根据传入SQL语句,查询所有结果.
     * @param sql
     * @param params 参数键值对
     * @return List 结果集.
     */
    List<Map<String, Object>> queryBySql(String sql, Map<String, Object> params);

    /**
     * 根据传入SQL语句,查询所有结果.
     * @param sql
     * @param params 参数键值对
     * @param clazz 实体类class
     * @return List 结果集.
     */
    <T extends IPojo>  List<T> queryBySql(String sql, Map<String, Object> params,Class<T> clazz);

    /**
     * 分页查询 - 执行传入hql, 并将params中存放的参数放入查询条件中.
     * @param hql
     * @param pageindex 指定查询页数
     * @param pagesize 指定分页大小
     * @param params
     * @return Page 结果对象.
     */
    Page<?> pagingQuery(String hql, Integer pageindex,Integer pagesize, Map<String, Object> params);

    /**
     * 分页查询 - 执行传入sql, 并将params中存放的参数放入查询条件中.
     * @param sql
     * @param pageindex 指定查询页数
     * @param pagesize 指定分页大小
     * @param params
     * @return Page 结果对象.
     */
    Page<Map<String, Object>> pagingQueryBySql(String sql, Integer pageindex,Integer pagesize, Map<String, Object> params);

    /**
     * 分页查询 - 执行传入sql, 并将params中存放的参数放入查询条件中.
     * @param sql
     * @param pageindex 指定查询页数
     * @param pagesize 指定分页大小
     * @param params
     * @param clazz 对象类型
     * @return Page 结果对象.
     */
    <T extends IPojo> Page<T> pagingQueryBySql(String sql, Integer pageindex,Integer pagesize, Map<String, Object> params,Class<T> clazz);


    /**
     * 分页查询
     * @param page 分页对象
     * @param hql 查询hql
     * @param params 检索参数
     */
    void pagingQuery(Page<?> page, String hql ,Map<String, Object> params);

    /**
     * 分页查询，原生sql
     * @param page 分页对象
     * @param sql 查询sql
     * @param params 检索参数
     */
    void pagingQueryBySql(Page<Map<String, Object>> page, String sql ,Map<String, Object> params);

    /**
     * 分页查询，原生sql
     * @param page 分页对象
     * @param sql 查询sql
     * @param clazz 对象类型
     * @param params 检索参数
     */
    <T extends IPojo> void pagingQueryBySql(Page<T> page, String sql ,Map<String, Object> params,Class<T> clazz);


    int queryCount(String hql,Map<String, Object> params);

    int queryCountBySql(String sql,Map<String, Object> params);



}
