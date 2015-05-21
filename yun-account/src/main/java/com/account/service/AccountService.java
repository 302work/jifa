package com.account.service;

import com.account.pojo.Account;
import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.core.exception.NoneLoginException;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.jifa.core.dao.interfaces.IMasterDao;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.*;

/**
 * 记账本操作相关
 * @author june
 * 2015年05月17日 13:26
 */
@Service
public class AccountService {

    @Resource
    private IMasterDao dao;

    @Resource
    private RecordService recordService;

    @SuppressWarnings("unchecked")
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

    /**
     * 检查记账本底下是否有子记账本
     * @param accountId
     * @return
     */
    @Expose
    public int checkAccount(Long accountId){
        String hql = "from "+Account.class.getName()+" where isDeleted=:isDeleted and parentAccountId=:accountId ";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("accountId",accountId);
        params.put("isDeleted",false);
        return dao.queryCount(hql,params);
    }

    @DataResolver
    public void saveAccounts(Collection<Account> accounts){
        IUser user = ContextHolder.getLoginUser();
        if (user == null) {
            throw new NoneLoginException("Please login first");
        }
        for (Account account : accounts) {
            EntityState state = EntityUtils.getState(account);

            if (state.equals(EntityState.NEW)) {
                account.setCrTime(new Date());
                account.setCrUser(user.getUsername());
                account.setIsDeleted(false);
                dao.saveOrUpdate(account);
            }
            if (state.equals(EntityState.MODIFIED) || state.equals(EntityState.MOVED)) {
                dao.saveOrUpdate(account);
            }

            if (state.equals(EntityState.DELETED)) {
                if (checkAccount(account.getId()) == 0) {
                    recordService.deleteRecords(account.getId());
                    account.setIsDeleted(true);
                    dao.saveOrUpdate(account);
                } else {
                    throw new RuntimeException("请先删除子分类");
                }
            }
        }
    }

}
