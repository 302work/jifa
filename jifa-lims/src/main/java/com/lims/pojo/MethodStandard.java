package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;
import java.util.Date;

/**
 * 方法标准
 * @author june
 * 2015年09月19日 16:37
 */
@SuppressWarnings("serial")
@Entity
@Table(name=MethodStandard.TABLENAME)
public class MethodStandard implements IPojo {

    public static final String TABLENAME = "lims_method_standard";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=100,nullable = false)
    private String name;//标准名称

    @Column(length=100,nullable = false)
    private String standardNo;//标准号

    @Column(nullable = false)
    private Integer status;//状态，1现行，2为作废

    @Column(nullable = false)
    private Long standardId;//所属产品标准id

    @Column
    private Double price;//基准价格，保留字段暂时无用

    @Column(length=20)
    private String unit;//单位，保留字段暂时无用

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getStandardId() {
        return standardId;
    }

    public void setStandardId(Long standardId) {
        this.standardId = standardId;
    }

    public String getStandardNo() {
        return standardNo;
    }

    public void setStandardNo(String standardNo) {
        this.standardNo = standardNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
