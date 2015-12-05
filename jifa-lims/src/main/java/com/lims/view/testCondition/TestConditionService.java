package com.lims.view.testCondition;

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
import com.lims.pojo.RecordTestCondition;
import com.lims.pojo.TestCondition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author june
 *         2015年10月25日 20:58
 */
@Service
public class TestConditionService {
    @Resource
    private IMasterDao dao;

    @DataProvider
    public void query(Page<TestCondition> page, Long projectId, Criteria criteria){
        if(projectId==null || projectId<=0){
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" From "+TestCondition.class.getName()+" where 1=1 ");
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

        sb.append(orderHql);

        dao.pagingQuery(page,sb.toString(),params);
    }

    @DataProvider
    public List<RecordTestCondition> queryTestCondition(Long recordId){
        if(recordId==null || recordId<=0){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" From "+RecordTestCondition.class.getName()+" where recordId=:recordId ");
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("recordId",recordId);
        return (List<RecordTestCondition>) dao.query(sb.toString(),params);
    }

    @DataProvider
    public List<RecordTestCondition> queryProjectTestCondition(Long orderId,Long projectMethodStandardId){
        if(orderId==null || orderId<=0){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" From "+RecordTestCondition.class.getName()+" where recordId=:recordId ");
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("recordId",orderId);
        return (List<RecordTestCondition>) dao.query(sb.toString(),params);
    }

    @DataResolver
    public void save(Collection<TestCondition> testConditions,Long projectId){
        if(projectId==null || projectId==0){
            return;
        }
        for (TestCondition testCondition : testConditions) {
            IUser user = ContextHolder.getLoginUser();
            String userName = user.getUsername();
            EntityState state = EntityUtils.getState(testCondition);
            testCondition.setProjectId(projectId);
            //如果是删除
            if (EntityState.DELETED.equals(state)) {
                dao.delete(testCondition);
            }else if (EntityState.NEW.equals(state)) {
                dao.saveOrUpdate(testCondition);
            }else if (EntityState.MODIFIED.equals(state)) {
                dao.saveOrUpdate(testCondition);
            }
        }
    }
}
