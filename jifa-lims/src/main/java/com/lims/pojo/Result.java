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
 * 检测结果
 * @author june
 * 2015年09月20日 22:48
 */
@SuppressWarnings("serial")
@Entity
@Table(name=Result.TABLENAME)
public class Result implements IPojo {

    public static final String TABLENAME = "lims_result";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long orderId;//所属订单id

    @Column(nullable = false)
    private Long projectId;//项目id

    @Column(nullable = false)
    private Long methodStandardId;//方法标准id

    @Column(nullable = false)
    private Long recordId;//所属检测数据

    @Column(name="`index`",nullable = false)
    private Integer index;//第几次检测

    @Column(name="`status`",nullable = false)
    private Integer status;//状态，1正常，2为作废

    @Column
    private Integer isDeleted;//逻辑删除标识，1为已删除

    @Column(length=500)
    private String remark;//备注

    @Column(nullable = false)
    private Date crTime;//创建时间

    @Column(nullable = false,length=60)
    private String crUser;//创建人

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getMethodStandardId() {
        return methodStandardId;
    }

    public void setMethodStandardId(Long methodStandardId) {
        this.methodStandardId = methodStandardId;
    }
}
