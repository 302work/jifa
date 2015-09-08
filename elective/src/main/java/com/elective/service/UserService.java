package com.elective.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.core.security.SecurityUtils;
import com.bstek.bdf2.core.security.UserShaPasswordEncoder;
import com.bstek.bdf2.core.service.IUserService;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.common.DateUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.elective.pojo.DeptUser;
import com.elective.pojo.User;

/**
 * 用户管理
 * @author june
 * 2015年06月02日 10:13
 */
@SuppressWarnings("deprecation")
public class UserService implements IUserService{

    @Resource
    private IMasterDao dao;

    @Resource(name= UserShaPasswordEncoder.BEAN_ID)
    private PasswordEncoder passwordEncoder;

    @Override
    public void loadPageUsers(Page<IUser> page, String companyId, Criteria criteria) {
        StringBuilder sb = new StringBuilder();
        sb.append( " From "+ User.class.getName() +" as u where u.companyId=:companyId and u.isDeleted=:isDeleted ");
        ParseResult result = SqlKit.parseCriteria(criteria,true,"u",false);
        String orderSql = SqlKit.buildOrderHql(criteria,"u");
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("companyId", companyId);
        params.put("isDeleted", false);
        if(result!=null){
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params.putAll(result.getValueMap());
        }
        if(StringUtils.isEmpty(orderSql)){
            sb.append(" ORDER BY crTime desc");
        }
        sb.append(orderSql);
        dao.pagingQuery(page,sb.toString(),params);
    }

    @SuppressWarnings("unchecked")
	@Override
    public Collection<IUser> loadUsersByDeptId(String deptId) {
        if(StringUtils.isEmpty(deptId)){
            return null;
        }
        //String sql = " select u.* from "+User.TABLENAME+" as u JOIN "+ DeptUser.TABLENAME+" as ud on u.id=ud.userId where ud.deptId=:deptId ";
        //参考http://blog.csdn.net/chenssy/article/details/7728367
        String hql = "select u From "+User.class.getName()+" as u inner join "+DeptUser.class.getName()+" as ud  where u.id=ud.userId and ud.deptId=:deptId ";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("deptId",deptId);
        return (List<IUser>)dao.query(hql,params);
    }

    @Override
    public String checkPassword(String username, String password) {
        //当前登录用户
        User user = (User)ContextHolder.getLoginUser();
        String salt = user.getSalt();
        if (!passwordEncoder
                .isPasswordValid(user.getPassword(), password, salt)) {
            return "密码不正确";
        }
        return null;
    }

    @Override
    public void changePassword(String username, String newPassword) {
        String hql = "update "+User.class.getName()+" set password=:password,salt=:salt where username=:username";
        int salt = RandomUtils.nextInt(1000);
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("password",passwordEncoder.encodePassword(newPassword, salt));
        params.put("salt",salt+"");
        params.put("username",username);
        dao.executeHQL(hql,params);
    }

    @Override
    public void registerAdministrator(String username, String cname,
                                      String ename, String password, String email, String mobile,
                                      String companyId) {
        User user = new User();
        user.setCrUser("system");
        user.setEnabled(true);
        user.setCname(cname);
        user.setUsername(username);
        user.setEname(ename);
        user.setIsDeleted(false);
        int salt = RandomUtils.nextInt(1000);
        password = passwordEncoder.encodePassword(password, salt);
        user.setPassword(password);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setCompanyId(companyId);
        //系统注册功能
//        dao.saveOrUpdate(user);
    }

    @Override
    public IUser newUserInstance(String username) {
        return new User(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String hql = " From "+ User.class.getName() +" where username=:username and isDeleted=:isDeleted ";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("username",username);
        params.put("isDeleted", false);
        List<?> users = dao.query(hql,params);
        if(users==null || users.size()==0){
            throw new UsernameNotFoundException("User " + username
                    + " is not exist");
        }
        return (User)users.get(0);
    }
    
    public User queryUser(String username) throws UsernameNotFoundException {
        String hql = " From "+ User.class.getName() +" where username=:username and isDeleted=:isDeleted ";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("username",username);
        params.put("isDeleted", false);
        List<?> users = dao.query(hql,params);
        if(users==null || users.size()==0){
            return null;
        }
        return (User)users.get(0);
    }
    /**
     * 保存新增的用户
     * @param user
     */
    public User saveUser(User user){
    	IUser user2 = ContextHolder.getLoginUser();
    	String userName = user2.getUsername();
    	String companyId = user2.getCompanyId();
    	user.setCrTime(new Date());
		user.setCrUser(userName);
		user.setEnabled(true);
		user.setCompanyId(companyId);
		//默认密码654321
		String salt = String.valueOf(RandomUtils.nextInt(100));
		String password = passwordEncoder.encodePassword(User.defaultPassword, salt);
		user.setPassword(password);
		user.setSalt(salt);
		user.setIsDeleted(false);
		user = dao.saveOrUpdate(user).get(0);
		return user;
    }
    
    public User saveUser(String deptId, User user) {
		//先删除关联关系
		if(user.getId()!=null && user.getId()!=0){
			String hql = "delete from "+DeptUser.class.getName()+" where userId=:userId and deptId=:deptId";
			Map<String,Object> params = new HashMap<String,Object>();
	        params.put("userId",user.getId());
	        params.put("deptId",deptId);
	        dao.executeHQL(hql, params);
		}
        user = saveUser(user);
		DeptUser du = new DeptUser();
		du.setDeptId(deptId);
		du.setUserId(user.getId());
		dao.saveOrUpdate(du);
		//添加到角色中
		saveRoleMember(user.getUsername(), user.getType());	
		//刷新缓存
		SecurityUtils.refreshUrlSecurityMetadata();
		SecurityUtils.refreshComponentSecurityMetadata();
		return user;
	}
    
	private void saveRoleMember(String userName,int userType){
		deleteRoleMember(userName, userType);;
		String roleId = getRoleId(userType);
		deleteRoleMember(userName, userType);
		String sql = "INSERT INTO `BDF2_ROLE_MEMBER` VALUES ('"+UUID.randomUUID().toString()+"', '"+DateUtil.getDateTimeStr(new Date())+"', null, b'1', null, '"+roleId+"', '"+userName+"', null)";
		dao.executeSQL(sql, null);
	}
	
	private void deleteRoleMember(String userName,int userType){
		String roleId = getRoleId(userType);
		String sql = " delete from `BDF2_ROLE_MEMBER` where username_='"+userName+"' and role_id_='"+roleId+"'";
		dao.executeSQL(sql, null);
	}
	private String getRoleId(int userType){
		//2c32476e-0f95-48d5-8023-423036a99b25 老师
		//c2517ee5-e6a1-43ed-a4f6-676437655bb8 管理员
		//f2b74c58-205f-4b71-96cf-7ea00854d7db 学生
		String roleId = "";
		//1为学生，2为老师，3为管理员
		switch (userType) {
			case 1:
				roleId = "f2b74c58-205f-4b71-96cf-7ea00854d7db";
				break;
			case 2:
				roleId = "2c32476e-0f95-48d5-8023-423036a99b25";	
				break;
			case 3:
				roleId = "c2517ee5-e6a1-43ed-a4f6-676437655bb8";
				break;
		}
		return roleId;
	}
    /**
     * 删除用户
     */
    public void deleteUser(User user){
    	user.setIsDeleted(true);
		dao.saveOrUpdate(user);
    }
}
