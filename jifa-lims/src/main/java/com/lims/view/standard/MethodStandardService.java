package com.lims.view.standard;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.MethodStandard;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;

/**
 * @author Song
 * @version 1.0
 * @since 2015-10-07 16:05
 */

@Service
public class MethodStandardService {
    @Resource
    private IMasterDao dao;

    @DataResolver
    public void saveMethodStandards(Collection<MethodStandard> methodStandards) {

        for (MethodStandard ms : methodStandards) {
            EntityState state = EntityUtils.getState(ms);
            IUser user = ContextHolder.getLoginUser();
            String userName = user.getUsername();
            if (EntityState.NEW.equals(state)) {
                ms.setCrTime(new Date());
                ms.setCrUser(userName);
                //默认启用
                ms.setStatus(1);
                ms.setIsDeleted(0);
                dao.saveOrUpdate(ms);
            } else if (EntityState.MODIFIED.equals(state)) {
                dao.saveOrUpdate(ms);
            } else if (EntityState.DELETED.equals(state)) {
                //删除，逻辑删除
                ms.setIsDeleted(1);
                dao.saveOrUpdate(ms);

            }
        }
    }
}
