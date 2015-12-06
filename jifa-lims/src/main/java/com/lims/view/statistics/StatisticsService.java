package com.lims.view.statistics;

import com.bstek.dorado.annotation.DataProvider;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.StatisticInfo;
import com.lims.pojo.StatisticInfoItem;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 获取统计结果的service
 *
 * @author Song
 * @version 1.0
 * @since 2015-11-15 22:14
 */
@Service
public class StatisticsService {

    @Resource
    private IMasterDao dao;

    @DataProvider
    public Collection<StatisticInfo> queryStatisticsInfo(Map<String, Object> params) {
        List<StatisticInfoItem> items = new ArrayList<StatisticInfoItem>();
        String testUserNameParam = null;
        Date testDateStartParam = null;
        Date testDateEndParam = null;
        String clientParam = null;
        if (params != null && params.size() > 0) {
            testUserNameParam = (String) params.get("testUserName");
            testDateStartParam = (Date) params.get("testDateStart");
            testDateEndParam = (Date) params.get("testDateEnd");
            clientParam = (String) params.get("client");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" select record.testUserName,u.cname as testName,result.status,count(*) as countByStatus ");
        sb.append(" from lims_record record ");
        sb.append(" join lims_result result ");
        sb.append(" on record.id=result.recordId and record.isDeleted=0 and result.isDeleted =0 ");
        if (!StringUtils.isEmpty(testUserNameParam)) {
            sb.append(" and record.testUserName=:testUserName ");
        }
        if (testDateStartParam != null && testDateEndParam != null) {
            sb.append(" and testDate >=:testDateStart and testDate <=:testDateEnd ");
        }
        sb.append(" join lims_order orders ");
        sb.append(" on record.orderId=orders.id ");
        if (!StringUtils.isEmpty(clientParam)) {
            sb.append(" and orders.client like ? ");
        }
        sb.append(" join lims_user u ");
        sb.append(" on u.username=record.testUserName ");
        sb.append(" group by record.testUserName, result.status ");
        items = dao.queryBySql(sb.toString(), params, StatisticInfoItem.class);

        Map<String, StatisticInfo> infos = new HashMap<String, StatisticInfo>();
        for (StatisticInfoItem item : items) {
            String testUserName = item.getTestUserName();
            Integer status = item.getStatus();
            StatisticInfo info = infos.get(testUserName);
            Integer count = item.getCountByStatus();
            if (info != null) {
                if (status == 1) {
                    info.setPassedCount(count);
                } else if (status == 2) {
                    info.setNotPassedCount(count);
                }
                info.setActualTestCount(info.getActualTestCount());
                infos.remove(testUserName);
                infos.put(testUserName, info);
            } else {
                info = new StatisticInfo();
                if (status == 1) {
                    info.setPassedCount(count);
                } else if (status == 2) {
                    info.setNotPassedCount(count);
                }
                info.setTestUserName(testUserName);
                info.setTestName(item.getTestName());
                info.setActualTestCount(info.getActualTestCount());
                infos.put(testUserName, info);
            }

        }
        Collection<StatisticInfo> result = new ArrayList<StatisticInfo>();
        for (String userName : infos.keySet()) {
            result.add(infos.get(userName));
        }
        return result;
    }
}
