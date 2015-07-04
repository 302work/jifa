package com.elective.view.record;

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

	@DataResolver
	public void saveRecords(List<Map<String,Object>> maps) throws Exception {
		for (int i =0;i<maps.size();i++) {
			EntityState state = EntityUtils.getState(maps.get(i));
			Record record = (Record)DosolaUtil.convertMap(Record.class, maps.get(i));
			IUser user2 = ContextHolder.getLoginUser();
	    	String userName = user2.getUsername();
			if(EntityState.NEW.equals(state)){
				record.setCrTime(new Date());
				record.setCrUser(userName);
				record.setIsDeleted(false);
				dao.saveOrUpdate(record);
			}else if(EntityState.MODIFIED.equals(state)){
				dao.saveOrUpdate(record);
			}else if (EntityState.DELETED.equals(state)) {
				record.setIsDeleted(true);
				dao.delete(record);
			}
		}
	}
	
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

}