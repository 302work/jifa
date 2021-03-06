package com.lims.view.order;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.bstek.dorado.uploader.UploadFile;
import com.bstek.dorado.uploader.annotation.FileResolver;
import com.bstek.dorado.web.DoradoContext;
import com.bstek.uflo.client.service.ProcessClient;
import com.bstek.uflo.service.StartProcessInfo;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.common.DateUtil;
import com.dosola.core.common.StringUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.google.common.collect.ImmutableMap;
import com.lims.pojo.*;
import com.lims.service.UserService;
import com.lims.view.auditOrder.AuditOrderService;
import com.lims.view.result.ResultService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

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

    @Autowired
    private UserService userService;
    @Autowired
    private ResultService resultService;
    @Autowired
    private AuditOrderService auditOrderService;

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
            //项目的方法标准，多个逗号隔开
            String projectMethodStandardIds = order.getProjectMethodStandardIds();
            if(projectMethodStandardIds==null){
                throw new RuntimeException("必须添加检测项目");
            }
            String[] pmsIds = projectMethodStandardIds.split(",");
            Map<String,String> map = new HashMap<>();
            for (String pmsId : pmsIds) {
                map.put(pmsId,pmsId);
            }
            if(map.size() != pmsIds.length){
                throw new RuntimeException("不能重复添加项目的方法标准");
            }
            //if (EntityState.NEW.equals(state) && order.getId()==null) {
            if (order.getId()==null) {
                order.setCrTime(new Date());
                order.setCrUser(userName);
                order.setOrderNo(generatorOrderNo());
                order.setIsDeleted(0);
                order.setStatus("审核检测单");
                order = dao.saveOrUpdate(order).get(0);
                //生成检测记录
                auditOrderService.createRecords(order);

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
            } else if (EntityState.MODIFIED.equals(state) && order.getId()!=null) {
                dao.saveOrUpdate(order);
            } else if (EntityState.DELETED.equals(state)) {
                //删除，逻辑删除
                order.setIsDeleted(1);
                dao.saveOrUpdate(order);
            }else{
                throw new RuntimeException("干啥呢");
            }
        }
    }

    /**
     * 生成订单号
     * @return QDTTC-170000001
     */
    private String generatorOrderNo(){
        String count = dao.queryCount("From "+Order.class.getName(),null)+"";
        String init = "0000000";
        String result = init.substring(0,init.length()-count.length())+count;
        return "QDTTC"+ DateUtil.getDateStr(new Date(),"yy")+result;
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
            orderSql = " order by r.sampleNo ";
        }
        sql += orderSql;
        dao.pagingQueryBySql(page,sql,params);
    }
    //查询检测记录
    @Expose
    public List<Record> queryOrderRecordByOrderIdAndProjectId(Long orderId,Long projectId){
        if(orderId==null || projectId==null){
            return null;
        }
        String hql = " From "+Record.class.getName()+" where orderId=:orderId and projectId=:projectId and isDeleted<>1";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("projectId",projectId);
        params.put("orderId",orderId);
        return (List<Record>) dao.query(hql,params);
    }

    //查询检测协议
    @Expose
    public Map<String,Object> queryJiancexieyiByOrderId(Long orderId){
        Map<String,Object> map = new HashMap<>();
        Order order = dao.getObjectById(Order.class,orderId);
        map.put("order",order);
        //批准人
        String auditUserName = order.getAuditUserName();
        if(!StringUtil.isEmpty(auditUserName)){
            //签名图片
            String auditUserPic = userService.queryUser(auditUserName).getUserNamePic();
            map.put("auditUserPic",auditUserPic);
        }
        //客户信息
        Consumer consumer = dao.getObjectById(Consumer.class,order.getConsumerId());
        map.put("consumer",consumer);
        //订单所属检测项目
        String projectMethodStandardIds = order.getProjectMethodStandardIds();
        List<Map<String,Object>> projectList = new ArrayList<>();
        for (String projectMethodStandardId : projectMethodStandardIds.split(",")) {
            ProjectMethodStandard projectMethodStandard = dao.getObjectById(ProjectMethodStandard.class,Long.valueOf(projectMethodStandardId));
            Project project = dao.getObjectById(Project.class,projectMethodStandard.getProjectId());
            MethodStandard methodStandard = dao.getObjectById(MethodStandard.class,projectMethodStandard.getMethodStandardId());
            Map<String,Object> projectMap = new HashMap<>();
            projectMap.put("project",project);
            projectMap.put("methodStandard",methodStandard);
            projectList.add(projectMap);
        }
        map.put("projectList",projectList);
        return map;
    }

    //上传原样照片
    @FileResolver
    public String processFile(UploadFile file, Map<String, Object> parameter) {
        String fjlj = null;
        try {
            String fileName = file.getFileName();
            // 文件扩展名
            String extName = fileName.substring(fileName.indexOf(".") + 1, fileName
                    .length());
            if (StringUtil.isEmpty(extName)
                    || (!extName.equalsIgnoreCase("jpg")
                    && !extName.equalsIgnoreCase("png")
                    && !extName.equalsIgnoreCase("jpeg") && !extName
                    .equalsIgnoreCase("bmp"))) {
                throw new RuntimeException("只能上传图片格式为jpg、png、bmp的文件！");
            }

            String folderName = DateUtil.getDateStr(new Date()).substring(0, 7);
            //当天
            String todayName = DateUtil.getDateStr(new Date()).substring(8, 10);
            //新文件名
            String newName = new Date().getTime() + "." + extName;
            // 获取系统路径
            String ctxDir = null;
            ResourceBundle rb = ResourceBundle.getBundle("application");
            if(rb.containsKey("upload.path") && !StringUtil.isEmpty(rb.getString("upload.path"))){
                ctxDir = rb.getString("upload.path");
            }else{
                ctxDir = DoradoContext.getCurrent().getServletContext().getRealPath("/");
            }
            if (!ctxDir.endsWith(String.valueOf(File.separatorChar))) {
                ctxDir = ctxDir + File.separatorChar;
            }
            String savePath = ctxDir + "upload" + File.separator + folderName
                    + File.separator + todayName;
            File folderFile = new File(savePath);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            // 新文件路径
            String newFilePath = savePath + File.separator + newName;
            // 保存上传的文件
            File newFile = new File(newFilePath);
            // 返回前台的路径
            fjlj = "upload" + File.separator + folderName + File.separator
                    + todayName + File.separator + newName;
            // 如果文件存在则删除
            if (newFile.exists()) {
                newFile.delete();
            }
            file.transferTo(newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fjlj;
    }

    //查询原始记录
    @Expose
    public Map<String,Object> queryYuanshijiluByRecordId(Long recordId){
        Map<String,Object> map = new HashMap<>();
        Record record = dao.getObjectById(Record.class,recordId);
        map.put("record",record);
        //检测项目
        map.put("project",dao.getObjectById(Project.class,record.getProjectId()));
        //方法标准
        map.put("methodStandard",dao.getObjectById(MethodStandard.class,record.getMethodStandardId()));
        //检测人
        String testUserName = record.getTestUserName();
        if(!StringUtil.isEmpty(testUserName)){
            //签名图片
            String testUserNamePic = userService.queryUser(testUserName).getUserNamePic();
            map.put("testUserNamePic",testUserNamePic);
        }
        //审核人
        String auditUserName = record.getAuditUserName();
        if(!StringUtil.isEmpty(auditUserName)){
            //签名图片
            String auditUserPic = userService.queryUser(auditUserName).getUserNamePic();
            map.put("auditUserPic",auditUserPic);
        }
        //检测条件
        String hql = "From "+ RecordTestCondition.class.getName()+" where recordId=:recordId";
        List<RecordTestCondition> recordTestConditionList = (List<RecordTestCondition>) dao.query(hql, ImmutableMap.<String, Object>of("recordId",recordId));
        map.put("recordTestConditionList",recordTestConditionList);

        //检测仪器
        List<Device> deviceList = new ArrayList<>();
        String deviceIds = record.getDeviceIds();
        if(!StringUtil.isEmpty(deviceIds)) {
            hql = "From " + Device.class.getName() + " where id in(:deviceIds) and isDeleted<>1";
            List<Long> deviceIdList = new ArrayList<>();
            for (String deviceId : deviceIds.split(",")) {
                deviceIdList.add(Long.valueOf(deviceId));
            }
            deviceList = (List<Device>) dao.query(hql, ImmutableMap.<String, Object>of("deviceIds", deviceIdList));
        }
        map.put("deviceList",deviceList);
        //检测记录
        //所有列
        String resultColumnIds = record.getResultColumnIds();
        List<ResultColumn> resultColumnList = new ArrayList<>();
        if(!StringUtil.isEmpty(resultColumnIds)) {
            hql = "From " + ResultColumn.class.getName() + " where id in(:resultColumnIds) and isDeleted<>1";
            List<Long> resultColumnIdList = new ArrayList<>();
            for (String reId : resultColumnIds.split(",")) {
                resultColumnIdList.add(Long.valueOf(reId));
            }
            resultColumnList = (List<ResultColumn>) dao.query(hql, ImmutableMap.<String, Object>of("resultColumnIds", resultColumnIdList));
        }
        map.put("resultColumnList",resultColumnList);
        //结果
        List<Map<String,Object>> resultList = resultService.queryResultList(recordId);
        map.put("resultList",resultList);
        return map;
    }

}
