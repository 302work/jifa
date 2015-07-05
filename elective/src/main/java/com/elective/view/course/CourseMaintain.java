package com.elective.view.course;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.common.DosolaUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.elective.pojo.Classroom;
import com.elective.pojo.Course;
import com.elective.pojo.Dept;
import com.elective.pojo.Record;
import com.elective.pojo.Term;
import com.elective.pojo.User;

/**
 * 单表维护服务类
 * 实现对com.elective.pojo.Course对象信息的保存操作
 */
@Component("coursePR")
public class CourseMaintain {
	
	@Resource
    private IMasterDao dao;

	@DataResolver
	public void saveCourse(List<Map<String,Object>> maps) throws Exception {
		for (int i =0;i<maps.size();i++) {
			EntityState state = EntityUtils.getState(maps.get(i));
			Course course = (Course)DosolaUtil.convertMap(Course.class, maps.get(i));
			IUser user2 = ContextHolder.getLoginUser();
	    	String userName = user2.getUsername();
			if(EntityState.NEW.equals(state)){
				//检查教室是否占用
				checkClassroom(course);
				course.setCrTime(new Date());
				course.setCrUser(userName);
				course.setIsEnable(1);
				course.setIsAudit(2);
				dao.saveOrUpdate(course);
				
			}else if(EntityState.MODIFIED.equals(state)){
				//检查教室是否占用
				checkClassroom(course);
				dao.saveOrUpdate(course);
			}else if (EntityState.DELETED.equals(state)) {
				//校验课程是否已被使用
				String hql = "From "+Record.class.getName()+" where courseId=:courseId ";
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("courseId", course.getId());
				if(dao.queryCount(hql, params)>0){
					throw new Exception("课程已被使用，无法删除，您可禁用该课程。");
				}
				dao.delete(course);
			}
		}
	}
	/**
	 * 检查教室是否被占用
	 * @param course
	 * @return
	 * @throws Exception 
	 */
	private void checkClassroom(Course course) throws Exception{
		
		String hql = "From "+Course.class.getName()+" where classroomId=:classroomId and type=:type";
		Map<String,Object> params = new HashMap<String, Object>();
		//修改课程
		Long courseId = course.getId();
		if(courseId!=null){
			hql += " and id<>:courseId";
		}
		params.put("classroomId", course.getClassroomId());
		params.put("type", course.getType());
		params.put("courseId", courseId);
		int count = dao.queryCount(hql, params);
		if(count>0){
			throw new Exception("该教室已被占用，请选择其他教室或更改上课时间。");
		}
	}
	
	//课程管理，非管理员只能看见自己的课程
	@DataProvider
    public void queryCourses(Page<Map<String,Object>> page, Criteria criteria){
        
		StringBuilder sb = new StringBuilder();
        sb.append(" select c.*,");
        sb.append(" u.cname as teacherName,");
        sb.append(" r.`name` as classroomName,");
        sb.append(" t.`name` as termName,");
        sb.append(" group_concat(d.`name`) as deptNames ");
        sb.append(" from "+Course.TABLENAME+" as c ");
        sb.append(" join "+User.TABLENAME+" as u on c.teacherId=u.id ");
        sb.append(" join "+Classroom.TABLENAME+" as r on c.classroomId=r.id");
        sb.append(" join "+Term.TABLENAME+" as t on c.termId=t.id");
        sb.append(" left join "+Dept.TABLENAME+" as d on LOCATE(d.id,c.deptIds)>0");
        sb.append(" where 1=1 ");
       
        //替换前台传入的字段名称
		Map<String,String> propertyMap = new HashMap<String, String>();
		propertyMap.put("teacherName", "u.cname");
		propertyMap.put("classroomName", "r.`name`");
		propertyMap.put("termName", "t.`name`");
		propertyMap.put("deptNames", "d.`name`");
		SqlKit.replaceProperty(criteria, propertyMap);
      		
        ParseResult result = SqlKit.parseCriteria(criteria,true,"c",true);
        String orderSql = SqlKit.buildOrderSql(criteria,"c");
        Map<String,Object> params = new HashMap<String, Object>();
        User user = (User)ContextHolder.getLoginUser();
		//非管理员只能查看自己的课程
        if(user.getType()!=3){
        	sb.append(" and u.id=:userId ");
        	params.put("userId", user.getId());
		}
        if(result!=null){
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params.putAll(result.getValueMap());
        }
        sb.append(" group by c.id");
        if(StringUtils.isEmpty(orderSql)){
            sb.append(" ORDER BY c.crTime desc");
        }
        sb.append(orderSql);
        
        dao.pagingQueryBySql(page, sb.toString(), params);
    }
	
	@Expose
	public User getUserInfo(){
		return (User)ContextHolder.getLoginUser();
	}
	
	/**
	 * 审核课程
	 * @throws Exception 
	 */
	@Expose
	public void auditCourse(Map<String,Object> param) throws Exception{
		User user = (User)ContextHolder.getLoginUser();
		if(user.getType()!=3){
			throw new Exception("您无权审核。");
		}
		int courseId = Integer.valueOf(param.get("courseId").toString());
		String button = param.get("button").toString();
		//1为已审核，2为未审核，3为审核未通过
		int isAudit = 0;
		if(button.equals("yes")){
			isAudit = 1;
		}else{
			isAudit = 3;
		}
		String hql = "update "+Course.class.getName()+" set isAudit="+isAudit+" where id="+courseId;
		dao.executeHQL(hql, null);
	}


	/**
	 * 审核课程
	 * @throws Exception 
	 */
	@Expose
	public void enableCourse(Map<String,Object> param) throws Exception{
		int courseId = Integer.valueOf(param.get("courseId").toString());
		String button = param.get("button").toString();
		//1为可用，2为不可用
		int isEnable = 0;
		if(button.equals("yes")){
			isEnable = 1;
		}else{
			isEnable = 2;
		}
		String hql = "update "+Course.class.getName()+" set isEnable="+isEnable+" where id="+courseId;
		dao.executeHQL(hql, null);
	}
	
	//供学生查看的课程列表，审核通过的，可用的
	@DataProvider
    public void queryCourseList(Page<Map<String,Object>> page, Criteria criteria){
        
		StringBuilder sb = new StringBuilder();
        sb.append(" select c.*,");
        sb.append(" u.cname as teacherName,");
        sb.append(" r.`name` as classroomName,");
        sb.append(" t.`name` as termName,");
        sb.append(" group_concat(d.`name`) as deptNames ");
        sb.append(" from "+Course.TABLENAME+" as c ");
        sb.append(" join "+User.TABLENAME+" as u on c.teacherId=u.id ");
        sb.append(" join "+Classroom.TABLENAME+" as r on c.classroomId=r.id");
        sb.append(" join "+Term.TABLENAME+" as t on c.termId=t.id");
        sb.append(" left join "+Dept.TABLENAME+" as d on LOCATE(d.id,c.deptIds)>0");
        sb.append(" where c.isAudit=1 and c.isEnable=1 ");
        
        //替换前台传入的字段名称
		Map<String,String> propertyMap = new HashMap<String, String>();
		propertyMap.put("teacherName", "u.cname");
		propertyMap.put("classroomName", "r.`name`");
		propertyMap.put("termName", "t.`name`");
		propertyMap.put("deptNames", "d.`name`");
		SqlKit.replaceProperty(criteria, propertyMap);
      		
        ParseResult result = SqlKit.parseCriteria(criteria,true,"c",true);
        String orderSql = SqlKit.buildOrderSql(criteria,"c");
        Map<String,Object> params = new HashMap<String, Object>();
        if(result!=null){
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params = result.getValueMap();
        }
        sb.append(" group by c.id");
        if(StringUtils.isEmpty(orderSql)){
            sb.append(" ORDER BY c.crTime desc");
        }
        sb.append(orderSql);
        
        dao.pagingQueryBySql(page, sb.toString(), params);
    }
}