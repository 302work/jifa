package com.lims.view.standard;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Page;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.StandardIndex;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Song
 * @version 1.0
 * @since 2015-10-11 13:12
 */

@Service
public class StandardIndexService {
    @Resource
    private IMasterDao dao;

    @DataProvider
    public void queryStandardIndexs(Page<StandardIndex> page,Long standardId){
        StringBuilder sb = new StringBuilder();
        sb.append(" From " + StandardIndex.class.getName() + " WHERE isDeleted<>1 ");
        sb.append("AND standardId =:standardId ");
        sb.append("ORDER BY crTime DESC");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("standardId", standardId);
        dao.pagingQuery(page, sb.toString(), map);
    }

    @DataResolver
    public void saveMethodStandards(Collection<StandardIndex> standardIndexs) {

        for (StandardIndex si : standardIndexs) {
            EntityState state = EntityUtils.getState(si);
            IUser user = ContextHolder.getLoginUser();
            String userName = user.getUsername();
            if (EntityState.NEW.equals(state)) {
                si.setCrTime(new Date());
                si.setCrUser(userName);
                //默认启用
                si.setIsDeleted(0);
                dao.saveOrUpdate(si);
            } else if (EntityState.MODIFIED.equals(state)) {
                dao.saveOrUpdate(si);
            } else if (EntityState.DELETED.equals(state)) {
                //删除，逻辑删除
                si.setIsDeleted(1);
                dao.saveOrUpdate(si);

            }
        }
    }
}
