/**
 * 
 */
package com.dosola.core.common;

import java.io.Serializable;
import java.util.Map;

/**
 * 返回前台封装对象
 * @author june
 * 2014年12月7日 下午10:22:29
 * 
 */
@SuppressWarnings("serial")
public class ReturnContext implements Serializable{
	/**
	 * 返回结果集
	 */
	private Map< String, Object >  data;
	
	/**
	 * 返回消息
	 */
	private String resultMsg;
	
	/**
	 * 执行结果是否成功, 默认false
	 */
	private Boolean isSuccess;
	
	/**
	 * 初始化返回对象，默认执行失败
	 * @return
	 */
	public static ReturnContext init() {
		ReturnContext returnContext = new ReturnContext();
		returnContext.setIsSuccess(Boolean.FALSE);
		returnContext.setResultMsg("操作失败");
		return returnContext;
	}
	/**
	 * 构建成功对象
	 * @param returnContext
	 * @return
	 */
	public static ReturnContext build(ReturnContext returnContext) {
		if(returnContext==null){
			returnContext = init();
		}
		returnContext.setIsSuccess(Boolean.TRUE);
		returnContext.setResultMsg("操作成功");
		return returnContext;
	}
	

	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	
	
	
}
