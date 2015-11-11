package com.lims.view.projectRole;

import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.provider.Page;
import com.dosola.core.common.StringUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.ProjectRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author june
 * 2015年11月11日 16:38
 */
@Service
public class ProjectRoleService {
    @Resource
    private IMasterDao dao;

    @DataProvider
    public void query(Page<Map<String,Object>> page, Long projectId){
        if(projectId==null || projectId<=0){
            return;
        }
        String sql = "select r.NAME_ as roleName,pr.id as projectRoleId " +
                " from "+ProjectRole.TABLENAME+" as pr " +
                " join BDF2_ROLE as r " +
                " on pr.roleId=r.ID_" +
                " where  projectId=:projectId";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("projectId",projectId);
        dao.pagingQueryBySql(page,sql,params);
    }

    @Expose
    public void deleteProjectRole(Long projectRoleId){
        if(projectRoleId==null || projectRoleId==0){
            return;
        }
        String sql = "delete from "+ProjectRole.TABLENAME+" where id=:projectRoleId";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("projectRoleId",projectRoleId);
        dao.executeSQL(sql,params);
    }

    @Expose
    public void saveProjectRole(String roleId, Long projectId){
        if(projectId==null || projectId==0 || StringUtil.isEmpty(roleId)){
            return;
        }
        //先删除存在的关联关系
        String sql = "delete from "+ProjectRole.TABLENAME+" where projectId=:projectId and roleId=:roleId";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("projectId",projectId);
        params.put("roleId",roleId);
        dao.executeSQL(sql,params);
        ProjectRole projectRole = new ProjectRole();
        projectRole.setProjectId(projectId);
        projectRole.setRoleId(roleId);
        dao.saveOrUpdate(projectRole);
    }
}
