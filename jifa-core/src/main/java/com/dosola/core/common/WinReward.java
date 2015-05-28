/**
 * 
 */
package com.dosola.core.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 获取各种概率
 * @author june
 * 2014年12月8日 下午5:02:07
 * 
 */
public class WinReward {
	/**
	 * @Description: 获取是否中奖以及中几等奖
	 * @param winRate  中奖概率 中奖概率80% 传递参数为 80 ，100%中奖传递参数100
	 * @param rewardNumArr  按照奖项输入中奖数量数组 new int[]{10,30,100}; 表示一等奖10个，二等奖30个，三等奖100个,不传入该参数时概率取决于第一个百分比    
	 * @return int  返回-1 表示没有中奖  返回其它数值如0,1,2 返回numArr数组的索引表示中奖相应奖项 
	 * @throws
	 */
	public static int isGetReward(double winRate,int... rewardNumArr){
		int isWin=-1;
		if(rewardNumArr==null || !isWinRewardRate(winRate)){
			return -1;
		}
		try {
			
			//奖品种类数
			int rewardSize=rewardNumArr.length;
			//没有奖品返回没有中奖
			if(rewardSize==0){
				return -1;
			}
			//奖品总数
			int rewardNum = 0;
			for(int i=0;i<rewardSize;i++){
				rewardNum += rewardNumArr[i];
			}
			
			//所有奖品中随机获取一个奖品的编号
			int rewardRandNum = (int)(Math.random()*rewardNum)+1;
			
			//根据奖品数目构建数据
			List<List<Integer>> rewardList = new ArrayList<List<Integer>>();
			List<Integer> rewardSegmentNum = null;
			
			int currBeginNum = 0;
			for(int i=0;i<rewardSize;i++){
				rewardSegmentNum=new ArrayList<Integer>();
				//设置各个列表中分段数据,
				for(int j=(currBeginNum+1);j<(currBeginNum+1+rewardNumArr[i]);j++){
					rewardSegmentNum.add(j);
				}
				rewardList.add(rewardSegmentNum);
				//将初始num加上上一个奖品的数量
				currBeginNum+=rewardNumArr[i];
			}
			//查询是第几个奖品
			for(int i=0;i<rewardList.size();i++){
				List<Integer> rewardListEach=rewardList.get(i);
				if(rewardListEach.indexOf(rewardRandNum)>-1){
					isWin=i;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return isWin;
	}
	/**
	* @Description: 百分比是否中奖  
	* @param winRate 比如55.5 表示中奖概率为55.5%，支持两位小数
	* @return
	 */
	public static boolean isWinRewardRate(double winRate){
		if(winRate>=100){
			return true;
		}
		//放大100倍
		winRate = StringUtil.round(StringUtil.mul(winRate, 100), 0);
		
		boolean isWin = false;
		//获得随机数
		int randNum = new Random().nextInt(100*100);
		//获奖概率为0，直接返回没中奖
		if(winRate<=0){
			return isWin;
		}
		//获取的随机数大于等于获奖概率表示没有中奖
		if(randNum>winRate){
			return isWin;
		}
		return true;
	}
	public static void main(String[] args) {
		
		for (int i = 0; i < 100; i++) {
			System.out.println(WinReward.isGetReward(20.5, new int[]{0, 0, 0, 0}));
		}
	}
}
