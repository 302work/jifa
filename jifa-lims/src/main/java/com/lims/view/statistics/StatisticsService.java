package com.lims.view.statistics;

import com.bstek.dorado.annotation.DataProvider;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.StatisticInfo;
import com.lims.pojo.StatisticInfoItem;

import java.util.*;

/**
 * 获取统计结果的service
 *
 * @author Song
 * @version 1.0
 * @since 2015-11-15 22:14
 */
public class StatisticsService {

    private IMasterDao dao;

    @DataProvider
    public Collection<StatisticInfo> queryStatisticsInfoItems(Map<String, Object> params) {
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
        items = dao.queryBySql(sb.toString(), params, StatisticInfoItem.class);

        Map<String, StatisticInfo> infos = new HashMap<String, StatisticInfo>();
        for (StatisticInfoItem item : items) {
            String testUserName = item.getTestUserName();
            Integer status = item.getStatus();
            StatisticInfo info = infos.get(testUserName);
            Integer count=item.getCountByStatus();
            if (info != null) {
                if(status==1){
                    info.setPassedCount(count);
                }else if(status==2){
                    info.setNotPassedCount(count);
                }
                info.setActualTestCount(info.getActualTestCount());
                infos.remove(testUserName);
                infos.put(testUserName,info);
            }else{
                if(status==1){
                    info.setPassedCount(count);
                }else if(status==2){
                    info.setNotPassedCount(count);
                }
                info.setTestUserName(testUserName);
                info.setTestName(item.getTestName());
                info.setActualTestCount(info.getActualTestCount());
                infos.put(testUserName,info);
            }

        }
        return infos.values();
    }
}
