package com.elective.pojo;

import com.bstek.bdf2.core.business.AbstractUser;
import com.bstek.bdf2.core.business.IDept;
import com.bstek.bdf2.core.business.IPosition;
import com.bstek.bdf2.core.model.Group;
import com.bstek.bdf2.core.model.Role;
import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户
 * @author june
 * 2015年06月02日 09:52
 */
@SuppressWarnings("serial")
@Entity
@Table(name=User.TABLENAME)
public class User extends AbstractUser implements IPojo {

    public static final String TABLENAME = "e_user";
    
    //默认密码
    public static final String defaultPassword = "654321";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=60,unique = true)
    private String username;

    @Column(length=70,nullable=false)
    private String password;

    @Column(length=10,nullable=false)
    private String salt;

    @Column(length=60,nullable=false)
    private String cname;

    @Column(nullable=false)
    private boolean enabled=true;

    @Column(length=20)
    private String mobile;

    @Column(length=60)
    private String email;

    @Column(length=60)
    private String ename;

    @Column(nullable=false)
    private Integer type;//类型，1为学生，2为老师，3为管理员

    @Column(nullable = false)
    private Date crTime;//创建时间

    @Column(nullable = false, length = 60)
    private String crUser;//创建人

    @Column(length=60,nullable=false)
    private String companyId;

    @Column
    private String remark;//备注

    @Transient
    private List<IPosition> positions;

    @Transient
    private List<IDept> depts;

    @Transient
    private List<Group> groups;

    @Transient
    private List<Role> roles;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getCname() {
        return cname;
    }

    @Override
    public boolean isAdministrator() {
        return false;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public List<IPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<IPosition> positions) {
        this.positions = positions;
    }

    @Override
    public List<IDept> getDepts() {
        return depts;
    }

    public void setDepts(List<IDept> depts) {
        this.depts = depts;
    }

    @Override
    public List<Group> getGroups() {
        return groups;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String getCompanyId() {
        return companyId;
    }

    @Override
    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
