package com.elective.view.record;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.business.IDept;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.core.service.IDeptService;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.common.DateUtil;
import com.dosola.core.common.StringUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.elective.pojo.Classroom;
import com.elective.pojo.Course;
import com.elective.pojo.Dept;
import com.elective.pojo.DeptUser;
import com.elective.pojo.Record;
import com.elective.pojo.Term;
import com.elective.pojo.User;

/**
 * 单表维护服务类
 * 实现对com.elective.pojo.Record对象信息的保存操作
 */
@Component("recordPR")
public class RecordMaintain {
	@Resource
    private IMasterDao dao;

	@Resource(name=IDeptService.BEAN_ID)
	private IDeptService deptService;
	
	
	
	@Expose
	public String saveRecord(Map<String,Object> param) throws Exception {
		User user = (User)ContextHolder.getLoginUser();
		int userType = user.getType();//类型，1为学生，2为老师，3为管理员
		Long courseId = Long.valueOf(param.get("courseId").toString());
		Long studentId = Long.valueOf(param.get("studentId").toString());
		
		Course course = dao.getObjectById(Course.class, courseId);
		int isEnable = course.getIsEnable();//是否可用，1为可用，2为不可用
		int isAudit = course.getIsAudit();//是否已审核，1为已审核，2为未审核，3为审核未通过
		
		if(userType==1 && isEnable==2 || isAudit!=1){
			return "课程不可用，请联系管理员";
		}
		if(userType==2){
			if(isAudit!=1){
				return "课程还没有审核通过，请联系管理员";
			}
			if(!course.getCrUser().equals(user.getUsername())){
				return "只能维护自己的课程";
			}
		}
		//查询是否已存在
		String sql = "select r.* from e_record as r"
				+ " join e_course as c on r.courseId=c.id"
				+ " join e_term as t on c.termId=t.id "
				+ " where r.studentId=:studentId "
				+ " and t.`year`=:year"
				+ " and c.type=:type and r.isDeleted=0";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("studentId", studentId);
		String year = DateUtil.getYear(new Date());
		Date date = DateUtil.getDate("yyyy-mm", year+"-6");
		if(new Date().getTime()>date.getTime()){
			year = year+"下半年";
		}else{
			year = year+"上半年";
		}
		int courseType = course.getType();
		params.put("year", year);
		params.put("type", courseType);
		int count = dao.queryCountBySql(sql, params);
		String error = courseType==6?"单周":"双周";
		if(count>0 && userType!=1){
			return "该学生已选了"+error+"的课程";
		}
		if(count>0 && userType==1){
			return "您已选了"+error+"的课程，可以在\"我的课程\"中查看";
		}
				
		//所属学期
		Term term = dao.getObjectById(Term.class, course.getTermId());
		long nowTime = new Date().getTime();
		if(nowTime<term.getStartTime().getTime()){
			return "选课时间还没有开始";
		}
		if(nowTime>term.getEndTime().getTime()){
			return "选课时间已经结束啦";
		}
				
		//查询当前用户的dept id
		List<IDept> deptList = user.getDepts();
		if(deptList==null){
			deptList = deptService.loadUserDepts(user.getUsername());
		}
		User student = null;
		//如果是老师指定学生
		if(userType!=1){
			student = dao.getObjectById(User.class, studentId);
			deptList = deptService.loadUserDepts(student.getUsername());
		}
		
		//课程限制的年级
		String deptIds = course.getDeptIds();//这里存的是部门名字
		if(!StringUtil.isEmpty(deptIds)){
			boolean flg = false;
			for(IDept dept : deptList){
				String parentId = dept.getParentId();
				if(StringUtil.isEmpty(deptIds)){
					flg = true;
					break;
				}
				String[] array = deptIds.split(",");
				for (String str : array) {
					if(str.equals(parentId) || str.equals(dept.getId())){
						flg = true;
						break;
					}
				}
				
			}
			if(!flg){
				return "对不起，您没有权限选择该们课程";
			}
		}
		
		// 班限制人数
		int num = course.getNum();
		//查询该班级的报名人数
		//String sql = "select count(r.id),ud.deptId from e_record as r join e_user_dept as ud on r.studentId=ud.userId  join e_course as c on r.courseId=c.id where r.isDeleted=0 and r.courseId=12  and ud.deptId='aa'";
		StringBuilder sb = new StringBuilder();
		sb.append(" select count(r.id) as ct,ud.deptId ");
		sb.append(" from "+Record.TABLENAME+" as r ");
		sb.append(" join "+DeptUser.TABLENAME+" as ud on r.studentId=ud.userId ");
		sb.append(" join "+Course.TABLENAME+" as c on r.courseId=c.id ");
		sb.append(" where r.isDeleted=0 and r.courseId=:courseId ");
		sb.append(" and ud.deptId=:deptId ");
		Map<String,Object> countParams = new HashMap<String, Object>();
		countParams.put("courseId", courseId);
		countParams.put("deptId", deptList.get(0));
		List<Map<String,Object>> countList = dao.queryBySql(sb.toString(), countParams);
		if(countList!=null && countList.size()>0){
			int deptCount = Integer.valueOf(countList.get(0).get("ct").toString());
			if(num<=deptCount){
				return "对不起，该课程名额已满，您可以选择其他课程。";
			}
		}
		//总人数限制
		int total = course.getTotal();
		String countSql = "select id from "+Record.TABLENAME+" as r where r.isDeleted=0 and r.courseId=:courseId";
		Map<String,Object> totalParams = new HashMap<String, Object>();
		totalParams.put("courseId", courseId);
		int curTotal = dao.queryCountBySql(countSql, totalParams);
		if(curTotal>=total){
			return "对不起，该课程名额已满，您可以选择其他课程";
		}
		Record record = new Record();
		record.setCourseId(courseId);
		record.setCrTime(new Date());
		record.setCrUser(user.getUsername());
		record.setIsDeleted(false);
		record.setStudentId(studentId);
		
		dao.saveOrUpdate(record);
		return null;
	}
	


	@Expose
	public void deleteRecord(Map<String,Object> param) throws Exception {
		Long courseId = Long.valueOf(param.get("courseId").toString());
		Long recordId = Long.valueOf(param.get("recordId").toString());
		String hql = "update "+Record.class.getName()+" set isDeleted=true where courseId=:courseId and id=:recordId ";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("courseId", courseId);
		params.put("recordId", recordId);
		dao.executeHQL(hql, params);
	}
	
//	@DataResolver
//	public void saveRecords(List<Map<String,Object>> maps) throws Exception {
//		for (int i =0;i<maps.size();i++) {
//			EntityState state = EntityUtils.getState(maps.get(i));
//			Record record = (Record)DosolaUtil.convertMap(Record.class, maps.get(i));
//			IUser user2 = ContextHolder.getLoginUser();
//	    	String userName = user2.getUsername();
//			if(EntityState.NEW.equals(state)){
//				record.setCrTime(new Date());
//				record.setCrUser(userName);
//				record.setIsDeleted(false);
//				dao.saveOrUpdate(record);
//			}else if(EntityState.MODIFIED.equals(state)){
//				dao.saveOrUpdate(record);
//			}else if (EntityState.DELETED.equals(state)) {
//				record.setIsDeleted(true);
//				dao.delete(record);
//			}
//		}
//	}
	
	//学生查询自己的选课记录
	@DataProvider
    public void queryRecords(Page<Map<String,Object>> page, Criteria criteria,int type){
		User user = (User)ContextHolder.getLoginUser();
		StringBuilder sb = new StringBuilder();
        sb.append(" select r.*,");
        sb.append(" u.cname as teacherName,");//老师
        sb.append(" c.`name` as courseName,");//课程名称
        sb.append(" c.`type` as courseType,");//课程名称
        sb.append(" u2.`cname` as studentName,");//学生姓名
        sb.append(" t.`name` as termName,");//学期
        sb.append(" cr.`name` as classroomName");//教室名称
        sb.append(" From "+Record.TABLENAME+" as r");
        sb.append(" join "+Course.TABLENAME+" as c ");
        sb.append(" join "+User.TABLENAME+" as u ");
        sb.append(" join "+User.TABLENAME+" as u2 ");
        sb.append(" join "+Classroom.TABLENAME+" as cr ");
        sb.append(" join "+Term.TABLENAME+" as t ");
        sb.append(" where r.courseId=c.id ");
        sb.append(" and c.teacherId=u.id");
        sb.append(" and r.studentId=u2.id");
        sb.append(" and c.classroomId=cr.id");
        sb.append(" and c.termId=t.id");
        sb.append(" and r.isDeleted=0 ");
        
        Map<String,Object> params = new HashMap<String, Object>();
        //学生自己查看自己的课程
        if(type==1){
        	 sb.append(" and r.studentId=:studentId ");
        	 params.put("studentId", user.getId());
        }
        
        //替换前台传入的字段名称
		Map<String,String> propertyMap = new HashMap<String, String>();
		propertyMap.put("teacherName", "u.cname");
		propertyMap.put("courseName", "c.`name`");
		propertyMap.put("studentName", "u2.cname");
		propertyMap.put("classroomName", "cr.`name`");
		propertyMap.put("termName", "t.`name`");
		SqlKit.replaceProperty(criteria, propertyMap);
      		
        ParseResult result = SqlKit.parseCriteria(criteria,true,"r",true);
        String orderSql = SqlKit.buildOrderSql(criteria,"r");
        
        if(result!=null){
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params.putAll(result.getValueMap());
        }
        if(StringUtils.isEmpty(orderSql)){
            sb.append(" ORDER BY r.crTime desc");
        }
        sb.append(orderSql);
        
        dao.pagingQueryBySql(page, sb.toString(), params);
    }
	
	//查询某个课程的选课记录、或全部记录
	@DataProvider
    public void queryRecordsByCourse(Page<Map<String,Object>> page, Criteria criteria,Map<String,Object> parameter){
		StringBuilder sb = new StringBuilder();
        sb.append(" select r.*,");
        sb.append(" u.cname as teacherName,");//老师
        sb.append(" c.`name` as courseName,");//课程名称
        sb.append(" c.`type` as courseType,");//课程名称
        sb.append(" u2.`cname` as studentName,");//学生姓名
        sb.append(" t.`name` as termName,");//学期
        sb.append(" cr.`name` as classroomName,");//教室名称
        sb.append(" group_concat(d.`name`) as deptNames ");//学生所属部门
        sb.append(" From "+Record.TABLENAME+" as r");
        sb.append(" join "+Course.TABLENAME+" as c ");
        sb.append(" join "+User.TABLENAME+" as u ");
        sb.append(" join "+User.TABLENAME+" as u2 ");
        sb.append(" join "+Classroom.TABLENAME+" as cr ");
        sb.append(" join "+Term.TABLENAME+" as t ");
        sb.append(" join "+DeptUser.TABLENAME+" as du ");
        sb.append(" join "+Dept.TABLENAME+" as d ");
        sb.append(" where r.courseId=c.id ");
        sb.append(" and c.teacherId=u.id");
        sb.append(" and r.studentId=u2.id");
        sb.append(" and c.classroomId=cr.id");
        sb.append(" and c.termId=t.id");
        sb.append(" and du.userId=u2.id");
        sb.append(" and du.deptId=d.id");
        sb.append(" and r.isDeleted=0 ");
        Object obj = parameter.get("courseId");
        Integer courseId = null;
        if(obj!=null){
        	courseId = Integer.valueOf(obj.toString());
        }
        Map<String,Object> params = new HashMap<String, Object>();
        //查看课程的选课记录
        if(courseId!=null){
        	 sb.append(" and c.id=:courseId ");
        	 params.put("courseId", courseId);
        }
        
        //替换前台传入的字段名称
		Map<String,String> propertyMap = new HashMap<String, String>();
		propertyMap.put("teacherName", "u.cname");
		propertyMap.put("courseName", "c.`name`");
		propertyMap.put("studentName", "u2.cname");
		propertyMap.put("classroomName", "cr.`name`");
		propertyMap.put("termName", "t.`name`");
		propertyMap.put("deptNames", "d.`name`");
		propertyMap.put("courseType", "c.`type`");
		SqlKit.replaceProperty(criteria, propertyMap);
      		
        ParseResult result = SqlKit.parseCriteria(criteria,true,"r",true);
        String orderSql = SqlKit.buildOrderSql(criteria,"r");
        
        if(result!=null){
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params.putAll(result.getValueMap());
        }
        sb.append(" group by r.id");
        if(StringUtils.isEmpty(orderSql)){
            sb.append(" ORDER BY r.crTime desc");
        }
        sb.append(orderSql);
        
        dao.pagingQueryBySql(page, sb.toString(), params);
    }
	
	@Expose
	public String queryHomework(long courseId){
		Course course = dao.getObjectById(Course.class, courseId);
		if(course!=null){
			return course.getHomework();
		}
		return null;
	}
	
	@Expose
	public String queryStudentHomework(long recordId){
		Record record = dao.getObjectById(Record.class, recordId);
		if(record!=null){
			return record.getHomework();
		}
		return null;
	}
	
	@Expose
	public void saveHomework(String homework,long recordId){
		User user = (User)ContextHolder.getLoginUser();
		Record record = dao.getObjectById(Record.class, recordId);
		if(record==null){
			throw new RuntimeException("没有找到您的选课记录，无法提交作业。");
		}
		if(record!=null && record.getStudentId()==user.getId()){
			record.setHomework(homework);
			dao.saveOrUpdate(record);
		}else{
			throw new RuntimeException("只能提交自己的作业");
		}
	}
	
	@Expose
	public void saveScore(String score,long recordId){
		User user = (User)ContextHolder.getLoginUser();
		if(user.getType()==1){
			throw new RuntimeException("您没有权限批改作业。");
		}
		Record record = dao.getObjectById(Record.class, recordId);
		if(record==null){
			throw new RuntimeException("没有找到该选课记录，无法批改作业。");
		}
		record.setScore(score);
		dao.saveOrUpdate(record);
	}

}