package com.lims.view.order;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.bstek.uflo.client.service.ProcessClient;
import com.bstek.uflo.service.StartProcessInfo;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
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

    @Resource(name=ProcessClient.BEAN_ID)
    private ProcessClient processClient;

    @DataProvider
    public void queryOrder(Page<Map<String,Object>> page,Criteria criteria,Long businessId){
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
        //审核订单时
        if(businessId!=null && businessId>0){
            sql += " and o.id=:businessId";
            params.put("businessId",businessId);
        }
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
        for (Order  order: orders) {
            EntityState state = EntityUtils.getState(order);
            IUser user = ContextHolder.getLoginUser();
            String userName = user.getUsername();
            if (EntityState.NEW.equals(state)) {
                order.setCrTime(new Date());
                order.setCrUser(userName);
                order.setOrderNo(generatorOrderNo("QDTTC"));
                order.setIsDeleted(0);
                order.setStatus("审核检测单");
                order = dao.saveOrUpdate(order).get(0);
                //进入流程
                //提交工作流
                StartProcessInfo info = new StartProcessInfo();
                //流程发起人
                info.setPromoter(user.getUsername());
                //订单ID
                info.setBusinessId(order.getId().toString());
                //是否完成了开始节点，我们设置为true,表示自动提交到下一个节点
                info.setCompleteStartTask(true);
                //选择下一个节点采用哪个分支
                //		info.setSequenceFlowName("");
                //增加标记，每个实例中可以获取到
                //		info.setTag("");
                //传入变量到流程中，流程中可以通过EL表达式获取
                Map<String,Object> variables = new HashMap<String, Object>();
                variables.put("msg", "【正审】");
                info.setVariables(variables);
                processClient.startProcessByName("普通审批流程", info);
            } else if (EntityState.MODIFIED.equals(state)) {
                dao.saveOrUpdate(order);
            } else if (EntityState.DELETED.equals(state)) {
                //删除，逻辑删除
                order.setIsDeleted(1);
                dao.saveOrUpdate(order);
            }
        }
    }

    /**
     * 生成订单号
     * @param prefix 订单号开头的部分,如prefix为“QDTTC”，则订单号为“QDTTC*******”
     * @return
     */
    private String generatorOrderNo(String prefix){
        return prefix+System.currentTimeMillis();
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
                " p.name as projectName, " + //项目名称
                " group_concat(d.`name`) as deviceNames, "+//设备名称
                " u1.userNamePic as auditUserNamePic, " + //审核人电子签名
                " u2.userNamePic as testUserNamePic " + //检测人电子签名
                " from "+Record.TABLENAME+" as r " +
                " join "+Order.TABLENAME+" as o " +
                " on o.id=r.orderId " +
                " join "+ProjectMethodStandard.TABLENAME+" as pm " +
                " on pm.id=r.projectMethodStandardId " +
                " join "+Project.TABLENAME+" as p " +
                " on pm.projectId=p.id " +
                " left join "+User.TABLENAME+" as u1 " +
                " on u1.username=r.auditUserName " +//审核人电子签名
                " left join "+User.TABLENAME+" as u2 " +
                " on u2.username=r.testUserName " +//检测人电子签名
                " join "+MethodStandard.TABLENAME+" as m " +
                " on pm.methodStandardId=m.id " +
                " left join "+Device.TABLENAME+" as d " +//关联设备
                " on LOCATE(d.id,r.deviceIds)>0 "+
                " where r.orderId=:orderId and r.isDeleted<>1 ";

        //替换参数
        Map<String,String> replaceMap = new HashMap<String,String>();
        replaceMap.put("methodStandardName","m.name");
        replaceMap.put("methodStandardNo","m.standardNo");
        replaceMap.put("projectName","p.name");
        replaceMap.put("deviceNames","d.name");
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
        sql += " group by r.id ";//分组
        if(StringUtils.isEmpty(orderSql)){
            orderSql = " ORDER BY r.crTime desc";
        }
        sql += orderSql;
        dao.pagingQueryBySql(page,sql,params);
    }
}
