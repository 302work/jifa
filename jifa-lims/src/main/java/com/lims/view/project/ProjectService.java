package com.lims.view.project;

import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.MethodStandard;
import com.lims.pojo.Project;
import com.lims.pojo.ProjectMethodStandard;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author june
 * 2015年10月02日 22:18
 */
@Service
public class ProjectService {

    @Resource
    private IMasterDao dao;


    @DataProvider
    public List<Project> getProjectsByParentId(long projectId){
        StringBuilder sb = new StringBuilder();
        sb.append(" From "+Project.class.getName()+" where isDeleted<>1 ");
        Map<String,Object> params = new HashMap<String, Object>();
        if(projectId==0){
            sb.append(" and (parentId is null or parentId=0 or parentId='') ");
        }else{
            sb.append(" and parentId=:parentId");
            params.put("parentId",projectId);
        }
        List<Project> list = (List<Project>) dao.query(sb.toString(),params);
        if(list!=null && list.size()>0){
            for (Project project : list){
                project.setHasChild(hasChild(project));
            }
        }
        return list;
    }

    /**
     * 项目是否有子项目
     * @param project
     * @return
     */
    public boolean hasChild(Project project){
        if(project==null || project.getId()==null){
            return false;
        }
        Long projectId = project.getId();
        String hql = " From "+Project.class.getName()+" where isDeleted<>1 and parentId=:parentId";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("parentId",projectId);
        return dao.queryCount(hql,params)>0;
    }

    /**
     * 项目是否有方法标准
     * @param project
     * @return
     */
    public boolean hasMethodStandard(Project project){
        if(project==null || project.getId()==null){
            return false;
        }
        Long projectId = project.getId();
        String hql = " From "+ProjectMethodStandard.class.getName()+" where projectId=:projectId";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("projectId",projectId);
        return dao.queryCount(hql,params)>0;
    }

    @DataProvider
    public void queryMethodStandardsById(Page<MethodStandard> page, long projectId, Criteria criteria){

        if(projectId<=0){
            return;
        }

        //三表关联查询
        StringBuilder sb = new StringBuilder();
        sb.append( " select mt.* from ");
        sb.append( " "+ ProjectMethodStandard.TABLENAME+" as pmt ");
        sb.append( " join "+MethodStandard.TABLENAME+" as mt ");
        sb.append( " on pmt.methodStandardId=mt.id ");
        sb.append( " where mt.isDeleted<>1 ");
        sb.append(" and pmt.projectId=:projectId ");

        //构建查询条件
        ParseResult result = SqlKit.parseCriteria(criteria, true, "mt", true);
        //排序
        String orderSql = SqlKit.buildOrderSql(criteria, "mt");

        Map<String,Object> params = new HashMap<String, Object>();
        params.put("projectId",projectId);

        if(result!=null){
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params.putAll(result.getValueMap());
        }
        if(StringUtils.isEmpty(orderSql)){
            sb.append(" ORDER BY mt.crTime desc");
        }
        sb.append(orderSql);

        dao.pagingQueryBySql(page,sb.toString(),params,MethodStandard.class);
    }
}
