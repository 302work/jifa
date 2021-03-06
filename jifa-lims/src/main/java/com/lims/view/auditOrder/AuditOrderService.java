package com.lims.view.auditOrder;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.uploader.UploadFile;
import com.bstek.dorado.uploader.annotation.FileResolver;
import com.bstek.dorado.web.DoradoContext;
import com.bstek.uflo.client.service.TaskClient;
import com.bstek.uflo.model.task.Task;
import com.bstek.uflo.model.task.TaskState;
import com.bstek.uflo.service.HistoryService;
import com.bstek.uflo.service.TaskOpinion;
import com.bstek.uflo.service.TaskService;
import com.dosola.core.common.DateUtil;
import com.dosola.core.common.StringUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.*;
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
public class AuditOrderService {

    @Resource
    private IMasterDao dao;

    @Resource(name=TaskClient.BEAN_ID)
    private TaskClient taskClient;

    @Resource(name= HistoryService.BEAN_ID)
    private HistoryService historyService;

    @Resource(name= TaskService.BEAN_ID)
    private TaskService taskService;

    @DataResolver
    public void saveOrder(Collection<Order> orders){
        for (Order  order: orders) {
            EntityState state = EntityUtils.getState(order);
            if (EntityState.NEW.equals(state)) {

            } else if (EntityState.MODIFIED.equals(state)) {
                dao.saveOrUpdate(order);
            } else if (EntityState.DELETED.equals(state)) {

            }
        }
    }

    @Expose
    public void auditOrder(int status,String taskIds,String remark){

            if (StringUtil.isEmpty(taskIds) || status == 0) {
                return;
            }
            String[] taskIdArray = taskIds.split(",");

            for (String taskIdStr : taskIdArray) {
                if (StringUtil.isEmpty(taskIdStr)) {
                    continue;
                }
                long taskId = Long.valueOf(taskIdStr);
                Task task = taskService.getTask(taskId);
                if (task == null) {
                    continue;
                }
                Order order = dao.getObjectById(Order.class, Long.valueOf(task.getBusinessId()));
                if (order == null || order.getIsDeleted() == 1) {
                    continue;
                }
                //当前处理人
                String assignee = task.getAssignee();
                //订单原状态
                String orderOldStatus = order.getStatus();
                String taskName = task.getTaskName();
                //回滚的节点
                String backFlowName = "to "+taskName;

                //审批意见
                TaskOpinion taskOpinion = new TaskOpinion(remark);

                //下个节点名称
                String flowName = getFlowName(taskName, status);
                if (flowName == null) {
                    continue;
                }

                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("msg", status == 1 ? "【正审】" : "【退回】");

                //如果没有生成检测记录则生成检测记录
//                if (taskName.equals("审核检测单") && status == 1) {
//                    createRecords(order);
//                }
                //如果是结果审核,则更新record的auditUserName字段
                if (taskName.equals("结果审核") && status == 1) {
                    updateRecordsAuditUserName(order);
                }

                //开始任务,只有状态是Created或Reserved才能开始任务
                TaskState taskState = task.getState();
                if(taskState.equals(TaskState.Created) || taskState.equals(TaskState.Reserved)) {
                    taskService.start(taskId);
                }
                //完成任务
                taskService.complete(taskId, flowName, variables, taskOpinion);

                //更新订单状态
                String statusStr = flowName.substring(3);
                if (statusStr.equals("结束")) {
                    statusStr = "审核通过";
                }
                order.setStatus(statusStr);
                dao.saveOrUpdate(order);

            }

    }

    /**
     * 结果审核时,更新record的auditUserName
     * @param order
     */
    private void updateRecordsAuditUserName(Order order) {
        String sql = "  update "+Record.TABLENAME+" " +
                     "  set auditUserName=:auditUserName " +
                    "   where orderId=:orderId and isDeleted<>1";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("orderId",order.getId());
        params.put("auditUserName",ContextHolder.getLoginUser().getUsername());
        dao.executeSQL(sql,params);
    }

    /**
     * 生成订单的检测记录
     * @param order
     */
    public void createRecords(Order order) {
        //先检测是否生成过record
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("orderId",order.getId());
        int count = dao.queryCountBySql("select id from "+Record.TABLENAME+" where orderId=:orderId",params);
        if(count>0){
            return;
        }
        //该订单的检测项目和方法标准
        String projectMethodStandardIds = order.getProjectMethodStandardIds();
        //样品数量
        int sampleCount = order.getSampleCount();
        IUser user = ContextHolder.getLoginUser();
        String userName = user.getUsername();
        int sampleIndex = 0;
        for(String projectMethodStandardIdStr : projectMethodStandardIds.split(",")){
            sampleIndex++;
            Long projectMethodStandardId = Long.valueOf(projectMethodStandardIdStr);
            ProjectMethodStandard projectMethodStandard = dao.getObjectById(ProjectMethodStandard.class,projectMethodStandardId);
            if(projectMethodStandard==null){
                continue;
            }
            Long projectId = projectMethodStandard.getProjectId();
            if(projectId==null){
                continue;
            }
            //记录项
            String resultColumnIds = getResultColumnIds(projectId);
            for(int i=1;i<=sampleCount;i++){
                Record record = new Record();
                record.setCrTime(new Date());
                record.setIsDeleted(0);
                record.setOrderId(order.getId());
                record.setProjectMethodStandardId(projectMethodStandardId);
                record.setResultColumnIds(resultColumnIds);
                //样品编号
                String sampleNo = order.getOrderNo().substring(5);
                sampleNo += "-";
                sampleNo += numToLetter(i);
                sampleNo += "-";
                sampleNo += sampleIndex;
                record.setSampleNo(sampleNo);
                record.setCrUser(userName);
                record.setProjectId(projectId);
                record.setMethodStandardId(projectMethodStandard.getMethodStandardId());
                record = dao.saveOrUpdate(record).get(0);
                //添加检测条件
                addTestCondition(record.getId(),projectId,projectMethodStandard.getMethodStandardId(),record.getSampleNo(),order.getId());
            }
        }
    }

    /**
     * 根据 样品的序号生成字母
     * @param sampleIndex 序号
     * @return ABC
     */
    private String numToLetter(int sampleIndex){
        switch (sampleIndex){
            case 1:
                return "A";
            case 2:
                return "B";
            case 3:
                return "C";
            case 4:
                return "D";
            case 5:
                return "E";
            case 6:
                return "F";
            case 7:
                return "G";
            case 8:
                return "H";
            case 9:
                return "I";
            case 10:
                return "J";

        }
        return "K";
    }

    /**
     * 添加默认的检测条件
     * @param recordId
     * @param projectId
     */
    private void addTestCondition(Long recordId, Long projectId,Long methodStandardId,String sampleNo,Long orderId) {
        String hql = "From "+TestCondition.class.getName()+" where projectId=:projectId";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("projectId",projectId);
        List<TestCondition> testConditionList = (List<TestCondition>) dao.query(hql,params);
        if(testConditionList!=null && testConditionList.size()>0){
            for(TestCondition testCondition : testConditionList){
                RecordTestCondition recordTestCondition = new RecordTestCondition();
                recordTestCondition.setRecordId(recordId);
                recordTestCondition.setName(testCondition.getName());
                recordTestCondition.setValue(testCondition.getValue());
                recordTestCondition.setRemark(testCondition.getRemark());
                recordTestCondition.setProjectId(projectId);
                recordTestCondition.setMethodStandardId(methodStandardId);
                recordTestCondition.setSampleNo(sampleNo);
                recordTestCondition.setOrderId(orderId);
                dao.saveOrUpdate(recordTestCondition);
            }
        }
    }


    /**
     * 查找项目的记录项
     * @param projectId
     * @return
     */
    private String getResultColumnIds(Long projectId){
        if(projectId==null){
            return null;
        }
        String hql = "From "+ResultColumn.class.getName()+" where projectId=:projectId and isDeleted<>1 ";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("projectId",projectId);
        List<ResultColumn> resultColumnList = (List<ResultColumn>) dao.query(hql,params);
        String resultColumnIds = "";
        for(ResultColumn resultColumn : resultColumnList){
            if(StringUtil.isEmpty(resultColumnIds)){
                resultColumnIds = resultColumn.getId()+"";
            }else{
                resultColumnIds += ","+resultColumn.getId();
            }
        }
        return resultColumnIds;
    }

    @DataResolver
    public void saveRecordTestCondition(Collection<RecordTestCondition> recordTestConditions,Long recordId){
        for (RecordTestCondition recordTestCondition : recordTestConditions) {
            EntityState state = EntityUtils.getState(recordTestCondition);
            if (EntityState.NEW.equals(state)) {
                recordTestCondition.setRecordId(recordId);
                dao.saveOrUpdate(recordTestCondition);
            } else if (EntityState.MODIFIED.equals(state)) {
                recordTestCondition.setRecordId(recordId);
                dao.saveOrUpdate(recordTestCondition);
            } else if (EntityState.DELETED.equals(state)) {
                dao.delete(recordTestCondition);
            }
        }
    }

    /**
     * 根据任务名称和状态获取下一个流程名称
     * @param taskName
     * @param status
     * @return
     */
    private String getFlowName(String taskName,int status){
        String[] nodeNames = new String[]{"申请检测单","审核检测单","结果录入","结果审核","生成报告","审核报告","结束"};
        List<String> nodeList = Arrays.asList(nodeNames);
        int index = nodeList.indexOf(taskName);
        int maxIndex = nodeList.size()-1;
        if(index!=-1){
            if(status==1 && index<maxIndex){
                return "to "+nodeList.get(index+1);
            }
            if(status==2 && index>0){
                return "to "+nodeList.get(index-1);
            }
        }
        return null;
    }

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
            //记录id
            Long recordId = Long.valueOf(parameter.get("recordId").toString());
            Record record = dao.getObjectById(Record.class,recordId);
            if(record==null){
                throw new RuntimeException("没有找到该检测记录,recordId:"+recordId);
            }
            //上传字段类型
            int type = Integer.valueOf(parameter.get("type").toString());
            //更新原样图片
            if(type==1){
                record.setSamplePic(fjlj);
            }
            //更新测试样图片
            if(type==2){
                record.setTestSamplePic(fjlj);
            }
            dao.saveOrUpdate(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fjlj;
    }

    //逻辑删除记录
    @Expose
    public void deleteRecord(Long recordId){
        Record record = dao.getObjectById(Record.class,recordId);
        record.setIsDeleted(1);
        dao.saveOrUpdate(record);
    }

}
