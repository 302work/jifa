/**   
* @Title: RequestBean.java
* @Package com.tts.http.bean
* @Description: 
* @author june   
* @date 2013-12-12 上午9:41:06
* @version V1.0   
*/
package com.dosola.core.common;

import org.apache.commons.httpclient.HttpClient;

import java.util.Map;

/**
 * @ClassName: RequestBean
 * @Description: 请求实体，封装一些参数
 * @author june
 * @date 2013-12-12 上午9:41:06
 * 
 */
public class RequestBean {
	
	/**
	 * 请求地址
	 */
	private String url = "";
	/**
	 * 参数集
	 */
	private Map<String,String> paramMap = null;
	/**
	 * 请求类型,默认GET
	 */
	private String type = "get";
	/**
	 * http连接，默认每个请求一个使用不同的连接
	 * 如果需要在同一个会话里完成操作，需要用同一个httpClient
	 */
	private HttpClient httpClient = new HttpClient(); 
	/**
	 * 网络文件的类型和网页的编码
	 */
	private String contentType = "application/x-www-form-urlencoded;charset=utf-8";
	/**
	 * 用户代理，指浏览器,它的信息包括硬件平台、系统软件、应用软件和用户个人偏好.
	 */
	private String userAgent = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Shuame)";
	/**
	 * 标记自己从哪里来的
	 */
	private String referer = "http://www.baidu.com";
	/**
	 * 读数据超时时间，单位毫秒，默认30秒
	 */
	private long so_timeout = 30000;
	/**
	 * 连接超时时间，单位毫秒，默认60秒
	 */
	private long con_timeout = 60000;
	
	/**
	 * 设置请求重试处理，默认为0不重试
	 */
	private int retryCount = 0;
	
	
	public RequestBean() {
		super();
	}
	
	public RequestBean(String url) {
		super();
		this.url = url;
	}
	
	
	public RequestBean(String url, Map<String, String> paramMap) {
		super();
		this.url = url;
		this.paramMap = paramMap;
	}


	public RequestBean(String url, String type) {
		super();
		this.url = url;
		this.type = type;
	}


	public RequestBean(String url, HttpClient httpClient) {
		super();
		this.url = url;
		this.httpClient = httpClient;
	}


	public RequestBean(String url, Map<String, String> paramMap, String type) {
		super();
		this.url = url;
		this.paramMap = paramMap;
		this.type = type;
	}


	public RequestBean(String url, String type, HttpClient httpClient) {
		super();
		this.url = url;
		this.type = type;
		this.httpClient = httpClient;
	}


	public RequestBean(String url, Map<String, String> paramMap,
			HttpClient httpClient) {
		super();
		this.url = url;
		this.paramMap = paramMap;
		this.httpClient = httpClient;
	}


	public RequestBean(String url, Map<String, String> paramMap, String type,
			HttpClient httpClient) {
		super();
		this.url = url;
		this.paramMap = paramMap;
		this.type = type;
		this.httpClient = httpClient;
	}

	public RequestBean(String url, Map<String, String> paramMap, String type,
			HttpClient httpClient, String contentType, String userAgent,
			String referer, long so_timeout, long con_timeout, int retryCount) {
		super();
		this.url = url;
		this.paramMap = paramMap;
		this.type = type;
		this.httpClient = httpClient;
		this.contentType = contentType;
		this.userAgent = userAgent;
		this.referer = referer;
		this.so_timeout = so_timeout;
		this.con_timeout = con_timeout;
		this.retryCount = retryCount;
	}


	public Map<String, String> getParamMap() {
		return paramMap;
	}
	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public HttpClient getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getReferer() {
		return referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
	public long getSo_timeout() {
		return so_timeout;
	}
	public void setSo_timeout(long so_timeout) {
		this.so_timeout = so_timeout;
	}
	public long getCon_timeout() {
		return con_timeout;
	}
	public void setCon_timeout(long con_timeout) {
		this.con_timeout = con_timeout;
	}


	public int getRetryCount() {
		return retryCount;
	}


	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	
}
