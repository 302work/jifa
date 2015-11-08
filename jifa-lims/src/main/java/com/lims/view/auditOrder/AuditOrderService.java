package com.lims.view.auditOrder;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.uflo.client.service.TaskClient;
import com.bstek.uflo.model.task.Task;
import com.bstek.uflo.service.HistoryService;
import com.bstek.uflo.service.TaskOpinion;
import com.bstek.uflo.service.TaskService;
import com.dosola.core.common.StringUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.*;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        if(StringUtil.isEmpty(taskIds) || status==0){
            return;
        }
        String[] taskIdArray = taskIds.split(",");

        for(String taskIdStr : taskIdArray){
            if(StringUtil.isEmpty(taskIdStr)){
                continue;
            }
            long taskId = Long.valueOf(taskIdStr);
            Task task = taskService.getTask(taskId);
            if(task==null){
                continue;
            }
            Order order = dao.getObjectById(Order.class,Long.valueOf(task.getBusinessId()));
            if(order==null || order.getIsDeleted().intValue()==1){
                continue;
            }
            String taskName = task.getTaskName();
            //审批意见
            TaskOpinion taskOpinion = new TaskOpinion(remark);

            //下个节点名称
            String flowName = getFlowName(taskName,status);
            if(flowName==null){
                continue;
            }

            Map<String,Object> variables = new HashMap<String, Object>();
            variables.put("msg", status==1?"【正审】":"【退回】");

            //如果没有生成检测记录则生成检测记录
            if(taskName.equals("审核检测单") && status==1){
                createRecords(order);
            }

            //开始任务
            taskService.start(taskId);
            //完成任务
            taskService.complete(taskId,flowName,variables,taskOpinion);

            //更新订单状态
            String statusStr = flowName.substring(3);
            if(statusStr.equals("结束")){
                statusStr = "审核通过";
            }
            order.setStatus(statusStr);
            dao.saveOrUpdate(order);

        }
    }

    /**
     * 生成订单的检测记录
     * @param order
     */
    private void createRecords(Order order) {
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
        for(String projectMethodStandardIdStr : projectMethodStandardIds.split(",")){
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
                record.setSampleNo(generateSampleNo());
                record.setCrUser(userName);
                record = dao.saveOrUpdate(record).get(0);
                //添加检测条件
                addTestCondition(record.getId(),projectId);
            }
        }
    }

    /**
     * 添加默认的检测条件
     * @param recordId
     * @param projectId
     */
    private void addTestCondition(Long recordId, Long projectId) {
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
                dao.saveOrUpdate(recordTestCondition);
            }
        }
    }

    /**
     * 随机生成16位样品编号
     * @return
     */
    private String generateSampleNo(){
        return RandomStringUtils.randomNumeric(16);
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

}
