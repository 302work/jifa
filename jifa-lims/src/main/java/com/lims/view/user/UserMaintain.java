/**
 * 
 */
package com.lims.view.user;

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
import com.lims.pojo.DeptUser;
import com.lims.pojo.User;
import com.lims.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    
}
