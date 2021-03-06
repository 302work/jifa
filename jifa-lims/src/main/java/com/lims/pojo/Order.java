package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 订单、报告
 *
 * @author june
 *         2015年09月20日 22:46
 */
@SuppressWarnings("serial")
@Entity
@Table(name = Order.TABLENAME)
public class Order implements IPojo {

    public static final String TABLENAME = "lims_order";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 50, nullable = false)
    private String orderNo;//订单编号，报告编号

    @Column(length = 50)
    private String testType;//检测性质，委托检测

    @Column(nullable = false)
    private Long consumerId;//客户id

    @Column(length = 50, nullable = false)
    private String client;//委托人，申请人

    @Column(length = 30)
    private String clientPhone;//申请人电话

    @Column(length = 30)
    private String clientFax;//申请人传真

    @Column(length = 30)
    private String clientZip;//申请人邮编

    @Column
    private String payer;//付款人

    @Column
    private String payerPhone;//付款人电话

    @Column
    private String payerFax;//付款人传真

    @Column
    private String payerZip;//付款人邮编

    @Column
    private Date deliveryDate;//送样时间

    @Column
    private Date finishDate;//完成时间

    @Column(length = 100)
    private String area;//出口国（地区）

    @Column(nullable = false, length = 40)
    private String sampleName;//样品名称

    @Column(nullable = false)
    private Integer sampleCount;//样品数量

    @Column(length = 1000)
    private String sampleDesc;//样品描述

    @Column(length = 30)
    private String fibreComponent;//纤维成分

    @Column
    private Double weight;//克重

    @Column(nullable = false)
    private Long standardId;//产品标准id

    @Column(nullable = false)
    private String projectMethodStandardIds;//项目的方法标准，多个逗号隔开

    @Column
    private Integer timeLimit;//测试时间，要求多少天完成检测。5-常规：5个工作日，3-加急：3个工作日，2-特快：2个工作日 1-当天

    @Column
    private Integer sampleHandleType;//剩余样品处理方式 ，1-“自取”、2-“不退”、3-“寄回”

    @Column(nullable = false)
    private Integer reportSendWay;//报告发送方式，1-自取 2-邮寄

    @Column
    private String reportLanguage;//报告语言，“ch”-中文，“en”-英语，多种语言逗号隔开

    @Column(length = 1000)
    private String testResult;//结论

    @Column
    private Date signDate;//签发日期

    @Column(length=50)
    private String auditUserName;//批准人

    @Column
    private Integer isDeleted;//逻辑删除标识，1为已删除

    @Column
    private String status;//状态

    @Column(length = 500)
    private String remark;//备注

    @Column(nullable = false)
    private Date crTime;//创建时间

    @Column(nullable = false, length = 60)
    private String crUser;//创建人

    @Column
    private String consumerOrderNo;//客户订单编号

    @Column
    private String guoHao;//锅号

    @Column
    private String oldSamplePic;//原样照片


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientFax() {
        return clientFax;
    }

    public void setClientFax(String clientFax) {
        this.clientFax = clientFax;
    }

    public String getClientZip() {
        return clientZip;
    }

    public void setClientZip(String clientZip) {
        this.clientZip = clientZip;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getPayerPhone() {
        return payerPhone;
    }

    public void setPayerPhone(String payerPhone) {
        this.payerPhone = payerPhone;
    }

    public String getPayerFax() {
        return payerFax;
    }

    public void setPayerFax(String payerFax) {
        this.payerFax = payerFax;
    }

    public String getPayerZip() {
        return payerZip;
    }

    public void setPayerZip(String payerZip) {
        this.payerZip = payerZip;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getFibreComponent() {
        return fibreComponent;
    }

    public void setFibreComponent(String fibreComponent) {
        this.fibreComponent = fibreComponent;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getReportLanguage() {
        return reportLanguage;
    }

    public void setReportLanguage(String reportLanguage) {
        this.reportLanguage = reportLanguage;
    }

    public Integer getReportSendWay() {
        return reportSendWay;
    }

    public void setReportSendWay(Integer reportSendWay) {
        this.reportSendWay = reportSendWay;
    }

    public Integer getSampleHandleType() {
        return sampleHandleType;
    }

    public void setSampleHandleType(Integer sampleHandleType) {
        this.sampleHandleType = sampleHandleType;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getProjectMethodStandardIds() {
        return projectMethodStandardIds;
    }

    public void setProjectMethodStandardIds(String projectMethodStandardIds) {
        this.projectMethodStandardIds = projectMethodStandardIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConsumerOrderNo() {
        return consumerOrderNo;
    }

    public void setConsumerOrderNo(String consumerOrderNo) {
        this.consumerOrderNo = consumerOrderNo;
    }

    public String getGuoHao() {
        return guoHao;
    }

    public void setGuoHao(String guoHao) {
        this.guoHao = guoHao;
    }

    public String getOldSamplePic() {
        return oldSamplePic;
    }

    public void setOldSamplePic(String oldSamplePic) {
        this.oldSamplePic = oldSamplePic;
    }
}

