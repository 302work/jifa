package com.lims.view.order;

import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 申请单或报告service
 * @author Song
 * @version 1.0
 * @since 2015-10-21 23:01
 */
@Service
public class OrderService {

    @Resource
    private IMasterDao dao;

    @DataProvider
    public void queryOrder(Page<Map<String,Object>> page,Criteria criteria,Map<String,Object> parameter){
        String sql =
                " select o.*," +
                " c.name as consumerName," + //客户名称
                " c.address as consumerAddress," + //客户名称
                " s.name as standardName," + //标准名称
                " s.standardNo as standardNo " + //标准号
                " from "+Order.TABLENAME+" as o " +
                " join "+ Consumer.TABLENAME+" as c " +
                " on o.consumerId=c.id  "+
                " join "+ Standard.TABLENAME+" as s "+
                " on o.standardId=s.id " +
                " where o.isDeleted<>1 ";
        //替换客户名称参数
        Map<String,String> replaceMap = new HashMap<String,String>();
        replaceMap.put("consumerName","c.name");
        replaceMap.put("standardName","s.name");
        replaceMap.put("standardNo","s.standardNo");
        replaceMap.put("consumerAddress","c.address");

        SqlKit.replaceProperty(criteria,replaceMap);
        //构建查询条件
        ParseResult result = SqlKit.parseCriteria(criteria, true, "o", true);
        //排序
        String orderSql = SqlKit.buildOrderSql(criteria, "o");

        Map<String,Object> params = new HashMap<String, Object>();
        if(result!=null){
            sql += " AND ";
            sql += result.getAssemblySql();
            params.putAll(result.getValueMap());
        }
        if(StringUtils.isEmpty(orderSql)){
            orderSql = " ORDER BY o.crTime desc";
        }
        sql += orderSql;
        dao.pagingQueryBySql(page,sql,params);
    }

    @DataResolver
    public void saveOrder(Collection<Order> orders){

    }

    //查询订单的检测项目
    @DataProvider
    public void queryOrderProject(Page<Map<String,Object>> page,Criteria criteria,Long orderId){
        if(orderId==null){
            return;
        }
        Order order = dao.getObjectById(Order.class,orderId);
        if(order==null || StringUtils.isEmpty(order.getProjectMethodStandardIds())){
            return;
        }
        String[] projectMethodStandardIds = order.getProjectMethodStandardIds().split(",");
        String sql = "select p.*," +
                " m.name as methodStandardName," + //方法标准名称
                " m.standardNo as methodStandardNo " + //方法标准号
                " from "+ProjectMethodStandard.TABLENAME+" as pm " +
                " join "+Project.TABLENAME+" as p " +
                " on pm.projectId=p.id " +
                " join "+MethodStandard.TABLENAME+" as m " +
                " on pm.methodStandardId=m.id " +
                " where pm.id in (:projectMethodStandardIds) ";
        //替换参数
        Map<String,String> replaceMap = new HashMap<String,String>();
        replaceMap.put("methodStandardName","m.name");
        replaceMap.put("methodStandardNo","m.standardNo");
        SqlKit.replaceProperty(criteria,replaceMap);
        //构建查询条件
        ParseResult result = SqlKit.parseCriteria(criteria, true, "p", true);
        //排序
        String orderSql = SqlKit.buildOrderSql(criteria, "p");

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("projectMethodStandardIds",projectMethodStandardIds);
        if(result!=null){
            sql += " AND ";
            sql += result.getAssemblySql();
            params.putAll(result.getValueMap());
        }
        if(StringUtils.isEmpty(orderSql)){
            orderSql = " ORDER BY p.crTime desc";
        }
        sql += orderSql;
        dao.pagingQueryBySql(page,sql,params);
    }

    //查询订单的检测记录
    @DataProvider
    public void queryOrderRecord(Page<Map<String,Object>> page,Criteria criteria,Long orderId){
        if(orderId==null){
            return;
        }
        String sql = " select r.*," +
                " m.name as methodStandardName," + //方法标准名称
                " m.standardNo as methodStandardNo, " + //方法标准号
                " p.name as projectName " + //项目名称
                " from "+Record.TABLENAME+" as r " +
                " join "+Order.TABLENAME+" as o " +
                " on o.id=r.orderId " +
                " join "+ProjectMethodStandard.TABLENAME+" as pm " +
                " on pm.id=r.projectMethodStandardId " +
                " join "+Project.TABLENAME+" as p " +
                " on pm.projectId=p.id " +
                " join "+MethodStandard.TABLENAME+" as m " +
                " on pm.methodStandardId=m.id " +
                " where r.orderId=:orderId and r.isDeleted<>1 ";

        //替换参数
        Map<String,String> replaceMap = new HashMap<String,String>();
        replaceMap.put("methodStandardName","m.name");
        replaceMap.put("methodStandardNo","m.standardNo");
        replaceMap.put("projectName","p.name");
        SqlKit.replaceProperty(criteria,replaceMap);
        //构建查询条件
        ParseResult result = SqlKit.parseCriteria(criteria, true, "r", true);
        //排序
        String orderSql = SqlKit.buildOrderSql(criteria, "r");

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("orderId",orderId);
        if(result!=null){
            sql += " AND ";
            sql += result.getAssemblySql();
            params.putAll(result.getValueMap());
        }
        if(StringUtils.isEmpty(orderSql)){
            orderSql = " ORDER BY r.crTime desc";
        }
        sql += orderSql;
        dao.pagingQueryBySql(page,sql,params);
    }
}
