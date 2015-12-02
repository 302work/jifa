package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

/**
 * 统计工作量的查询结果条目
 * @author Song
 * @version 1.0
 * @since 2015-11-15 21:38
 */
public class StatisticInfoItem implements IPojo{
    private String testUserName;//测试人员帐号
    private String testName;//测试人员姓名
    private Integer status;//状态
    private Integer countByStatus;//审核结果数目

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

    public Integer getCountByStatus() {
        return countByStatus;
    }

    public void setCountByStatus(Integer countByStatus) {
        this.countByStatus = countByStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
