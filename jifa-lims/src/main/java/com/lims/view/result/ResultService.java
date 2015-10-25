package com.lims.view.result;

import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.common.event.DefaultClientEvent;
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
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.Record;
import com.lims.pojo.Result;
import com.lims.pojo.ResultColumn;
import com.lims.pojo.ResultValue;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检测结果相关
 * @author june
 * 2015年10月24日 11:52
 */
@Service
public class ResultService {

    @Resource
    private IMasterDao dao;

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
        Object obj = DoradoContext.getCurrent().getAttribute(DoradoContext.VIEW, "recordId");
        if(obj==null){
            obj = DoradoContext.getCurrent().getRequest().getParameter("recordId");;
        }
        if(obj==null){
            return;
        }

        //创建result的属性
        //第几次检测
        PropertyDef indexPropertyDef = new BasePropertyDef("index");
        indexPropertyDef.setLabel("序号");
        indexPropertyDef.setDataType(viewCofig.getDataType("Integer"));
        dtResult.addPropertyDef(indexPropertyDef);

        DataColumn indexColumn = new DataColumn();
        indexColumn.setName("index");
        indexColumn.setProperty("index");
        indexColumn.setWidth("50");
        indexColumn.setAlign(Align.center);
        //绘制footer
        indexColumn.addClientEventListener("onRenderFooterCell",
                new DefaultClientEvent("arg.dom.innerText = '平均值'"));
        resultDataGrid.addColumn(indexColumn);

        //动态创建检测属性
        Long recordId = Long.valueOf(obj.toString());
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
                String colName = "col_"+resultColumnId.longValue();
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
                String average = getColumnAverage(recordId,resultColumnId);
                dataColumn.addClientEventListener("onRenderFooterCell",
                        new DefaultClientEvent("arg.dom.innerText = "+average+""));
                resultDataGrid.addColumn(dataColumn);
            }
        }
        //状态
        PropertyDef statusPropertyDef = new BasePropertyDef("status");
        statusPropertyDef.setLabel("状态");
        statusPropertyDef.setDataType(viewCofig.getDataType("Integer"));
        statusPropertyDef.setMapping(Mapping.parseString("1=有效,2=作废"));
        dtResult.addPropertyDef(statusPropertyDef);

        DataColumn statusColumn = new DataColumn();
        statusColumn.setName("status");
        statusColumn.setProperty("status");
        statusColumn.setWidth("50");
        statusColumn.setAlign(Align.center);
        resultDataGrid.addColumn(statusColumn);
    }

    /**
     * 计算该记录项的平均值
     * @param recordId
     * @param resultColumnId
     * @return
     */
    private String getColumnAverage(Long recordId, Long resultColumnId) {
        if(recordId==null || resultColumnId==null){
            return "";
        }
        //只取未删除的有效的数据
        String sql = "select id " +
                " from "+ Result.TABLENAME+" " +
                " where recordId=:recordId " +
                " and `status`=1 and isDeleted<>1 ";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("recordId", recordId);
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
        return dao.queryBySql(sql,params).get(0).get("avgValue").toString();
    }


    @DataProvider
    public List<Map<String,Object>> queryResult(Long recordId){
        //查询所有result
        String sql = "select re.* " +
                " from "+ Result.TABLENAME+" as re " +
                " join "+Record.TABLENAME+" as r " +
                " on re.recordId=r.id " +
                " where r.id=:recordId " +
                " and re.isDeleted<>1 ";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("recordId",recordId);
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
                    map.put("col_"+valueMap.get("rId"),valueMap.get("rValue"));
                }
            }
        }
        return list;
    }
}
