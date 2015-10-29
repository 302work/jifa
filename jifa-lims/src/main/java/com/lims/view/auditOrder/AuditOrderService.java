package com.lims.view.auditOrder;

import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.Order;
import com.lims.pojo.RecordTestCondition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

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



    @DataResolver
    public void saveOrder(Collection<Order> orders){

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




}
