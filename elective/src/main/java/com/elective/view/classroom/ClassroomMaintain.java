/**
 * 
 */
package com.elective.view.classroom;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

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
import com.elective.pojo.Classroom;
import com.elective.pojo.Course;

/**
 * 教室维护
 * @author june
 * 2015年6月20日
 */
@Service
public class ClassroomMaintain {

	@Resource
    private IMasterDao dao;
	
	@DataProvider
    public void queryClassroom(Page<Classroom> page, Criteria criteria){
        StringBuilder sb = new StringBuilder();
        sb.append( " From "+ Classroom.class.getName() +" where isEnable=true ");
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
	
	@DataResolver
	public void saveClassroom(Collection<Classroom> crs) throws Exception{
		for (Classroom cr : crs) {
			EntityState state = EntityUtils.getState(cr);
			IUser user2 = ContextHolder.getLoginUser();
	    	String userName = user2.getUsername();
			if(EntityState.NEW.equals(state)){
				cr.setCrTime(new Date());
				cr.setCrUser(userName);
				dao.saveOrUpdate(cr);
			}else if(EntityState.MODIFIED.equals(state)){
				dao.saveOrUpdate(cr);
			}else if (EntityState.DELETED.equals(state)) {
				//校验教室是否已被使用
				String hql = "From "+Course.class.getName()+" where classroomId=:classroomId ";
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("classroomId", cr.getId());
				if(dao.queryCount(hql, params)>0){
					throw new Exception("教室已被使用，无法删除，您可禁用该教室。");
				}
				dao.delete(cr);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@DataProvider
	public Collection<Classroom> queryClassrooms() {
        String hql =  " From "+ Classroom.class.getName()+" where isEnable=true ORDER BY crTime desc";
        return (Collection<Classroom>) dao.query(hql, null);
	}
}
