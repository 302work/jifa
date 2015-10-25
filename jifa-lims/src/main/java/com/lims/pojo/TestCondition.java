package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;

/**
 * 检测条件默认值
 * @author june
 * 2015年10月25日 20:34
 */
@Entity
@Table(name = TestCondition.TABLENAME)
public class TestCondition implements IPojo{

    public static final String TABLENAME = "lims_test_condition";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;//名称

    @Column(nullable = false)
    private String value;//默认值

    @Column(nullable = false)
    private Long projectId;//项目id

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
}
