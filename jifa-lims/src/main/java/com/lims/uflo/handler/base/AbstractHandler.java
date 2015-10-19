/**
 * AbstractHandler.java
 */
package com.lims.uflo.handler.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.bstek.uflo.env.Context;
import com.bstek.uflo.model.ProcessInstance;
import com.bstek.uflo.process.handler.AssignmentHandler;
import com.bstek.uflo.process.node.TaskNode;
import com.dosola.core.common.StringUtil;
import com.lims.pojo.Order;
import com.lims.pojo.User;
import com.lims.service.RoleService;

/**
 *
 * @Description: 获取下一个节点的任务处理人
 * @author june
 * @date 2014-6-22 下午3:30:17
 * 
 */
public abstract class AbstractHandler implements AssignmentHandler{
	
	@Resource
	private RoleService roleService;
	
	private Logger logger = Logger.getLogger(AbstractHandler.class);
	
	@Override
	public Collection<String> handle(TaskNode taskNode, ProcessInstance processInstance,Context context) {
		
		Session session = context.getSession();
		//获取订单
		Order order = (Order) session.get(Order.class, Integer.valueOf(processInstance.getBusinessId()));
		String roleId = getRoleId();
		if(StringUtil.isEmpty(roleId)){
			logger.error("获取下节点处理人员出错了，没有查找到角色ID");
			throw new RuntimeException("获取下节点处理人员出错了，没有查找到角色ID");
		}
		logger.info("订单编号："+order.getOrderNo()+"，当前节点的角色ID:"+roleId);
		//当前节点的角色的用户
		List<User> users = roleService.getUsers(roleId);
		if(users==null || users.size()==0){
			throw new RuntimeException("下节点没有处理人员");
		}
		List<String> userNameList = new ArrayList<String>();
		for (User user : users) {
			logger.info("当前节点的角色的用户："+user.getUsername());
			userNameList.add(user.getUsername());
		}
		return userNameList;
	}
	protected abstract String getRoleId();
}
