package com.elective.service;

import com.bstek.bdf2.core.business.IDept;
import com.bstek.bdf2.core.service.IDeptService;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.elective.pojo.Dept;

import javax.annotation.Resource;
import java.util.List;

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
        return null;
    }

    @Override
    public IDept loadDeptById(String deptId) {
        return null;
    }

    @Override
    public List<IDept> loadDeptsByParentId(String parentId,String companyId) {
        return null;
    }
}
