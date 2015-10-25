package com.lims.view.auditOrder;

import com.bstek.dorado.annotation.DataResolver;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.Order;
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


}
