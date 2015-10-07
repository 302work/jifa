package com.lims.view.resultColumn;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.ResultColumn;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 维护项目的记录项
 * @author june
 * 2015年10月07日 16:01
 */
@Service
public class ResultColumnService {

    @Resource
    private IMasterDao dao;

    @DataProvider
    public void query(Page<ResultColumn> page, Long projectId, Criteria criteria){
        if(projectId==null || projectId==0){
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" From "+ResultColumn.class.getName()+" where isDeleted<>1 ");
        Map<String,Object> params = new HashMap<String, Object>();
        sb.append(" and projectId=:projectId");
        params.put("projectId",projectId);

        //构建查询条件
        ParseResult result = SqlKit.parseCriteria(criteria, true, null, false);
        //排序
        String orderHql = SqlKit.buildOrderHql(criteria, null);

        if(result!=null){
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params.putAll(result.getValueMap());
        }
        if(StringUtils.isEmpty(orderHql)){
            sb.append(" ORDER BY crTime desc");
        }
        sb.append(orderHql);

        dao.pagingQuery(page,sb.toString(),params);
    }

    @DataResolver
    public void save(Collection<ResultColumn> resultColumns,Long projectId){
        if(projectId==null || projectId==0){
            return;
        }
        for (ResultColumn resultColumn : resultColumns) {
            IUser user = ContextHolder.getLoginUser();
            String userName = user.getUsername();
            EntityState state = EntityUtils.getState(resultColumn);
            resultColumn.setProjectId(projectId);
            //如果是删除
            if (EntityState.DELETED.equals(state)) {
                resultColumn.setIsDeleted(1);
                dao.saveOrUpdate(resultColumn);
            }else if (EntityState.NEW.equals(state)) {
                resultColumn.setIsDeleted(0);
                resultColumn.setCrTime(new Date());
                resultColumn.setCrUser(userName);
                dao.saveOrUpdate(resultColumn);
            }else if (EntityState.MODIFIED.equals(state)) {
                dao.saveOrUpdate(resultColumn);
            }
        }
    }
}
