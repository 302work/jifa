package com.lims.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import com.bstek.bdf2.core.business.IDept;
import com.bstek.bdf2.core.business.IPosition;
import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.model.DefaultPosition;
import com.bstek.bdf2.core.model.Group;
import com.bstek.bdf2.core.model.RoleMember;
import com.bstek.bdf2.core.service.IDeptService;
import com.bstek.bdf2.core.service.IRoleService;
import com.lims.pojo.Dept;
import com.lims.pojo.User;



public class RoleService {
	
	private IRoleService roleService;
	
	@Resource(name=IDeptService.BEAN_ID)
	private IDeptService deptService;
	
	@Resource
	private UserService userService;
	
	/**
	 * 根据角色ID获取该角色下的所有用户
	 * @param roleName
	 * @return
	 */
	public List<User> getUsers(String roleId){
		List<RoleMember> roleMemberList = roleService.loadRoleMemberByRoleId(roleId);
		List<User> users = new ArrayList<User>();
		for (RoleMember roleMember : roleMemberList) {
			//角色的用户
			User u = (User) roleMember.getUser();
			if(u!=null){
				users.add(u);
			}
			//角色的部门
			Dept d = (Dept) roleMember.getDept();
			if(d!=null){
				String deptId = d.getId();
				Collection<IUser> deptUsers = userService.loadUsersByDeptId(deptId);
				for (IUser user : deptUsers) {
					users.add((User)user);
				}
			}
			//角色的群组
			Group group = roleMember.getGroup();
			if(group!=null){
				//群组的用户
				Collection<IUser> groupUsers = group.getUsers();
				if(groupUsers!=null){
					for (IUser user : groupUsers) {
						users.add((User)user);
					}
				}
				//群组的部门
				List<IDept> depts = group.getDepts();
				if(depts!=null && depts.size()>0){
					for (IDept dept : depts) {
						String deptId = dept.getId();
						Collection<IUser> deptUsers = userService.loadUsersByDeptId(deptId);
						for (IUser user : deptUsers) {
							users.add((User)user);
						}
					}
				}
				
				//群组的职位
				List<IPosition> positions = group.getPositions();
				if(positions!=null && positions.size()>0){
					for (IPosition position : positions) {
						String positionId = position.getId();
						Collection<IUser> pUsers = userService.loadUsersByPositionId(positionId);
						for (IUser user : pUsers) {
							users.add((User)user);
						}
					}
				}
			}
			//角色的职位
			DefaultPosition position = (DefaultPosition) roleMember.getPosition();
			if(position!=null){
				String positionId = position.getId();
				Collection<IUser> pUsers = userService.loadUsersByPositionId(positionId);
				for (IUser user : pUsers) {
					users.add((User)user);
				}
			}
			
			
		}
		return users;
	}
	
}
