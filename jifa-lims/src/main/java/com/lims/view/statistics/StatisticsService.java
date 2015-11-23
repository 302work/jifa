package com.lims.view.statistics;

import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.SqlKit;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.StatisticInfo;
import com.lims.pojo.StatisticInfoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * 获取统计结果的service
 *
 * @author Song
 * @version 1.0
 * @since 2015-11-15 22:14
 */
public class StatisticsService {

    private IMasterDao dao;

    @DataProvider
    public void queryStatisticsInfoItems(Map<String, Object> params) {
        List<StatisticInfoItem> items = new ArrayList<StatisticInfoItem>();
        StringBuilder sb = new StringBuilder();
        sb.append("select record.testUserName,u.cname as testName,result.status,count(*) as countByStatus ");
        sb.append("from lims_record record ");
        sb.append("join lims_result result ");
        sb.append("on record.id=result.recordId and record.isDeleted=0 and result.isDeleted =0 and record.testUserName=? and testDate >=? and testDate<=? ");
        sb.append("join lims_order order ");
        sb.append("on record.orderId=order.id and order.client like ? ");
        sb.append("join lims_user u ");
        sb.append("on u.username=record.testUserName ");
        sb.append("group by record.testUserName, result.status");
        items=dao.queryBySql(sb.toString(),params,StatisticInfoItem.class);

        for(StatisticInfoItem item:items){
            String testUserName=item.getTestUserName();
            Integer status=item.getStatus();

        }

    }
}
