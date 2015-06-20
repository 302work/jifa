/**
 * 
 */
package com.elective.view.dept;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bstek.bdf2.core.business.IDept;
import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.core.exception.NoneLoginException;
import com.bstek.bdf2.core.security.UserShaPasswordEncoder;
import com.bstek.bdf2.core.service.IDeptService;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.elective.pojo.Dept;
import com.elective.pojo.DeptUser;
import com.elective.pojo.User;
import com.elective.service.UserService;

/**
 * 部门维护
 * @author june
 * 2015年6月11日
 */
@SuppressWarnings("deprecation")
@Service
public class DeptMaintain {
	@Resource
    private IMasterDao dao;
	
	@Resource(name=IDeptService.BEAN_ID)
	private IDeptService deptService;
	
	@Resource(name= UserShaPasswordEncoder.BEAN_ID)
    private PasswordEncoder passwordEncoder;
	
	@Resource
	private UserService userService;
	
	@DataProvider
    public List<Dept> getDeptsByParentId(String deptId){
		IUser user = ContextHolder.getLoginUser();
        if (user == null) {
            throw new NoneLoginException("Please login first");
        }
        List<IDept> list = deptService.loadDeptsByParentId(deptId, user.getCompanyId());
        List<Dept> depts = new ArrayList<Dept>();
        if(list!=null && list.size()>0){
        	for (IDept idept : list) {
    			Dept dept = (Dept)idept;
            	dept.setHasChild(hasChild(dept));
            	depts.add(dept);
    		}
        }
        return depts;
    }
	
    
  //前台传递的是整棵树，见http://www.bsdn.org/projects/dorado7/issue/dorado7-8718
    @DataResolver
    public void saveDepts(Collection<Dept> depts){
        IUser user = ContextHolder.getLoginUser();
        if (user == null) {
            throw new NoneLoginException("Please login first");
        }
        for (Dept dept : depts) {
        	doSaveOrUpdateDept(null, dept);
        }
    }
    /**
     * 检查部门底下是否有子部门
     * @param accountId
     * @return
     */
    private boolean hasChild(Dept dept){
        String hql = "from "+Dept.class.getName()+" where parentId=:deptId and isDeleted=:isDeleted ";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("deptId",dept.getId());
        params.put("isDeleted", false);
        return dao.queryCount(hql,params)>0;
    }
    /**
     * 检查部门底下是否有用户
     * @param accountId
     * @return
     */
    private boolean hasUser(Dept dept){
        String hql = "from "+DeptUser.class.getName()+" where deptId=:deptId ";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("deptId",dept.getId());
        return dao.queryCount(hql,params)>0;
    }
    /**
     * 保存部门
     * @param parentId
     * @param dept
     */
    private void doSaveOrUpdateDept(String parentId,Dept dept){
    	IUser user = ContextHolder.getLoginUser();
    	String userName = user.getUsername();
    	String companyId = user.getCompanyId();
    	EntityState state = EntityUtils.getState(dept);
    	//不是none并且不是delete
    	if(EntityState.isVisibleDirty(state)){
    		dept.setParentId(parentId);
    	}
    	Dept newDept = new Dept();
    	//如果是删除
    	if (EntityState.DELETED.equals(state)) {
    		if (!hasChild(dept) && !hasUser(dept)) {
    			dept.setIsDeleted(true);
    			dao.saveOrUpdate(dept);
            } else {
                throw new RuntimeException("请先删除子部门和部门底下的用户");
            }
    	}else if (EntityState.NEW.equals(state)) {
			dept.setCrTime(new Date());
			dept.setCrUser(userName);
			dept.setCompanyId(companyId);
			dept.setIsDeleted(false);
			dept.setType(1);
			newDept = dao.saveOrUpdate(dept).get(0);
		}else if (EntityState.MODIFIED.equals(state)
				|| EntityState.MOVED.equals(state)) {
			dept.setCrTime(new Date());
			dept.setCrUser(userName);
			dao.saveOrUpdate(dept);
		}
    	String deptId = dept.getId();
		if(newDept.getId()!=null){
			deptId = newDept.getId();
		}
    	//子部门
    	List<Dept> subDepts = EntityUtils.getValue(dept, "child");
    	if(subDepts!=null && subDepts.size()>0){
    		for (Dept subDept : subDepts) {
    			doSaveOrUpdateDept(deptId, subDept);
			}
    	}
    	//用户
    	List<User> users = EntityUtils.getValue(dept, "users");
    	if(users!=null && users.size()>0){
    		for (User user2 : users) {
				doSaveOrUpdateUser(deptId,user2);
			}
    	}
    	
    }

    /**
     * 添加删除用户
     * @param accountId
     * @param record
     */
	private void doSaveOrUpdateUser(String deptId, User user) {
		EntityState state = EntityUtils.getState(user);
		//先删除关联关系
		String hql = "delete from "+DeptUser.class.getName()+" where userId=:userId and deptId=:deptId";
		Map<String,Object> params = new HashMap<String,Object>();
        params.put("userId",user.getId());
        params.put("deptId",deptId);
        dao.executeHQL(hql, params);
		if(EntityState.NEW.equals(state)){
			user = userService.saveUser(user);
			DeptUser du = new DeptUser();
			du.setDeptId(deptId);
			du.setUserId(user.getId());
			dao.saveOrUpdate(du);
		}else if(EntityState.MODIFIED.equals(state)){
			dao.saveOrUpdate(user);
			DeptUser du = new DeptUser();
			du.setDeptId(deptId);
			du.setUserId(user.getId());
			dao.saveOrUpdate(du);
		}else if (EntityState.DELETED.equals(state)) {
			userService.deleteUser(user);
		}	
	}
}
