package com.elective.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;
import java.util.Date;

/**
 * 学期
 * @author june
 * 2015年06月01日 22:47
 */
@SuppressWarnings("serial")
@Entity
@Table(name=Term.TABLENAME)
public class Term implements IPojo {

    public static final String TABLENAME = "e_term";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=50,nullable = false)
    private String name;//名称

    @Column(length=20,nullable = false)
    private String year;//年份

    @Column(length=20,nullable = false)
    private String type;//上课时间，每周、单双周、隔两周

    @Column(nullable = false)
    private Date startTime;//选课开始时间

    @Column(nullable = false)
    private Date endTime;//选课结束时间

    @Column(nullable = false)
    private Date crTime;//创建时间

    @Column(nullable = false,length=60)
    private String crUser;//创建人

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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
