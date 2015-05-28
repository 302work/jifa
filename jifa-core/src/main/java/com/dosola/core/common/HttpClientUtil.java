/**   
* @Title: HttpClientUtil.java
* @Package com.tts.http.common
* @Description: 
* @author june   
* @date 2013-12-12 下午2:42:57
* @version V1.0   
*/
package com.dosola.core.common;

import net.sf.json.JSONObject;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * @ClassName: HttpClientUtil
 * @Description: http请求工具类
 * @author june
 * @date 2013-12-12 下午2:42:57
 * 
 */
public class HttpClientUtil {
	
	/**
	* @Title: handleRequest
	* @Description: 处理所有请求
	*  @param requestBean
	*  @return    
	* @return String    
	* @throws
	 */
	public static String handleRequest(RequestBean requestBean)throws Exception{
		if(requestBean==null){
			throw new Exception("requestBean不能为空");
		}
		String type = requestBean.getType();
		if(StringUtil.isEmpty(type)){
			throw new Exception("type不能为空");
		}
		if(type.equalsIgnoreCase("get")){
			return get(requestBean);
		}
		if(type.equalsIgnoreCase("post")){
			return post(requestBean);
		}
		return null;
	}
	/**
	* @Title: handleRequest
	* @Description: 处理所有请求
	*  @param requestBean
	*  @return    
	* @return String    
	* @throws
	 */
	public static Map<String,Object> handleRequestReturnMap(RequestBean requestBean)throws Exception{
		if(requestBean==null){
			throw new Exception("requestBean不能为空");
		}
		String type = requestBean.getType();
		if(StringUtil.isEmpty(type)){
			throw new Exception("type不能为空");
		}
		if(type.equalsIgnoreCase("get")){
			return getReturnMap(requestBean);
		}
		if(type.equalsIgnoreCase("post")){
			return postReturnMap(requestBean);
		}
		return null;
	}
	/**
	* @Title: post
	* @Description: 处理post请求
	*  @param url
	*  @return    
	* @return String    
	* @throws
	 */
	public static String post(String url)throws Exception{
		return post(new RequestBean(url,"post"));
	}
	/**
	* @Title: post
	* @Description: 处理post请求
	*  @param requestBean
	*  @return    
	* @return String    
	* @throws
	 */
	public static String post(RequestBean requestBean)throws Exception{
		if(requestBean==null){
			throw new Exception("requestBean不能为空");
		}
		String url = requestBean.getUrl();
		if(StringUtil.isEmpty(url)){
			throw new Exception("URL不能为空");
		}
		PostMethod methodPost = new PostMethod(url);
		Map<String,String> paramMap = requestBean.getParamMap();
		NameValuePair[] nvps = getNameValuePairs(paramMap);
		if(nvps!=null){
			methodPost.setRequestBody(nvps);
		}
		return executeMethod(methodPost,requestBean);
	}
	public static Map<String,Object> postReturnMap(RequestBean requestBean)throws Exception{
		if(requestBean==null){
			throw new Exception("requestBean不能为空");
		}
		String url = requestBean.getUrl();
		if(StringUtil.isEmpty(url)){
			throw new Exception("URL不能为空");
		}
		PostMethod methodPost = new PostMethod(url);
		Map<String,String> paramMap = requestBean.getParamMap();
		NameValuePair[] nvps = getNameValuePairs(paramMap);
		if(nvps!=null){
			methodPost.setRequestBody(nvps);
		}
		return executeMethodReturnMap(methodPost,requestBean);
	}
	/**
	* @Title: get
	* @Description: 处理get请求
	*  @param url
	*  @return    
	* @return String    
	* @throws
	 */
	public static String get(String url)throws Exception{
		return get(new RequestBean(url));
	}
	
	/**
	* @Title: get
	* @Description: 处理get请求
	*  @param requestBean
	*  @return    
	* @return String    
	* @throws
	 */
	public static String get(RequestBean requestBean)throws Exception{
		if(requestBean==null){
			throw new Exception("requestBean不能为空");
		}
		String url = requestBean.getUrl();
		if(StringUtil.isEmpty(url)){
			throw new Exception("URL不能为空");
		}
		GetMethod methodGet = new GetMethod(url);
		Map<String,String> paramMap = requestBean.getParamMap();
		NameValuePair[] nvps = getNameValuePairs(paramMap);
		if(nvps!=null){
			methodGet.setQueryString(nvps);
		}
		return executeMethod(methodGet,requestBean);
	}
	public static Map<String,Object> getReturnMap(RequestBean requestBean)throws Exception{
		if(requestBean==null){
			throw new Exception("requestBean不能为空");
		}
		String url = requestBean.getUrl();
		if(StringUtil.isEmpty(url)){
			throw new Exception("URL不能为空");
		}
		GetMethod methodGet = new GetMethod(url);
		Map<String,String> paramMap = requestBean.getParamMap();
		NameValuePair[] nvps = getNameValuePairs(paramMap);
		if(nvps!=null){
			methodGet.setQueryString(nvps);
		}
		return executeMethodReturnMap(methodGet,requestBean);
	}
	/**
	 * @throws com.trs.core.frame.exceptions.Exception
	* @Title: executeMethod
	* @Description: 执行请求
	*  @param method
	*  @param requestBean
	*  @return    
	* @return String    
	* @throws
	 */
	private static String executeMethod(HttpMethodBase method,RequestBean requestBean)throws Exception{
		String response = null;
		method.setRequestHeader("Content-Type",requestBean.getContentType());  
		method.setRequestHeader("User-Agent", requestBean.getUserAgent());
		int retryCount = requestBean.getRetryCount();
		if(retryCount>0){
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(retryCount, false));
		}
		try {
//			int statusCode = requestBean.getHttpClient().executeMethod(method);
//			if (statusCode != HttpStatus.SC_OK) {
//				throw new Exception("返回状态不是200，"+method.getStatusLine());
//            }
			requestBean.getHttpClient().executeMethod(method);
			response = inputStreamTOString(method.getResponseBodyAsStream());
		} catch (HttpException e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw new Exception(e);
        } catch (IOException e) {
            // 发生网络异常
            e.printStackTrace();
            throw new Exception(e);
        } finally {
        	//释放连接
        	method.releaseConnection();
        }
		return response;
	}
	/**
	* @Title: executeMethod
	* @Description: 执行请求,返回完整信息
	* 				包含服务器响应时间，返回状态，返回值
	*  @param method
	*  @param requestBean
	*  @param returnAll
	*  @return
	*  @throws com.trs.core.frame.exceptions.Exception
	* @return Map<String,Object>    
	* @throws
	 */
	private static Map<String,Object> executeMethodReturnMap(HttpMethodBase method,RequestBean requestBean)throws Exception{
		String response = null;
		method.setRequestHeader("Content-Type",requestBean.getContentType());  
		method.setRequestHeader("User-Agent", requestBean.getUserAgent());
		int retryCount = requestBean.getRetryCount();
		if(retryCount>0){
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(retryCount, false));
		}
		int statusCode = 0 ;
		long time = 0;
		String responseCharSet = "";
		try {
			long startTime = System.currentTimeMillis();
			statusCode = requestBean.getHttpClient().executeMethod(method);
			long endTime = System.currentTimeMillis();
			time = endTime-startTime;
			response = inputStreamTOString(method.getResponseBodyAsStream());
			responseCharSet = method.getResponseCharSet();
		} catch (HttpException e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
            throw new Exception(e);
        } catch (IOException e) {
            // 发生网络异常
            e.printStackTrace();
            throw new Exception(e);
        } finally {
        	//释放连接
        	method.releaseConnection();
        }
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("statusCode", statusCode);
		map.put("responeTime", time);
		map.put("responseStr", response);
		map.put("responseCharSet",responseCharSet);
		return map;
	}
	/**
	* @Title: getNameValuePairs
	* @Description: 封装参数
	*  @param paramMap
	*  @return    
	* @return NameValuePair[]    
	* @throws
	*/
	public static NameValuePair[] getNameValuePairs(
			Map<String, String> paramMap) {
		if(paramMap!=null && !paramMap.isEmpty()){
			Iterator<String> iterator = paramMap.keySet().iterator();
			List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
			while(iterator.hasNext()){
				String key = iterator.next().toString();
				if(!StringUtil.isEmpty(key)){
					String value = paramMap.get(key).toString();
					nvpList.add( new NameValuePair(key,value));
				}
			}
			if(nvpList.size()>0){
				return nvpList.toArray(new NameValuePair[]{});
			}
		}
		return null;
	}
	/**  
    * 将InputStream转换成String  
    * @param methodPost PostMethod  
    * @return String  
    * @throws java.io.IOException
    * @throws Exception  
    *   
    */  
   public static String inputStreamTOString(InputStream in) throws IOException{  
       if(in==null){
       	return "";
       }
       ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
       byte[] data = new byte[4096];  
       int count = -1;  
       while((count = in.read(data,0,4096)) != -1)  
           outStream.write(data, 0, count);  
         
       data = null;  
       return StringUtil.toUtf8(new String(outStream.toByteArray(), "ISO-8859-1"));
   }  
   /**
	 * 发送HTTP请求
	 * 
	 * @param url
	 *            接口地址
	 * @param paramMap
	 *            请求参数
	 * @return json对象，包含请求返回内容
	 */
	@SuppressWarnings("rawtypes")
	public static JSONObject sendRequest(String url,
			Map<String, Object> paramMap) {
		JSONObject objson = new JSONObject();
		try {
			PostMethod methodPost = new PostMethod(url);

			Iterator it = paramMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				methodPost.setParameter(key.toString(), value.toString());
			}
			HttpClient httpClient = new HttpClient();
			httpClient.executeMethod(methodPost);
			String responsePost = new String(methodPost.getResponseBody(),
					"utf-8");
			objson = JSONObject.fromObject(responsePost);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objson;
	}
	public static void main(String[] args) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sourceId", 53);
		paramMap.put("type", 1);
		paramMap.put("channelId", 1); 
		JSONObject objson = sendRequest(
				"http://localhost:8080/HaierBBS/haier/appcomment/commentCount.do?",
				paramMap);
		System.out.println(objson);
		String uid = objson.get("totalCount").toString();
		System.out.println(uid);

	}
}
