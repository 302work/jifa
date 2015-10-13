package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;
import java.util.Date;

/**
 * 检测结果的列和值
 * @author june
 * 2015年09月20日 22:49
 */
@SuppressWarnings("serial")
@Entity
@Table(name=ResultValue.TABLENAME)
public class ResultValue implements IPojo {

    public static final String TABLENAME = "lims_result_value";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long resultId;//所属检测结果

    @Column(nullable = false)
    private Long resultColumnId;//所属列

    @Column(nullable = false)
    private String value;//值

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

    public Long getResultColumnId() {
        return resultColumnId;
    }

    public void setResultColumnId(Long resultColumnId) {
        this.resultColumnId = resultColumnId;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
