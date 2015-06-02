package com.elective.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;
import java.util.Date;

/**
 * 教室
 * @author june
 * 2015年06月01日 22:55
 */
@SuppressWarnings("serial")
@Entity
@Table(name=Classroom.TABLENAME)
public class Classroom implements IPojo {

    public static final String TABLENAME = "e_classroom";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=50,nullable = false)
    private String name;//教室名称

    @Column
    private String address;//教室地址

    @Column(length = 1,columnDefinition="tinyint default 1")
    private Boolean isEnable = true;//是否可用，默认可用

    @Column
    private String remark;//备注

    @Column(nullable = false)
    private Date crTime;//创建时间

    @Column(nullable = false,length=60)
    private String crUser;//创建人

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
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
}
