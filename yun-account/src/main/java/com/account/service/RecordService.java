package com.account.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.account.pojo.Record;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.common.StringUtil;
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

    /**
     * 查询该分类下的所有记录，包括子分类的
     * @param page
     * @param accountId
     */
    @DataProvider
    public void getRecordsByAccountId(Page<Record> page, long accountId, Criteria criteria){
        //所有子分类
    	Long[] aIds = getChildAccountIds(accountId);
        StringBuilder sb = new StringBuilder();
        sb.append( " From "+ Record.class.getName() +" where accountId in (:accountIds) and isDeleted=:isDeleted ");
        ParseResult result = SqlKit.parseCriteria(criteria,true,null);
        String orderSql = SqlKit.buildOrderHql(criteria,null);
        Map<String,Object> params = new HashMap<String, Object>();
        if(result!=null){
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params = result.getValueMap();
        }
        if(StringUtils.isEmpty(orderSql)){
            sb.append(" ORDER BY crTime desc");
        }
        sb.append(orderSql);
        
        params.put("accountIds",aIds);
        params.put("isDeleted",false);
        dao.pagingQuery(page,sb.toString(),params);
    }
    /**
     * 查找所有子分类
     * @param accountId
     * @return
     */
    private Long[] getChildAccountIds(Long accountId){
    	String sql = "select getChildList('"+accountId+"') as accountIds";
        String accountIds = dao.queryBySql(sql, null).get(0).get("accountIds").toString();
        String[] accountIdArray = accountIds.split(",");
        Long[] aIds = new Long[accountIdArray.length];
        for (int i=0;i<accountIdArray.length;i++) {
        	aIds[i] = Long.valueOf(accountIdArray[i]);
		}
        return aIds;
    }
    @Expose
    public Map<String,Object> getTotal(Long accountId){
    	Map<String,Object> resultMap = new HashMap<String, Object>();
    	//所有子分类
    	Long[] aIds = getChildAccountIds(accountId);
    	
    	Map<String,Object> params = new HashMap<String, Object>();
    	String sql = "select sum(money) as total from "+Record.TABLENAME+" where isDeleted=:isDeleted and accountId in (:accountIds) and type=:type ";
    	params.put("isDeleted", false);
    	params.put("accountIds", aIds);
    	params.put("type", 1);
    	Object obj = dao.queryBySql(sql, params).get(0).get("total");
    	double zhichu = 0.0;
    	if(obj!=null){
    		zhichu =  Double.valueOf(obj.toString());
    	}
    	params.put("type", 2);
    	Object obj2 = dao.queryBySql(sql, params).get(0).get("total");
    	double shouru = 0.0;
    	if(obj2!=null){
    		shouru =  Double.valueOf(obj2.toString());
    	}
    	
    	resultMap.put("zhichu", zhichu);
    	resultMap.put("shouru", shouru);
    	resultMap.put("shengyu", StringUtil.sub(shouru, zhichu));
    	
		return resultMap;
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
