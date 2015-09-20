package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;

/**
 * 项目和方法标准关联表
 * @author june
 * 2015年09月20日 23:48
 */
@SuppressWarnings("serial")
@Entity
@Table(name=ProjectMethodStandard.TABLENAME)
public class ProjectMethodStandard implements IPojo {

    public static final String TABLENAME = "lims_project_method_standard";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long projectId;//项目id

    @Column(nullable = false)
    private Long methodStandardId;//方法标准id

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMethodStandardId() {
        return methodStandardId;
    }

    public void setMethodStandardId(Long methodStandardId) {
        this.methodStandardId = methodStandardId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
