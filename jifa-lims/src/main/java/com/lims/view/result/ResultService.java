package com.lims.view.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.common.event.DefaultClientEvent;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.type.EntityDataType;
import com.bstek.dorado.data.type.property.BasePropertyDef;
import com.bstek.dorado.data.type.property.Mapping;
import com.bstek.dorado.data.type.property.PropertyDef;
import com.bstek.dorado.view.View;
import com.bstek.dorado.view.manager.ViewConfig;
import com.bstek.dorado.view.widget.Align;
import com.bstek.dorado.view.widget.grid.DataColumn;
import com.bstek.dorado.view.widget.grid.DataGrid;
import com.bstek.dorado.web.DoradoContext;
import com.dosola.core.common.PojoKit;
import com.dosola.core.common.StringUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 检测结果相关
 * @author june
 * 2015年10月24日 11:52
 */
@Service
public class ResultService {

    @Resource
    private IMasterDao dao;

    private final static String colPreFix = "col_";

    public void onInit(ViewConfig viewCofig) throws Exception {
        EntityDataType dtResult = (EntityDataType) viewCofig.getDataType("dtResult");
        View view = viewCofig.getView();
        if(view==null){
            return;
        }
        DataGrid resultDataGrid = (DataGrid) view.getViewElement("resultDataGrid");
        if(resultDataGrid==null){
            return;
        }
        //获取全局参数
        //这个js设置了才能获取到参数
        Object obj = DoradoContext.getCurrent().getAttribute(DoradoContext.VIEW, "recordId");
        if(obj==null){
            //单个记录的查看结果
            obj = DoradoContext.getCurrent().getRequest().getParameter("recordId");
        }
        //查看某个项目的结果,需要传projectId和orderId
        Object projectIdObj = DoradoContext.getCurrent().getRequest().getParameter("projectId");
        Object orderIdObj = DoradoContext.getCurrent().getRequest().getParameter("orderId");
        if(obj==null && (projectIdObj==null || orderIdObj==null)){
            return;
        }

        Long recordId = null;
        List<Long> recordIds = new ArrayList<Long>();
        if(obj==null){
            Long projectId = Long.valueOf(projectIdObj.toString());
            Long orderId = Long.valueOf(orderIdObj.toString());
            recordIds = getRecordIdByOrderIdAndProjectId(orderId,projectId);
            if(recordIds==null || recordIds.size()==0){
                return;
            }
            //取第一条
            recordId = recordIds.get(0);
        }else{
            recordId = Long.valueOf(obj.toString());
            recordIds.add(recordId);
        }

        //创建result的属性
        //第几次检测
        PropertyDef indexPropertyDef = new BasePropertyDef("index");
        indexPropertyDef.setLabel("次数");
        indexPropertyDef.setDataType(viewCofig.getDataType("Integer"));
        dtResult.addPropertyDef(indexPropertyDef);

        DataColumn indexColumn = new DataColumn();
        indexColumn.setName("index");
        indexColumn.setProperty("index");
        indexColumn.setWidth("50");
        indexColumn.setAlign(Align.center);
        indexColumn.setReadOnly(true);
        //绘制footer
        indexColumn.addClientEventListener("onRenderFooterCell",
                new DefaultClientEvent("arg.dom.innerText = '平均值'"));
        resultDataGrid.addColumn(indexColumn);

        //动态创建检测属性
        String sql = "select rc.* " +
                " from "+ ResultColumn.TABLENAME+" as rc " +
                " join "+ Record.TABLENAME+" as r" +
                " on LOCATE(rc.id,r.resultColumnIds)>0 " +
                " where r.id=:recordId ";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("recordId",recordId);
        List<Map<String,Object>> list = dao.queryBySql(sql, params);
        if(list!=null && list.size()>0){
            for (Map<String,Object> map : list){
                Long resultColumnId = Long.valueOf(map.get("id").toString());
                String colName = colPreFix+resultColumnId.longValue();
                String label = map.get("name").toString();

                PropertyDef propertyDef = new BasePropertyDef(colName);
                propertyDef.setLabel(label);
                propertyDef.setDataType(viewCofig.getDataType("Double"));
                propertyDef.setDisplayFormat("#,##0.00");
                dtResult.addPropertyDef(propertyDef);

                DataColumn dataColumn = new DataColumn();
                dataColumn.setName(colName);
                dataColumn.setProperty(colName);
                dataColumn.setAlign(Align.center);
                //平均值
                String average = getColumnAverage(recordIds,resultColumnId);
                dataColumn.addClientEventListener("onRenderFooterCell",
                        new DefaultClientEvent("arg.dom.innerText = '"+average+"'"));
                resultDataGrid.addColumn(dataColumn);
            }
        }
        //状态
        PropertyDef statusPropertyDef = new BasePropertyDef("status");
        statusPropertyDef.setLabel("状态");
        statusPropertyDef.setDataType(viewCofig.getDataType("Integer"));
        statusPropertyDef.setDefaultValue(1);
        statusPropertyDef.setMapping(Mapping.parseString("1=有效,2=作废"));
        dtResult.addPropertyDef(statusPropertyDef);

        //备注
        PropertyDef remarkPropertyDef = new BasePropertyDef("remark");
        remarkPropertyDef.setLabel("备注");
        remarkPropertyDef.setDataType(viewCofig.getDataType("String"));
        dtResult.addPropertyDef(remarkPropertyDef);

        DataColumn statusColumn = new DataColumn();
        statusColumn.setName("status");
        statusColumn.setProperty("status");
        statusColumn.setWidth("50");
        statusColumn.setAlign(Align.center);
        statusColumn.setEditable(false);
        resultDataGrid.addColumn(statusColumn);

        DataColumn remarkColumn = new DataColumn();
        remarkColumn.setName("remark");
        remarkColumn.setProperty("remark");
        remarkColumn.setWidth("100");
//        remarkColumn.setAlign(Align.center);
        resultDataGrid.addColumn(remarkColumn);
    }

    /**
     * 计算该记录项的平均值
     * @param recordIds
     * @param resultColumnId
     * @return
     */
    private String getColumnAverage(List<Long> recordIds, Long resultColumnId) {
        if(recordIds==null || recordIds.size()==0 || resultColumnId==null){
            return "";
        }
        //只取未删除的有效的数据
        String sql = "select id " +
                " from "+ Result.TABLENAME+" " +
                " where recordId in(:recordIds) " +
                " and `status`=1 and isDeleted<>1 ";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("recordIds", recordIds);
        List<Map<String,Object>> list = dao.queryBySql(sql, params);
        if(list==null || list.size()==0){
            return "";
        }
        List<String> resultIdList = new ArrayList<String>();
        for(Map<String,Object> map : list){
            resultIdList.add(map.get("id").toString());
        }
        //查询平均值,保留2位小数，四舍五入
        sql = " select ROUND(avg(`value`),2) as avgValue " +
              " from "+ResultValue.TABLENAME+" " +
              " where resultId in (:resultIds) " +
              " and resultColumnId=:resultColumnId";
        params = new HashMap<String,Object>();
        params.put("resultIds", resultIdList);
        params.put("resultColumnId", resultColumnId);
        Object obj = dao.queryBySql(sql,params).get(0).get("avgValue");
        if(obj==null){
            return "";
        }
        return obj.toString();
    }


    @DataProvider
    public List<Map<String,Object>> queryResult(String parameter){
        JSONObject jsonObject = JSON.parseObject(parameter);
        String projectIdStr = jsonObject.getString("projectId");
        String orderIdStr = jsonObject.getString("orderId");
        String recordIdStr = jsonObject.getString("recordId");
        if(StringUtil.isEmpty(recordIdStr) && (StringUtil.isEmpty(orderIdStr)  || StringUtil.isEmpty(projectIdStr) )){
            return null;
        }
        List<Long> recordIds = new ArrayList<Long>();
        if(StringUtil.isEmpty(recordIdStr)){
            Long projectId = Long.valueOf(projectIdStr);
            Long orderId = Long.valueOf(orderIdStr);
            recordIds = getRecordIdByOrderIdAndProjectId(orderId,projectId);
        }else{
            Long recordId = Long.valueOf(recordIdStr);
            recordIds.add(recordId);
        }
        if(recordIds==null || recordIds.size()==0){
            return null;
        }

//        List<Long> recordIds = new ArrayList<Long>();
//        recordIds.add(recordId);
        //查询所有result
        String sql = "select re.* " +
                " from "+ Result.TABLENAME+" as re " +
                " join "+Record.TABLENAME+" as r " +
                " on re.recordId=r.id " +
                " where r.id in(:recordIds) " +
                " and re.isDeleted<>1 ";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("recordIds",recordIds);
        List<Map<String,Object>> list = dao.queryBySql(sql, params);
        if(list == null || list.size()==0){
            return list;
        }
        //把resultValue放进result
        for (Map<String,Object> map : list){
            Long resultId = Long.valueOf(map.get("id").toString());
            sql = " select rv.`value` as rValue ,rc.id as rId " +
                  " from "+ ResultValue.TABLENAME+" as rv " +
                  " join "+ResultColumn.TABLENAME+" as rc " +
                  " on rv.resultColumnId=rc.id " +
                  " where rv.resultId=:resultId";
            params = new HashMap<String,Object>();
            params.put("resultId",resultId);
            List<Map<String,Object>> valueList = dao.queryBySql(sql, params);
            if(valueList!=null && valueList.size()>0){
                for (Map<String,Object> valueMap : valueList){
                    //按构造dataType的属性名放入值
                    map.put(colPreFix+valueMap.get("rId"),valueMap.get("rValue"));
                }
            }
        }
        return list;
    }

    @DataResolver
    public void saveResult(Collection<Map<String,Object>> maps,String recordIdstr){
        if(StringUtils.isEmpty(recordIdstr)){
            throw new RuntimeException("recordId不能为空");
        }
        Long recordId = Long.valueOf(recordIdstr);
        for (Map<String,Object> map : maps) {
            EntityState state = EntityUtils.getState(map);
            if (EntityState.NEW.equals(state)) {
                addResult(map,recordId);
                //更新record表的testUserName
                updateRecordTestUserName(recordId);
            } else if (EntityState.MODIFIED.equals(state)) {
                if(Long.valueOf(map.get("recordId").toString()).longValue()!=recordId.longValue()){
                    throw new RuntimeException("recordId不匹配");
                }
                updateResult(map);
            } else if (EntityState.DELETED.equals(state)) {
                //不能删除
            }
        }
    }

    /**
     * 更新record表的testUserName
     * @param recordId
     */
    private void updateRecordTestUserName(Long recordId) {
        String sql = "  update "+Record.TABLENAME+" " +
                "  set testUserName=:testUserName " +
                "   where id=:recordId and isDeleted<>1";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("recordId",recordId);
        params.put("testUserName",ContextHolder.getLoginUser().getUsername());
        dao.executeSQL(sql,params);
    }

    /**
     * 新增检测记录
     * @param map
     * @param recordId
     */
    private void addResult(Map<String,Object> map,Long recordId){
        Result result = new Result();
        result.setRecordId(recordId);
        result.setCrTime(new Date());
        result.setCrUser(ContextHolder.getLoginUser().getUsername());
        result.setStatus(map.get("status")==null?1:Integer.valueOf(map.get("status").toString()));
        result.setRemark(map.get("remark")==null?null:map.get("remark").toString());
        result.setIsDeleted(0);
        //查找检测次数
        String sql = "select max(`index`) as maxIndex from "+Result.TABLENAME+" where isDeleted<>1 and recordId=:recordId ";
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("recordId",recordId);
        Map<String,Object> indexMap = dao.queryBySql(sql,paramMap).get(0);
        if(indexMap.get("maxIndex")!=null){
            result.setIndex(Integer.valueOf(indexMap.get("maxIndex").toString())+1);
        }else{
            result.setIndex(1);
        }
        result = dao.saveOrUpdate(result).get(0);
        saveResultValue(map, result.getId());
    }

    /**
     * 保存检测结果
     * @param map
     * @param resultId
     */
    private void saveResultValue(Map<String,Object> map,Long resultId){
        if(resultId==null){
            throw new RuntimeException("resultId不能为空");
        }
        String[] keys = map.keySet().toArray(new String[]{});
        for(String key : keys){
            if(key.indexOf(colPreFix)!=-1){
                Long resultColumnId = Long.valueOf(key.substring(colPreFix.length()));
                ResultValue resultValue = new ResultValue();
                resultValue.setResultId(resultId);
                resultValue.setResultColumnId(resultColumnId);
                resultValue.setValue(map.get(key)==null?null:map.get(key).toString());
                dao.saveOrUpdate(resultValue);
            }
        }
    }

    /**
     * 更新检测记录
     * @param map
     */
    private void updateResult(Map<String,Object> map){
        Result result = PojoKit.build(Result.class, map);
        if(result.getId()==null){
            throw new RuntimeException("resultId不能为空");
        }
        result.setRemark(map.get("remark")==null?null:map.get("remark").toString());
        result.setStatus(Integer.valueOf(map.get("status").toString()));
        result = dao.saveOrUpdate(result).get(0);
        updateResultValue(map,result.getId());
    }

    /**
     * 更新检测结果
     * @param map
     * @param resultId
     */
    private void updateResultValue(Map<String,Object> map,Long resultId){
        if(resultId==null){
            throw new RuntimeException("resultId不能为空");
        }
        String[] keys = map.keySet().toArray(new String[]{});
        for(String key : keys){
            if(key.indexOf(colPreFix)!=-1){
                Long resultColumnId = Long.valueOf(key.substring(colPreFix.length()));
                String hql = "From "+ResultValue.class.getName()+" where resultId=:resultId and resultColumnId=:resultColumnId ";
                Map<String,Object> paramMap = new HashMap<String,Object>();
                paramMap.put("resultId",resultId);
                paramMap.put("resultColumnId",resultColumnId);
                ResultValue resultValue = (ResultValue) dao.query(hql, paramMap).get(0);
                resultValue.setValue(map.get(key)==null?null:map.get(key).toString());
                dao.saveOrUpdate(resultValue);
            }
        }
    }

    /**
     * 根据订单id和项目id查询该项目的所有检测记录
     * @param orderId
     * @param projectId
     * @return
     */
    private List<Long> getRecordIdByOrderIdAndProjectId(Long orderId,Long projectId){
        if(orderId==null || projectId==null){
            return null;
        }
        Order order = dao.getObjectById(Order.class,orderId);
        String[] projectMethodStandardIds = order.getProjectMethodStandardIds().split(",");
        //查找项目的方法标准
        String sql = " select id From "+ProjectMethodStandard.TABLENAME+" " +
                " where id in(:projectMethodStandardIds) " +
                " and projectId=:projectId";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("projectMethodStandardIds",projectMethodStandardIds);
        params.put("projectId",projectId);
        ProjectMethodStandard projectMethodStandard = dao.queryBySql(sql,params,ProjectMethodStandard.class).get(0);
        //查找第一条record
        sql = "select id from "+Record.TABLENAME+" " +
                " where orderId=:orderId " +
                " and projectMethodStandardId=:projectMethodStandard and isDeleted<>1";
        params = new HashMap<String,Object>();
        params.put("orderId",orderId);
        params.put("projectMethodStandard",projectMethodStandard.getId());
        List<Record> records = dao.queryBySql(sql,params,Record.class);
        List<Long> recordIdList = new ArrayList<Long>();
        if(records!=null && records.size()>0){
            for (Record record : records){
                recordIdList.add(record.getId());
            }
        }
        return recordIdList;
    }
}
