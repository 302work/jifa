/**
 * 
 */
package com.dorado.intercept;

import com.bstek.dorado.common.proxy.PatternMethodInterceptor;
import com.bstek.dorado.data.JsonUtils;
import com.bstek.dorado.web.DoradoContext;
import com.dosola.core.annotation.DosolaTx;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.NestedTransactionHelpException;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * dorado全局拦截器，用于实现事务处理
 * @author june
 * 2014年11月28日 下午5:40:18
 * 
 */
@Component
public class ViewServiceTxInterceptor extends PatternMethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		Object object = null;
		//解析上下文
		Map<String,Object> paramMap = parseParam(invocation);
		//当前服务类
		Object beanObject = paramMap.get("beanObj");
		//当前方法名
		String methodName = paramMap.get("methodName").toString();
		//检查类上是否有事务注解
		DosolaTx dosolaTx = beanObject.getClass().getAnnotation(DosolaTx.class);
		if(dosolaTx==null){
			//检查方法上的注解
			//该模块下的所有方法
			Method[] methods = beanObject.getClass().getMethods();
			Method currMethod = null;
			if(!ArrayUtils.isEmpty(methods)){
				for (Method method : methods) {
					String mName = method.getName();
					if(mName.equals(methodName)){
						currMethod = method;
						break;
					}
				}
			}
			if(!currMethod.isAnnotationPresent(DosolaTx.class)){
				return invocation.proceed();
			}
		}
		
		//开始事务处理
		Config config = DbKit.getConfig();
		Connection conn = config.getThreadLocalConnection();
		if (conn != null) {	// Nested transaction support
			try {
				if (conn.getTransactionIsolation() < getTransactionLevel(config))
					conn.setTransactionIsolation(getTransactionLevel(config));
				return invocation.proceed();
			} catch (SQLException e) {
				throw new ActiveRecordException(e);
			}
		}
		
		Boolean autoCommit = null;
		try {
			conn = config.getConnection();
			autoCommit = conn.getAutoCommit();
			config.setThreadLocalConnection(conn);
			conn.setTransactionIsolation(getTransactionLevel(config));	// conn.setTransactionIsolation(transactionLevel);
			conn.setAutoCommit(false);
			object = invocation.proceed();
			conn.commit();
		} catch (NestedTransactionHelpException e) {
			if (conn != null) try {conn.rollback();} catch (Exception e1) {e1.printStackTrace();}
		} catch (Throwable t) {
			if (conn != null) try {conn.rollback();} catch (Exception e1) {e1.printStackTrace();}
			throw new ActiveRecordException(t);
		}
		finally {
			try {
				if (conn != null) {
					if (autoCommit != null)
						conn.setAutoCommit(autoCommit);
					conn.close();
				}
			} catch (Throwable t) {
				t.printStackTrace();	// can not throw exception here, otherwise the more important exception in previous catch block can not be thrown
			}
			finally {
				config.removeThreadLocalConnection();	// prevent memory leak
			}
		}
		return object;
	}
	
	/**
	 * 获取服务类
	 * @param invocation
	 * @return
	 * @throws Exception
	 */
	protected Map<String,Object> parseParam(MethodInvocation invocation) throws Exception {
		//dorado上下文
		DoradoContext doradoContext = null;
		//当前服务名  beanName#methodName
		String serviceName = null;
		//当前action类型
		String action = null;
		for (Object arg : invocation.getArguments()) {
			if(arg instanceof DoradoContext){
				doradoContext = (DoradoContext)arg;
			}
			if (arg instanceof ObjectNode) {
				ObjectNode objectNode = (ObjectNode) arg;
				action = JsonUtils.getString(objectNode, "action");
				serviceName = JsonUtils.getString(objectNode, "service");
				if (action.equals("resolve-data")) {
					serviceName = JsonUtils.getString(objectNode,
                            "dataResolver");
				}
				if (action.equals("load-data")) {
					serviceName = JsonUtils.getString(objectNode,
                            "dataProvider");
				}
			}
		}
		if(StringUtils.isEmpty(serviceName) || serviceName.indexOf("#")==-1){
			throw new RuntimeException("服务请求不能为空或不合法，serviceName："+serviceName);
		}
		String beanName = serviceName.split("#")[0];
		String methodName = serviceName.split("#")[1];
		if(StringUtils.isEmpty(methodName) || StringUtils.isEmpty(beanName)){
			throw new RuntimeException("服务请求不合法，serviceName："+serviceName+",beanName:"+beanName+",methodName:"+methodName);
		}
		ApplicationContext applicationContext = doradoContext.getApplicationContext();
		Object beanObj = applicationContext.getBean(beanName);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("serviceName", serviceName);
		map.put("beanName", beanName);
		map.put("methodName", methodName);
		map.put("beanObj", beanObj);
		return map;
	}
	
	/**
	 * 获取事务级别
	 * @param config
	 * @return
	 */
	protected int getTransactionLevel(Config config) {
		return config.getTransactionLevel();
	}

}
