package com.lims.view.consumer;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.Consumer;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户维护
 *
 * @author june
 *         2015年09月22日 23:33
 */
@Service
public class ConsumerService {

    @Resource
    private IMasterDao dao;

    @DataProvider
    public void queryConsumer(Page<Consumer> page, Criteria criteria, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(" From " + Consumer.class.getName() + " where isDeleted<>1 ");
        ParseResult result = SqlKit.parseCriteria(criteria, true, null, false);
        String orderSql = SqlKit.buildOrderHql(criteria, null);
        Map<String, Object> params = new HashMap<String, Object>();
        if (result != null) {
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params = result.getValueMap();
        }

        if (name != null) {
            sb.append(" AND name like :name");
            params.put("name",name);
        }

        if (StringUtils.isEmpty(orderSql)) {
            sb.append(" ORDER BY crTime desc");
        }
        sb.append(orderSql);

        dao.pagingQuery(page, sb.toString(), params);
    }


    @DataResolver
    public void saveConsumer(Collection<Consumer> consumers) {
        for (Consumer consumer : consumers) {
            EntityState state = EntityUtils.getState(consumer);
            IUser user2 = ContextHolder.getLoginUser();
            String userName = user2.getUsername();
            if (EntityState.NEW.equals(state)) {
                consumer.setCrTime(new Date());
                consumer.setCrUser(userName);
                //默认启用
                consumer.setStatus(1);
                consumer.setIsDeleted(0);
                dao.saveOrUpdate(consumer);
            } else if (EntityState.MODIFIED.equals(state)) {
                dao.saveOrUpdate(consumer);
            } else if (EntityState.DELETED.equals(state)) {
                //删除，逻辑删除
                consumer.setIsDeleted(1);
                dao.saveOrUpdate(consumer);
            }
        }
    }

    @Expose
    public void changeStatus(long consumerId, int type) {
        if (type != 1 && type != 2) {
            return;
        }
        String sql = "update " + Consumer.TABLENAME + " set status=:status where id=:consumerId";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("consumerId", consumerId);
        params.put("status", type);
        dao.executeSQL(sql, params);
    }
}
