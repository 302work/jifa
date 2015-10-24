package com.lims.view.result;

import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.data.type.EntityDataType;
import com.bstek.dorado.data.type.property.BasePropertyDef;
import com.bstek.dorado.data.type.property.Mapping;
import com.bstek.dorado.data.type.property.PropertyDef;
import com.bstek.dorado.view.manager.ViewConfig;
import com.bstek.dorado.web.DoradoContext;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.Record;
import com.lims.pojo.Result;
import com.lims.pojo.ResultColumn;
import com.lims.pojo.ResultValue;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        PropertyDef propertyDef = new BasePropertyDef("index");
        propertyDef.setLabel("序号");
        propertyDef.setDataType(viewCofig.getDataType("Integer"));
        dtResult.addPropertyDef(propertyDef);

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
                PropertyDef propDef = new BasePropertyDef("col_"+map.get("id"));
                propDef.setLabel(map.get("name").toString());
                propDef.setDataType(viewCofig.getDataType("String"));
                dtResult.addPropertyDef(propDef);
            }
        }
        //状态
        PropertyDef statusPropertyDef = new BasePropertyDef("status");
        statusPropertyDef.setLabel("状态");
        statusPropertyDef.setDataType(viewCofig.getDataType("Integer"));
        statusPropertyDef.setMapping(Mapping.parseString("1=有效,2=作废"));
        dtResult.addPropertyDef(statusPropertyDef);
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
