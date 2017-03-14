package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 记录的检测条件
 * @author june
 * 2015年10月25日 20:52
 */
@SuppressWarnings("serial")
@Entity
@Table(name=RecordTestCondition.TABLENAME)
public class RecordTestCondition implements IPojo {

    public static final String TABLENAME = "lims_record_test_condition";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long projectId;//项目id

    @Column(nullable = false)
    private Long methodStandardId;//方法标准id

    @Column(nullable = false)
    private Long recordId;//所属检测记录

    @Column(length = 50,nullable = false)
    private String sampleNo;//样品编号，条形码

    @Column(nullable = false)
    private String name;//名称

    @Column(nullable = false)
    private String value;//值

    @Column
    private String remark;//备注

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getSampleNo() {
        return sampleNo;
    }

    public void setSampleNo(String sampleNo) {
        this.sampleNo = sampleNo;
    }
}
