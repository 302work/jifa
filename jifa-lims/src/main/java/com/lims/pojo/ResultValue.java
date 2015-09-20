package com.lims.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;

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
    private Long resultId;//所属检测记录

    @Column(nullable = false)
    private Long resultColumnId;//所属列

    @Column(nullable = false)
    private String value;//值

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
}
