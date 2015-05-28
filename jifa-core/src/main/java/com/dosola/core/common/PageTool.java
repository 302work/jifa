/**
 * 
 */
package com.dosola.core.common;

/**
 * 
 * @author june
 * 2015年1月6日 上午10:37:41
 * 
 */
public class PageTool {

	/**
	 * 通过总记录数 分页大小 计算页数
	 * @param count
	 * @param pageSize
	 * @return
	 */
	public static int getPageCount(int count,int pageSize){
		if(count % pageSize ==0){
			return count / pageSize;
		}else{
			return ( count / pageSize ) + 1 ;
		}
		
	}
	
	public static void main(String args[]){
		System.out.println(PageTool.getPageCount(0, 5));
		 
	}
	
	
}
