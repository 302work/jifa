package com.account.service;

import com.account.pojo.Account;
import com.bstek.dorado.annotation.DataProvider;
import com.jifa.core.dao.interfaces.IMasterDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记账本操作相关
 * @author june
 * 2015年05月17日 13:26
 */
@Service
public class AccountService {

    @Resource
    private IMasterDao dao;

    @DataProvider
    public List<Account> getAccountsByParentId(Long accountId){
        Map<String,Object> params = new HashMap<String,Object>();
        String hql = "From "+ Account.class.getName()+" where isDeleted=:isDeleted ";
        if(accountId==null){
            hql += " and (parentAccountId=0 or parentAccountId is null) ";
        }else{
            hql += " and parentAccountId=:accountId ";
            params.put("accountId",accountId);
        }
        params.put("isDeleted",false);
        return (List<Account>)dao.query(hql,params);
    }


}
