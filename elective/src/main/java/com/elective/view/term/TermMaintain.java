package com.elective.view.term;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.elective.pojo.Term;

/**
 * 学期维护 
 * @author june
 * 2015年6月20日
 */
@Component("termPR")
public class TermMaintain {
	
	@Resource
    private IMasterDao dao;

	@DataProvider
	public void queryTerm(Page<Term> page, Criteria criteria) {
		StringBuilder sb = new StringBuilder();
        sb.append( " From "+ Term.class.getName() +" where 1=1 ");
        ParseResult result = SqlKit.parseCriteria(criteria,true,null,false);
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
        
        dao.pagingQuery(page,sb.toString(),params);
	}
	
	@SuppressWarnings("unchecked")
	@DataProvider
	public Collection<Term> queryTerms() {
        String hql =  " From "+ Term.class.getName()+" ORDER BY crTime desc";
        return (Collection<Term>) dao.query(hql, null);
	}
	
	@DataResolver
	public void saveTerm(Collection<Term> terms){
		for (Term term : terms) {
			EntityState state = EntityUtils.getState(term);
			IUser user2 = ContextHolder.getLoginUser();
	    	String userName = user2.getUsername();
			if(EntityState.NEW.equals(state)){
				term.setCrTime(new Date());
				term.setCrUser(userName);
				dao.saveOrUpdate(term);
			}else if(EntityState.MODIFIED.equals(state)){
				dao.saveOrUpdate(term);
			}else if (EntityState.DELETED.equals(state)) {
				//禁止删除
				//dao.delete(term);
			}
		}
	}

}