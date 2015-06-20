/**
 * 
 */
package com.elective.view.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.core.security.UserShaPasswordEncoder;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.elective.pojo.DeptUser;
import com.elective.pojo.User;
import com.elective.service.UserService;

/**
 * 用户维护
 * @author june
 * 2015年6月11日
 */
@SuppressWarnings("deprecation")
@Service
public class UserMaintain {
	@Resource
    private IMasterDao dao;
	
	@Resource
	private UserService userService;
	
	@Resource(name= UserShaPasswordEncoder.BEAN_ID)
    private PasswordEncoder passwordEncoder;
	
	@DataResolver
	public void saveUser(Collection<User> users) throws Exception {
		for (User user : users) {
			EntityState state = EntityUtils.getState(user);
			if(EntityState.NEW.equals(state)){
				userService.saveUser(user);
			}else if(EntityState.MODIFIED.equals(state)){
				dao.saveOrUpdate(user);
			}else if (EntityState.DELETED.equals(state)) {
				userService.deleteUser(user);
			}
		}
	}
	
	 /**
	  * 查询部门下的所有用户
	  * @param page
	  * @param deptId
	  * @param criteria
	  */
    @DataProvider
    public void getUsersByDeptId(Page<User> page, String deptId, Criteria criteria){
    	StringBuilder sb = new StringBuilder();
    	sb.append(" select u From "+DeptUser.class.getName()+" as du ");
    	sb.append(" inner join du.user as u ");
    	sb.append(" where u.id=du.userId ");
    	sb.append(" and du.deptId=:deptId ");
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("deptId",deptId);
        ParseResult result = SqlKit.parseCriteria(criteria,true,"u",false);
        String orderSql = SqlKit.buildOrderHql(criteria,"u");
        if(result!=null){
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params.putAll(result.getValueMap());
        }
        if(StringUtils.isEmpty(orderSql)){
            sb.append(" ORDER BY u.crTime desc");
        }
        sb.append(orderSql);
        dao.pagingQuery(page,sb.toString(),params);
    }
    
    /**
     * 查询老师
     * @return
     */
    @SuppressWarnings("unchecked")
    @DataProvider
	public Collection<User> queryTeachers(){
    	User user = (User) ContextHolder.getLoginUser();
    	//如果是管理员，查询所有老师
    	if(user.getType()==3){
    		String hql = "From "+User.class.getName()+" where isDeleted=0 and type=2 and enabled=true";
        	return (Collection<User>) dao.query(hql, null);
    	}
    	//如果是老师，只能指定自己
    	if(user.getType()==2){
    		List<User> users = new ArrayList<User>();
    		users.add(user);
    		return users;
    	}
    	return null;
    }

}
