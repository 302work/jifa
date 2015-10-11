package com.lims.view.standard;

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
import com.lims.pojo.MethodStandard;
import com.lims.pojo.Standard;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 产品标准维护，方法标准维护，产品标准指标维护，以及产品标准与方法标准关联维护，产品标准与指标关联维护
 *
 * @author Song
 * @version 1.0
 * @since 2015-09-26 20:33
 */

@Service
public class StandardService {

    @Resource
    private IMasterDao dao;

    @DataProvider
    public void queryStandards(Page<Standard> page, Criteria criteria) {
        StringBuilder sb = new StringBuilder();
        sb.append(" From " + Standard.class.getName() + " where isDeleted<>1 ");
        ParseResult result = SqlKit.parseCriteria(criteria, true, null, false);
        String orderSql = SqlKit.buildOrderHql(criteria, null);
        Map<String, Object> params = new HashMap<String, Object>();
        if (result != null) {
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params = result.getValueMap();
        }
        if (StringUtils.isEmpty(orderSql)) {
            sb.append(" ORDER BY crTime desc");
        }
        sb.append(orderSql);

        dao.pagingQuery(page, sb.toString(), params);
    }




    @DataResolver
    public void saveStandards(Collection<Standard> standards) {
        for (Standard standard : standards) {
            EntityState state = EntityUtils.getState(standard);
            IUser user = ContextHolder.getLoginUser();
            String userName = user.getUsername();
            if (EntityState.NEW.equals(state)) {
                standard.setCrTime(new Date());
                standard.setCrUser(userName);
                //默认启用
                standard.setStatus(1);
                standard.setIsDeleted(0);
                dao.saveOrUpdate(standard);
            } else if (EntityState.MODIFIED.equals(state)) {
                dao.saveOrUpdate(standard);
            } else if (EntityState.DELETED.equals(state)) {
                //删除，逻辑删除
                standard.setIsDeleted(1);
                dao.saveOrUpdate(standard);

            }
        }
    }
}
