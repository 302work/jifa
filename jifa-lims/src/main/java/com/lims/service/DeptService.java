package com.lims.service;

import com.bstek.bdf2.core.business.IDept;
import com.bstek.bdf2.core.service.IDeptService;
import com.dosola.core.common.DosolaUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.Dept;
import com.lims.pojo.DeptUser;
import com.lims.pojo.User;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 部门管理
 * @author june
 * 2015年06月02日 15:34
 */
public class DeptService implements IDeptService{

    @Resource
    private IMasterDao dao;

    @Override
    public IDept newDeptInstance(String deptId) {
        return new Dept(deptId);
    }

    @Override
    public List<IDept> loadUserDepts(String username) {
        if(StringUtils.isEmpty(username)){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(" select d.* from ");
        sb.append(Dept.TABLENAME+" as d join ");
        sb.append(DeptUser.TABLENAME+" as du on d.id=du.deptId join ");
        sb.append(User.TABLENAME+" as u on u.id=du.userId");
        sb.append(" where u.username=:username and d.isDeleted=:isDeleted ");
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("username",username);
        params.put("isDeleted", false);
        List<Map<String,Object>> list = dao.queryBySql(sb.toString(),params);
        try {
            List<IDept> depts = new ArrayList<IDept>();
            for(Map<String,Object> map : list){
                Dept dept = new Dept();
                DosolaUtil.buildPoJo(dept,map);
            	depts.add(dept);
            }
            return depts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public IDept loadDeptById(String deptId) {
        return dao.getObjectById(Dept.class,deptId);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<IDept> loadDeptsByParentId(String parentId,String companyId) {
        if(StringUtils.isEmpty(companyId)){
            return null;
        }
        String hql = "From "+Dept.class.getName()+" where companyId=:companyId and isDeleted=:isDeleted";
        Map<String,Object> params = new HashMap<>();
        params.put("isDeleted", false);
        if(parentId==null){
            hql += " and  parentId is null ";
        }else{
            hql += " and parentId=:parentId ";
            params.put("parentId",parentId);
        }
        hql += " order by sortFlag ";
        params.put("companyId",companyId);
        return (List<IDept>)dao.query(hql,params);
    }
    
    /**
     * 根据部门名称查询
     * @param deptName
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<IDept> loadDeptsByName(String deptName) {
        if(StringUtils.isEmpty(deptName)){
            return null;
        }
        String hql = "From "+Dept.class.getName()+" where name=:deptName and isDeleted=:isDeleted";
        Map<String,Object> params = new HashMap<>();
        params.put("isDeleted", false);
        params.put("deptName",deptName);
        return (List<IDept>)dao.query(hql,params);
    }
    
}
