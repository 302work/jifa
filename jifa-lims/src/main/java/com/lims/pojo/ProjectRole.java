package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;

/**
 * 项目的角色
 * @author june
 * 2015年11月10日 13:51
 */
@SuppressWarnings("serial")
@Entity
@Table(name=ProjectRole.TABLENAME)
public class ProjectRole implements IPojo {

    public static final String TABLENAME = "lims_project_role";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long projectId;//项目id

    @Column(nullable = false)
    private String roleId;//角色id

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
