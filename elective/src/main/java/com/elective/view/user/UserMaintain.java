/**
 * 
 */
package com.elective.view.user;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bstek.bdf2.core.security.UserShaPasswordEncoder;
import com.bstek.bdf2.core.service.IUserService;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.common.StringUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.elective.pojo.Dept;
import com.elective.pojo.DeptUser;
import com.elective.pojo.User;

/**
 * 用户维护
 * @author june
 * 2015年6月11日
 */
@Service
public class UserMaintain {
	@Resource
    private IMasterDao dao;
	
	@Resource(name=IUserService.BEAN_ID)
	private IUserService userService;
	
	@Resource(name= UserShaPasswordEncoder.BEAN_ID)
    private PasswordEncoder passwordEncoder;
	
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
        ParseResult result = SqlKit.parseCriteria(criteria,true,"u");
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
