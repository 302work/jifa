package com.lims.view.project;

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
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.MethodStandard;
import com.lims.pojo.Project;
import com.lims.pojo.ProjectMethodStandard;
import com.lims.pojo.Standard;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author june
 *         2015年10月02日 22:18
 */
@Service
public class ProjectService {

    @Resource
    private IMasterDao dao;


    @DataProvider
    public List<Project> getProjectsByParentId(Long projectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" From " + Project.class.getName() + " where isDeleted<>1 ");
        Map<String, Object> params = new HashMap<String, Object>();
        if (projectId == null || projectId == 0) {
            sb.append(" and (parentId is null or parentId=0 or parentId='') ");
        } else {
            sb.append(" and parentId=:parentId");
            params.put("parentId", projectId);
        }
        sb.append(" order by sortFlag");
        List<Project> list = (List<Project>) dao.query(sb.toString(), params);
        if (list != null && list.size() > 0) {
            for (Project project : list) {
                project.setHasChild(hasChild(project));
            }
        }
        return list;
    }

    /**
     * 项目是否有子项目
     *
     * @param project
     * @return
     */
    public boolean hasChild(Project project) {
        if (project == null || project.getId() == null) {
            return false;
        }
        Long projectId = project.getId();
        String hql = " From " + Project.class.getName() + " where isDeleted<>1 and parentId=:parentId";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("parentId", projectId);
        return dao.queryCount(hql, params) > 0;
    }

    /**
     * 项目是否有方法标准
     *
     * @param project
     * @return
     */
    public boolean hasMethodStandard(Project project) {
        if (project == null || project.getId() == null) {
            return false;
        }
        Long projectId = project.getId();
        String hql = " From " + ProjectMethodStandard.class.getName() + " where projectId=:projectId";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("projectId", projectId);
        return dao.queryCount(hql, params) > 0;
    }

    @DataProvider
    public void queryProjectsByProjectMethodStandardIds(Page<Map<String, Object>> page, String projectMethodStandardIds, Criteria criteria) {

        if (StringUtils.isEmpty(projectMethodStandardIds)) {
            return;
        }

        //三表关联查询
        StringBuilder sb = new StringBuilder();
        sb.append(" select p.*,pmt.id as projectMethodStandardId,mt.standardNo as methodStandardNo,mt.name as methodStandardName");
        sb.append(" from " + ProjectMethodStandard.TABLENAME + " as pmt ");
        sb.append(" join " + MethodStandard.TABLENAME + " as mt ");
        sb.append(" on pmt.methodStandardId=mt.id ");
        sb.append(" join "+Project.TABLENAME+" as p ");
        sb.append(" on pmt.projectId=p.id");
        sb.append(" where mt.isDeleted<>1 ");
        sb.append(" and pmt.id in (:projectMethodStandardIds)");

        //构建查询条件
        ParseResult result = SqlKit.parseCriteria(criteria, true, "p", true);
        //排序
        String orderSql = SqlKit.buildOrderSql(criteria, "p");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("projectMethodStandardIds", projectMethodStandardIds);

        if (result != null) {
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params.putAll(result.getValueMap());
        }
        if (StringUtils.isEmpty(orderSql)) {
            sb.append(" ORDER BY mt.crTime desc");
        }
        sb.append(orderSql);

        dao.pagingQueryBySql(page, sb.toString(), params);
    }

    @DataProvider
    public void queryMethodStandardsById(Page<Map<String, Object>> page, Long projectId, Criteria criteria) {

        if (projectId == null || projectId <= 0) {
            return;
        }

        //三表关联查询
        StringBuilder sb = new StringBuilder();
        sb.append(" select mt.*,s.name as productStandardName,s.standardId as productStandardId,s.standardNo as productStandardNo,pmt.id as projectMethodStandardId ");
        sb.append(" from " + ProjectMethodStandard.TABLENAME + " as pmt ");
        sb.append(" join " + MethodStandard.TABLENAME + " as mt ");
        sb.append(" on pmt.methodStandardId=mt.id ");
        sb.append(" join " + Standard.TABLENAME + " as s ");
        sb.append(" on mt.standardId=s.id ");
        sb.append(" where mt.isDeleted<>1 ");
        sb.append(" and pmt.projectId=:projectId ");

        //构建查询条件
        ParseResult result = SqlKit.parseCriteria(criteria, true, "mt", true);
        //排序
        String orderSql = SqlKit.buildOrderSql(criteria, "mt");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("projectId", projectId);

        if (result != null) {
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params.putAll(result.getValueMap());
        }
        if (StringUtils.isEmpty(orderSql)) {
            sb.append(" ORDER BY mt.crTime desc");
        }
        sb.append(orderSql);

        dao.pagingQueryBySql(page, sb.toString(), params);
    }

    @DataResolver
    public void saveProjects(Collection<Project> projects) {
        for (Project project : projects) {
            doSaveOrUpdateProject(null, project);
        }
    }

    private void doSaveOrUpdateProject(Long parentId, Project project) {
        IUser user = ContextHolder.getLoginUser();
        String userName = user.getUsername();
        EntityState state = EntityUtils.getState(project);
        //不是none并且不是delete
        if (EntityState.isVisibleDirty(state)) {
            project.setParentId(parentId);
        }
        Project newProject = new Project();
        //如果是删除
        if (EntityState.DELETED.equals(state)) {
            if (!hasChild(project) && !hasMethodStandard(project)) {
                project.setIsDeleted(1);
                dao.saveOrUpdate(project);
            } else {
                throw new RuntimeException("请先删除项目的方法标准");
            }
        } else if (EntityState.NEW.equals(state)) {
            project.setCrTime(new Date());
            project.setCrUser(userName);
            project.setIsDeleted(0);
            newProject = dao.saveOrUpdate(project).get(0);
        } else if (EntityState.MODIFIED.equals(state)) {
            project.setCrTime(new Date());
            project.setCrUser(userName);
            project.setIsDeleted(0);
            dao.saveOrUpdate(project);
        } else if (EntityState.MOVED.equals(state)) {
            project.setCrTime(new Date());
            project.setCrUser(userName);
            project.setIsDeleted(0);
            dao.saveOrUpdate(project);
        }
        Long projectId = project.getId();
        if (newProject.getId() != null) {
            projectId = newProject.getId();
        }
        //子项目
        List<Project> subProjects = EntityUtils.getValue(project, "child");
        if (subProjects != null && subProjects.size() > 0) {
            for (Project subProject : subProjects) {
                doSaveOrUpdateProject(projectId, subProject);
            }
        }
        //方法标准
//        List<MethodStandard> methodStandards = EntityUtils.getValue(project, "methodStandards");
//        if(methodStandards!=null && methodStandards.size()>0){
//            for (MethodStandard methodStandard : methodStandards) {
//                if(methodStandard!=null && methodStandard.getId()!=null){
//                    doSaveOrUpdateMethodStandard(projectId, methodStandard);
//                }else{
//                    throw new RuntimeException("方法标准不能为空");
//                }
//            }
//        }

    }

    /**
     * 添加方法标准
     *
     * @param projectId
     * @param methodStandardId
     */
    @Expose
    public void saveProjectMethodStandard(Long projectId, Long methodStandardId) {
        if (projectId == null || methodStandardId == null) {
            throw new RuntimeException("参数有误");
        }
        //先删除关联关系
        String hql = "delete from " + ProjectMethodStandard.class.getName() + " where projectId=:projectId and methodStandardId=:methodStandardId";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("projectId", projectId);
        params.put("methodStandardId", methodStandardId);
        dao.executeHQL(hql, params);

        ProjectMethodStandard projectMethodStandard = new ProjectMethodStandard();
        projectMethodStandard.setMethodStandardId(methodStandardId);
        projectMethodStandard.setProjectId(projectId);

        dao.saveOrUpdate(projectMethodStandard);
    }

    @Expose
    public void deleteProjectMethodStandard(Long projectId, Long methodStandardId) {
        if (projectId == null || methodStandardId == null) {
            throw new RuntimeException("参数有误");
        }
        //删除关联关系
        String hql = "delete from " + ProjectMethodStandard.class.getName() + " where projectId=:projectId and methodStandardId=:methodStandardId";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("projectId", projectId);
        params.put("methodStandardId", methodStandardId);
        dao.executeHQL(hql, params);
    }
}
