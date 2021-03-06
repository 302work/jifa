package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;

/**
 * 用户和部门关联表，用户和部门是多对多关系
 * @author june
 * 2015年06月02日 14:54
 */
@SuppressWarnings("serial")
@Entity
@Table(name= DeptUser.TABLENAME)
public class DeptUser implements IPojo {

    public static final String TABLENAME = "lims_user_dept";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private Long userId;

    @Column(length=60,nullable=false)
    private String deptId;
    
    @ManyToOne
   	@JoinColumn(name = "userId", updatable = false, insertable = false)
   	private User user;
    
    @ManyToOne
   	@JoinColumn(name = "deptId", updatable = false, insertable = false)
   	private Dept dept;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Dept getDept() {
		return dept;
	}

	public void setDept(Dept dept) {
		this.dept = dept;
	}
    
}
