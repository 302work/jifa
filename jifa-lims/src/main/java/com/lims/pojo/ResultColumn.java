package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;
import java.util.Date;

/**
 * 项目的记录项，即检测结果的列
 * 该数据不能修改，如果要修改：先将旧的逻辑删除，再新增一个
 * @author june
 * 2015年09月20日 22:51
 */
@SuppressWarnings("serial")
@Entity
@Table(name=ResultColumn.TABLENAME)
public class ResultColumn implements IPojo {

    public static final String TABLENAME = "lims_result_column";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long projectId;//项目id

    @Column(length = 100,nullable = false)
    private String name;//列名

    @Column(length = 100)
    private String enName;//列英文名

    @Column
    private Integer isDeleted;//逻辑删除标识，1为已删除。

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

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
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
}
