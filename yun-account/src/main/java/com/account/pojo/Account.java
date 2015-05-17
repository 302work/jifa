package com.account.pojo;

import com.jifa.core.pojo.IPojo;

import javax.persistence.*;
import java.util.Date;

/**
 * 记账本
 * @author june
 * 2015年05月17日 12:37
 */
@Entity
@Table(name=Account.TABLENAME)
public class Account implements IPojo{

    public static final String TABLENAME = "yun_account";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=50,nullable = false)
    private String name;//名称

    @Column(length = 1,columnDefinition="tinyint default 0")
    private Boolean isDeleted;//是否删除了

    @Column
    private Date delTime;//删除时间

    @Column(nullable = false)
    private Date crTime;//创建时间

    @Column(nullable = false,length=60)
    private String crUser;//创建人

    @Column(length=60)
    private String delUser;//删除人

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentAccountId", insertable = false, updatable = false)
    private Account parentAccount;//父记账本

    @Column
    private Long parentAccountId;//父记账本id


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
}
