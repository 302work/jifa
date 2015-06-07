package com.elective.service;

import com.bstek.bdf2.core.business.IDept;
import com.bstek.bdf2.core.service.IDeptService;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.elective.pojo.Dept;
import com.elective.pojo.DeptUser;
import com.elective.pojo.User;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
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
        sb.append(" select d from ");
        sb.append(Dept.class.getName()+" as d inner join ");
        sb.append(DeptUser.class.getName()+" as du with d.id=du.deptId inner join ");
        sb.append(User.class.getName()+" as u with u.id=du.userId");
        sb.append(" where u.username=:username ");
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("username",username);
        return (List<IDept>)(dao.query(sb.toString(),params));
    }

    @Override
    public IDept loadDeptById(String deptId) {
        return dao.getObjectById(Dept.class,deptId);
    }

    @Override
    public List<IDept> loadDeptsByParentId(String parentId,String companyId) {
        if(StringUtils.isEmpty(parentId) || StringUtils.isEmpty(companyId)){
            return null;
        }
        String hql = "From "+Dept.class.getName()+" where parentId=:parentId and companyId=:companyId";
        Map<String,Object> params = new HashMap<>();
        params.put("parentId",parentId);
        params.put("companyId",companyId);
        return (List<IDept>)dao.query(hql,params);
    }
}
