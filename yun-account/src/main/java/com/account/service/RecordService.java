package com.account.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.account.pojo.Record;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.data.provider.Page;
import com.dosola.core.dao.interfaces.IMasterDao;

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
    public void getRecordsByAccountId(Page<Record> page, long accountId){
        String hql = "From "+ Record.class.getName()+" where accountId=:accountId and isDeleted=:isDeleted";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("accountId",accountId);
        params.put("isDeleted",false);
        dao.pagingQuery(page,hql,params);
    }

    /**
     * 删除某个记账本的所有记录
     * @param accountId
     */
    public void deleteRecords(Long accountId){
        String hql = " update "+Record.class.getName()+" set isDeleted=:isDeleted where accountId=:accountId ";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("accountId",accountId);
        params.put("isDeleted",true);
        dao.executeHQL(hql,params);
    }


}
