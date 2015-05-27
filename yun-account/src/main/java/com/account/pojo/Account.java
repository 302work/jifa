package com.account.pojo;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jifa.core.pojo.IPojo;

/**
 * 记账本
 * @author june
 * 2015年05月17日 12:37
 */
@SuppressWarnings("serial")
@Entity
@Table(name=Account.TABLENAME)
public class Account implements IPojo{

    public static final String TABLENAME = "yun_account";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=50,nullable = false)
    private String name;//名称

    @Column(length = 1,columnDefinition="tinyint default 0",nullable = false)
    private Boolean isDeleted;//是否删除了

    @Column
    private Date delTime;//删除时间

    @Column(nullable = false)
    private Date crTime;//创建时间

    @Column(nullable = false,length=60)
    private String crUser;//创建人

    @Column(length=60)
    private String delUser;//删除人
    
    @Column
    private String remark;//备注
    
    @Column
    private Integer sortFlag;//排序标志

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentAccountId", insertable = false, updatable = false)
    private Account parentAccount;//父记账本

    @Column
    private Long parentAccountId;//父记账本id
    
//    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.MERGE)
//	@JoinColumn(name = "parentAccountId", insertable = false, updatable = false)
//	@OrderBy(value = "sortFlag")
    @Transient
//    private List<Account> child;//子分类
//    
//    @Transient
//    private List<Record> records;//记录明细
    
    


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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getDelTime() {
        return delTime;
    }

    public void setDelTime(Date delTime) {
        this.delTime = delTime;
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

    public String getDelUser() {
        return delUser;
    }

    public void setDelUser(String delUser) {
        this.delUser = delUser;
    }

    public Account getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(Account parentAccount) {
        this.parentAccount = parentAccount;
    }

    public Long getParentAccountId() {
        return parentAccountId;
    }

    public void setParentAccountId(Long parentAccountId) {
        this.parentAccountId = parentAccountId;
    }

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getSortFlag() {
		return sortFlag;
	}

	public void setSortFlag(Integer sortFlag) {
		this.sortFlag = sortFlag;
	}
    
    
}
