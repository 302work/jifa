package com.account.service;

import com.account.pojo.Account;
import com.account.pojo.Record;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.data.provider.Page;
import com.jifa.core.dao.interfaces.IMasterDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 记录操作相关
 * @author june
 * 2015年05月17日 13:29
 */
@Service
public class RecordService {

    @Resource
    private IMasterDao dao;

    @DataProvider
    public void getRecordsByAccountId(Page<Account> page, long accountId){
        String hql = "From "+ Record.class.getName()+" where accountId=:accountId and isDeleted=:isDeleted";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("accountId",accountId);
        params.put("isDeleted",false);
        dao.pagingQuery(page,hql,params);
    }
}
