package com.elective.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.bstek.bdf2.core.business.IDept;
import com.bstek.bdf2.core.service.IDeptService;
import com.dosola.core.common.DosolaUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.elective.pojo.Dept;
import com.elective.pojo.DeptUser;
import com.elective.pojo.User;

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
        sb.append(" where u.username=:username ");
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("username",username);
        List<Map<String,Object>> list = dao.queryBySql(sb.toString(),params);
        try {
            List<IDept> depts = new ArrayList<IDept>();
            for(Map<String,Object> map : list){
                depts.add((Dept)DosolaUtil.convertMap(Dept.class,map));
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
        String hql = "From "+Dept.class.getName()+" where companyId=:companyId";
        Map<String,Object> params = new HashMap<>();
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
}
