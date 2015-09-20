package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;
import java.util.Date;

/**
 * 检测数据
 * @author june
 * 2015年09月20日 22:47
 */
@SuppressWarnings("serial")
@Entity
@Table(name=Record.TABLENAME)
public class Record implements IPojo {

    public static final String TABLENAME = "lims_record";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(length = 50,nullable = false)
    private String sampleNo;//样品编号，条形码

    @Column(length=1000)
    private String testCondition;//测试条件

    @Column(length=200)
    private String deviceIds;//仪器设备，多个逗号隔开

    @Column(length=200)
    private String samplePic;//原样品图片

    @Column(length=200)
    private String testSamplePic;//测试样品图片

    @Column
    private Long testUserId;//检验人

    @Column
    private Long auditUserId;//审核人

    @Column
    private Date testDate;//检验日期

    @Column(nullable = false)
    private String resultColumnIds;//该项目的所有列，逗号隔开。在新建record时从resultColumn中读取

    @Column
    private Integer isDeleted;//逻辑删除标识，1为已删除

    @Column(length=500)
    private String remark;//备注

    @Column(nullable = false)
    private Date crTime;//创建时间

    @Column(nullable = false,length=60)
    private String crUser;//创建人

    public Long getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Long auditUserId) {
        this.auditUserId = auditUserId;
    }

    public Date getCrTime() {
        return crTime;
    }

    public void setCrTime(Date crTime) {
        this.crTime = crTime;
    }

    public String getCrUser() {
        return crUser;
    }

    public void setCrUser(String crUser) {
        this.crUser = crUser;
    }

    public String getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(String deviceIds) {
        this.deviceIds = deviceIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSampleNo() {
        return sampleNo;
    }

    public void setSampleNo(String sampleNo) {
        this.sampleNo = sampleNo;
    }

    public String getTestCondition() {
        return testCondition;
    }

    public void setTestCondition(String testCondition) {
        this.testCondition = testCondition;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public Long getTestUserId() {
        return testUserId;
    }

    public void setTestUserId(Long testUserId) {
        this.testUserId = testUserId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getSamplePic() {
        return samplePic;
    }

    public void setSamplePic(String samplePic) {
        this.samplePic = samplePic;
    }

    public String getTestSamplePic() {
        return testSamplePic;
    }

    public void setTestSamplePic(String testSamplePic) {
        this.testSamplePic = testSamplePic;
    }

    public String getResultColumnIds() {
        return resultColumnIds;
    }

    public void setResultColumnIds(String resultColumnIds) {
        this.resultColumnIds = resultColumnIds;
    }
}
