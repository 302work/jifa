package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;
import java.util.Date;

/**
 * 订单、报告
 * @author june
 * 2015年09月20日 22:46
 */
@SuppressWarnings("serial")
@Entity
@Table(name=Order.TABLENAME)
public class Order implements IPojo {

    public static final String TABLENAME = "lims_order";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=50,nullable = false)
    private String orderNo;//订单编号，报告编号

    @Column(length=50)
    private String testType;//检测性质，委托检测

    @Column(nullable = false)
    private Long consumerId;//客户id

    @Column(length=50,nullable = false)
    private String client;//委托人

    @Column(nullable = false)
    private Date deliveryDate;//送样时间

    @Column
    private Date finishDate;//完成时间

    @Column(length=100)
    private String area;//出口国（地区）

    @Column(nullable = false)
    private Integer sampleCount;//样品数量

    @Column(length=1000)
    private String sampleDesc;//样品描述

    @Column(nullable = false)
    private Long standardId;//产品标准id

    @Column(nullable = false)
    private String projectIds;//项目id，多个逗号隔开

    @Column(nullable = false)
    private String methodStandardIds;//检测方法id，多个逗号隔开

    @Column(length=1000)
    private String testResult;//结论

    @Column
    private Date signDate;//签发日期

    @Column
    private Long auditUserId;//批准人

    @Column
    private Integer isDeleted;//逻辑删除标识，1为已删除


    @Column(length=500)
    private String remark;//备注

    @Column(nullable = false)
    private Date crTime;//创建时间

    @Column(nullable = false,length=60)
    private String crUser;//创建人


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Long getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Long auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Long consumerId) {
        this.consumerId = consumerId;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethodStandardIds() {
        return methodStandardIds;
    }

    public void setMethodStandardIds(String methodStandardIds) {
        this.methodStandardIds = methodStandardIds;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(String projectIds) {
        this.projectIds = projectIds;
    }

    public Integer getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(Integer sampleCount) {
        this.sampleCount = sampleCount;
    }

    public String getSampleDesc() {
        return sampleDesc;
    }

    public void setSampleDesc(String sampleDesc) {
        this.sampleDesc = sampleDesc;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getStandardId() {
        return standardId;
    }

    public void setStandardId(Long standardId) {
        this.standardId = standardId;
    }
}
