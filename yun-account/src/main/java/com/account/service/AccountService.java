package com.account.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.account.pojo.Account;
import com.account.pojo.Record;
import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.core.exception.NoneLoginException;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.dosola.core.dao.interfaces.IMasterDao;

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
        hql += " order by sortFlag ";
        params.put("isDeleted",false);
        List<Account> list = (List<Account>)dao.query(hql,params);
        checkChild(list);
        return list;
    }

    /**
     * 检查是否有子分类
     * @param list
     */
    private void checkChild(List<Account> list) {
		for (Account account : list) {
			String hql = "From "+Account.class.getName()+" where parentAccountId=:parentAccountId and isDeleted=:isDeleted ";
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("isDeleted",false);
			params.put("parentAccountId",account.getId());
			int count = dao.queryCount(hql, params);
			account.setHasChild(count>0);
		}
		
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

    //前台传递的是整棵树，见http://www.bsdn.org/projects/dorado7/issue/dorado7-8718
    @DataResolver
    public void saveAccounts(Collection<Account> accounts){
        IUser user = ContextHolder.getLoginUser();
        if (user == null) {
            throw new NoneLoginException("Please login first");
        }
        for (Account account : accounts) {
        	doSaveOrUpdateAccount(null, account);
        }
    }
    /**
     * 保存分类
     * @param parentAccountId
     * @param account
     */
    private void doSaveOrUpdateAccount(Long parentAccountId,Account account){
    	String userName = ContextHolder.getLoginUser().getUsername();
    	EntityState state = EntityUtils.getState(account);
    	//不是none并且不是delete
    	if(EntityState.isVisibleDirty(state)){
    		account.setParentAccountId(parentAccountId);
    	}
    	Account newAccount = new Account();
    	//如果是删除
    	if (EntityState.DELETED.equals(state)) {
    		if (checkAccount(account.getId()) == 0) {
                recordService.deleteRecords(account.getId());
                account.setIsDeleted(true);
                account.setDelTime(new Date());
                account.setDelUser(userName);
                dao.saveOrUpdate(account);
            } else {
                throw new RuntimeException("请先删除子分类");
            }
    	}else if (EntityState.NEW.equals(state)) {
			account.setCrTime(new Date());
			account.setCrUser(userName);
			account.setIsDeleted(false);
			newAccount = dao.saveOrUpdate(account).get(0);
		}else if (EntityState.MODIFIED.equals(state)
				|| EntityState.MOVED.equals(state)) {
			account.setCrTime(new Date());
			account.setCrUser(userName);
			dao.saveOrUpdate(account);
		}
    	Long accountId = account.getId();
		if(newAccount.getId()!=null){
			accountId = newAccount.getId();
		}
    	//子分类
//    	List<Account> subAccounts = account.getChild();
    	List<Account> subAccounts = EntityUtils.getValue(account, "child");
    	if(subAccounts!=null && subAccounts.size()>0){
    		for (Account subAccount : subAccounts) {
    			doSaveOrUpdateAccount(accountId, subAccount);
			}
    	}
    	//记录
    	List<Record> records = EntityUtils.getValue(account, "records");
    	if(records!=null && records.size()>0){
    		for (Record record : records) {
				doSaveOrUpdateRecord(accountId,record);
			}
    	}
    	
    }

    /**
     * 保存记录
     * @param accountId
     * @param record
     */
	private void doSaveOrUpdateRecord(Long accountId, Record record) {
		EntityState state = EntityUtils.getState(record);
//		EntityWrapper recordEntity = EntityWrapper.create(record);
		String userName = ContextHolder.getLoginUser().getUsername();
		if(EntityState.NEW.equals(state)){
			record.setAccountId(accountId);
			record.setCrTime(new Date());
			record.setCrUser(userName);
			record.setIsDeleted(false);
			dao.saveOrUpdate(record);
		}else if(EntityState.MODIFIED.equals(state)){
			dao.saveOrUpdate(record);
		}else if (EntityState.DELETED.equals(state)) {
			record.setIsDeleted(true);
			record.setDelTime(new Date());
			record.setDelUser(userName);
			dao.saveOrUpdate(record);
		}	
	}

}
