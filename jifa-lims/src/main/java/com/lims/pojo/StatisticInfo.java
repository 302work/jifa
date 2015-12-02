package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

/**
 * 工作量统计结果
 *
 * @author Song
 * @version 1.0
 * @since 2015-11-15 21:52
 */
public class StatisticInfo implements IPojo {

    private String testUserName;//测试人员帐号
    private String testName;//测试人员姓名
    private Integer testCount;//应测试总数
    private Integer actualTestCount;//实际检测记录总数
    private Integer passedCount;//审核通过数量
    private Integer notPassedCount;//审核未通过数量

    public String getTestUserName() {
        return testUserName;
    }

    public void setTestUserName(String testUserName) {
        this.testUserName = testUserName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Integer getTestCount() {
        return testCount;
    }

    public void setTestCount(Integer testCount) {
        this.testCount = testCount;
    }

    public Integer getActualTestCount() {
        return (passedCount == null ? 0 : passedCount)+(notPassedCount == null ? 0 : notPassedCount);
    }

    public void setActualTestCount(Integer actualTestCount) {
        this.actualTestCount = actualTestCount;
    }

    public Integer getPassedCount() {
        return passedCount;
    }

    public void setPassedCount(Integer passedCount) {
        this.passedCount = passedCount;
    }

    public Integer getNotPassedCount() {
        return notPassedCount;
    }

    public void setNotPassedCount(Integer notPassedCount) {
        this.notPassedCount = notPassedCount;
    }
}
