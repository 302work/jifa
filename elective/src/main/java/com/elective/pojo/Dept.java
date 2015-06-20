package com.elective.pojo;

import com.bstek.bdf2.core.business.IDept;
import com.bstek.bdf2.core.business.IUser;
import com.dosola.core.dao.interfaces.IPojo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

/**
 * 部门
 * @author june
 * 2015年06月02日 14:05
 */
@SuppressWarnings("serial")
@Entity
@Table(name=Dept.TABLENAME)
public class Dept implements IDept,IPojo {

    public static final String TABLENAME = "e_dept";

    @Id
    @GeneratedValue(generator="idGenerator")
    @GenericGenerator(name="idGenerator", strategy="uuid")
    private String id;

    @Column(length=60)
    private String name;//部门名称

    @Column
    private String remark;//备注

    @Column(nullable = false)
    private Date crTime;//创建时间

    @Column(nullable = false, length = 60)
    private String crUser;//创建人

    @Column(length = 60,nullable = false)
    private String companyId;

    @Column(nullable = false)
    private Integer type;//类型，1为普通，2为已毕业

    @Column
    private Integer sortFlag;//排序标志

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parentId", insertable = false, updatable = false)
//    private Dept parent;//父部门

    @Column
    private String parentId;//父部门id
    
    @Column(length = 1,columnDefinition="tinyint default 0",nullable = false)
    private Boolean isDeleted;//是否删除了

    
    @Transient
	private IDept parent;
    
    @Transient
    private List<IUser> users;

    @Transient
    private Boolean hasChild;//是否有子部门
    
    
    public Dept() {
    }

    public Dept(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    @Override
    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Integer getSortFlag() {
        return sortFlag;
    }

    public void setSortFlag(Integer sortFlag) {
        this.sortFlag = sortFlag;
    }

    @Override
    public IDept getParent() {
        return parent;
    }

    public void setParent(IDept parent) {
        this.parent = parent;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public List<IUser> getUsers() {
        return users;
    }

    public void setUsers(List<IUser> users) {
        this.users = users;
    }  

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean getHasChild() {
		return hasChild;
	}

	public void setHasChild(Boolean hasChild) {
		this.hasChild = hasChild;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
    
}
