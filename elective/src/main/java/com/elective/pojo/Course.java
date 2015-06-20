package com.elective.pojo;

import com.dosola.core.dao.interfaces.IPojo;

import javax.persistence.*;

import java.util.Date;

/**
 * 课程
 * @author june
 * 2015年06月01日 23:04
 */
@SuppressWarnings("serial")
@Entity
@Table(name=Course.TABLENAME)
public class Course implements IPojo {

    public static final String TABLENAME = "e_course";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=50,nullable = false)
    private String name;//课程名称

    @Column(length=2000)
    private String intro;//课程介绍

    @Column(nullable = false)
    private Long teacherId;//开课老师ID

    @ManyToOne
	@JoinColumn(name = "teacherId", updatable = false, insertable = false)
	private User teacher;//开课老师
	
    @Column(nullable = false)
    private Long termId;//所属学期ID
    
    @ManyToOne
	@JoinColumn(name = "termId", updatable = false, insertable = false)
	private Term term;//所属学期

    @Column(nullable = false)
    private Long classroomId;//上课教室ID
    
    @ManyToOne
   	@JoinColumn(name = "classroomId", updatable = false, insertable = false)
   	private Classroom classroom;//上课教室

    @Column(nullable = false)
    private Integer num;//每班限报人数
    
    @Column(nullable = false)
    private Integer total;//总人数限制

    @Column(length=100)
    private String deptIds;//年级限制
    
    @Column(length = 1,columnDefinition="tinyint default 1")
    private Integer isEnable;//是否可用，1为可用，2为不可用

    @Column(length = 1,columnDefinition="tinyint default 0")
    private Integer isAudit;//是否已审核，1为已审核，2为未审核，3为审核未通过

    @Column(length=4000)
    private String homework;//家庭作业

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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String deptIds) {
        this.deptIds = deptIds;
    }

    public String getHomework() {
        return homework;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public User getTeacher() {
		return teacher;
	}

	public void setTeacher(User teacher) {
		this.teacher = teacher;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public Classroom getClassroom() {
		return classroom;
	}

	public void setClassroom(Classroom classroom) {
		this.classroom = classroom;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Integer isEnable) {
		this.isEnable = isEnable;
	}

	public Integer getIsAudit() {
		return isAudit;
	}

	public void setIsAudit(Integer isAudit) {
		this.isAudit = isAudit;
	}
	
}
