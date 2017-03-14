/**
 * AbstractHandler.java
 */
package com.lims.uflo.handler.base;

import com.bstek.uflo.env.Context;
import com.bstek.uflo.model.ProcessInstance;
import com.bstek.uflo.process.handler.AssignmentHandler;
import com.bstek.uflo.process.node.TaskNode;
import com.dosola.core.common.StringUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.Order;
import com.lims.pojo.Project;
import com.lims.pojo.ProjectMethodStandard;
import com.lims.pojo.ProjectRole;
import com.lims.pojo.User;
import com.lims.service.RoleUtil;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @Description: 获取下一个节点的任务处理人
 * @author june
 * @date 2014-6-22 下午3:30:17
 * 
 */
public abstract class AbstractHandler implements AssignmentHandler{

	@Resource
	private IMasterDao dao;

	@Resource
	private RoleUtil roleUtil;
	
	private Logger logger = Logger.getLogger(AbstractHandler.class);
	
	@Override
	public Collection<String> handle(TaskNode taskNode, ProcessInstance processInstance,Context context) {
		//获取订单
		Order order = dao.getObjectById(Order.class,Long.valueOf(processInstance.getBusinessId()));
		if(order==null){
			throw new RuntimeException("没有找到order,orderId:"+processInstance.getBusinessId());
		}
		//order.getProjectMethodStandardIds();

		String roleId = getRoleId();

		if(StringUtil.isEmpty(roleId)){
			logger.error("获取下节点处理人员出错了，没有查找到角色ID");
			throw new RuntimeException("获取下节点处理人员出错了，没有查找到角色ID");
		}
		logger.info("订单编号："+order.getOrderNo()+"，当前节点的角色ID:"+roleId);
		//当前节点的角色的用户
		List<User> users = roleUtil.getUsers(roleId);
		if(users==null || users.size()==0){
			throw new RuntimeException("下节点没有处理人员");
		}
		List<String> userNameList = new ArrayList<String>();
		//如果是到结果录入这步,需要指定项目的检测人员角色
		if(roleId.equals("8a86373e-ab1f-4d0b-a3c6-fea37c01f70e")){
			//当前项目的用户
			List<User> projectUsers = getUsers(order.getProjectMethodStandardIds());
			if(projectUsers==null || projectUsers.size()==0){
				throw new RuntimeException("需要指定项目的检测人员角色");
			}
			for(User user : projectUsers){
				if(users.contains(user)){
					logger.info("当前节点的角色的用户："+user.getUsername());
					userNameList.add(user.getUsername());
				}
			}
		}else{
			for(User user : users){
				logger.info("当前节点的角色的用户："+user.getUsername());
				userNameList.add(user.getUsername());
			}
		}
		if(userNameList.size()==0){
			throw new RuntimeException("下节点没有处理人员");
		}
		return userNameList;
	}
	protected abstract String getRoleId();

	/**
	 * 获取当前项目的所有角色用户,支持查找三级
	 * @param projectMethodStandardIds
	 * @return
     */
	private List<User> getUsers(String projectMethodStandardIds){
		List<User> userList = new ArrayList<User>();
		if(StringUtil.isEmpty(projectMethodStandardIds)){
			return userList;
		}
		String sql = " select pr.roleId as roleId " +
				" from "+ ProjectRole.TABLENAME+" as pr " +
				" join "+ ProjectMethodStandard.TABLENAME+" as pm " +
				" on pr.projectId=pm.projectId" +
				" where pm.id in (:projectMethodStandardIds)";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("projectMethodStandardIds",projectMethodStandardIds);

		List<Map<String,Object>> list = dao.queryBySql(sql,params);
		if(list==null || list.size()==0){
			//查找父级的角色id
			sql = " select pr.roleId as roleId " +
					" from "+ ProjectMethodStandard.TABLENAME+" as pm " +
					" join "+ Project.TABLENAME+" as p " +
					" on p.id=pm.projectId" +
					" join "+ Project.TABLENAME+" as p2 " +
					" on p2.id=p.parentId" +
					" join "+ ProjectRole.TABLENAME+" as pr " +
					" on pr.projectId=p2.id " +
					" where pm.id in (:projectMethodStandardIds) ";
			list = dao.queryBySql(sql,params);
			if(list==null || list.size()==0) {
				//查找父父级角色id
				sql = " select pr.roleId as roleId " +
						" from " + ProjectMethodStandard.TABLENAME + " as pm " +
						" join " + Project.TABLENAME + " as p " +
						" on p.id=pm.projectId" +
						" join " + Project.TABLENAME + " as p2 " +
						" on p2.id=p.parentId" +
						" join " + Project.TABLENAME + " as p3 " +
						" on p3.id=p2.parentId" +
						" join " + ProjectRole.TABLENAME + " as pr " +
						" on pr.projectId=p3.id " +
						" where pm.id in (:projectMethodStandardIds) ";
				list = dao.queryBySql(sql,params);
			}
		}
		if(list==null || list.size()==0) {
			return userList;
		}
		for(Map<String,Object> map : list){
			Object obj = map.get("roleId");
			if(obj==null){
				return userList;
			}
			userList.addAll(roleUtil.getUsers(obj.toString()));
		}
		return userList;
	}

}
