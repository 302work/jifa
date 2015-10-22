package com.lims.view.order;

import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.provider.Page;
import com.lims.pojo.Order;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * 申请单或报告service
 * @author Song
 * @version 1.0
 * @since 2015-10-21 23:01
 */
@Service
public class OrderService {

    @DataProvider
    public void queryOrder(Page<Order> page){

    }

    @DataResolver
    public void saveOrder(Collection<Order> orders){

    }
}
