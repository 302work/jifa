package com.jifa.test.dao;


import com.bstek.dorado.data.provider.Page;
import com.jifa.test.base.AbstractTestCase;
import com.jifa.test.pojo.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author june
 *         2015年04月30日 00:15
 */
public class TestMasterDao extends AbstractTestCase {


    /**
     * 测试根据主键查询对象
     */
    @Test
    public void testGetObjectById(){
        String username = "junegod";
        User user = this.getMasterDao().getObjectById(User.class,username);
        Assert.assertEquals(username,user.getUsername());
    }

    /**
     * 测试原生sql查询数量
     */
    @Test
    public void testQueryCountBySql(){

        //测试不带参数
        int count = this.getMasterDao().queryCountBySql(" select * from bdf2_company",null);
        Assert.assertEquals(1,count);

        //测试带参数
        String sql = "select * from bdf2_company where id_=:id";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id","jifa");

        count = this.getMasterDao().queryCountBySql(sql,params);
        Assert.assertEquals(1,count);
    }

    /**
     * 测试hql查询数量
     */
    @Test
    public void testQueryCount(){
        String hql = "From "+ User.class.getName()+" where username=:username";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username","junegod");
        int count = this.getMasterDao().queryCount(hql,params);
        Assert.assertEquals(1,count);
    }

    /**
     * 测试查询
     */
    @Test
    public void testquery(){
        String hql = "From "+ User.class.getName()+" where username=:username";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username","junegod");
        List<?> list = this.getMasterDao().query(hql,params);
        User user = (User)list.get(0);
        Assert.assertEquals(user.getUsername(),"junegod");
    }

    /**
     * 测试sql查询
     */
    @Test
    public void testqueryBySql(){
        String sql = "select * From "+ User.DBNAME+" where username_=:username";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username","junegod");
        List<Map<String, Object>> list = this.getMasterDao().queryBySql(sql, params);
        String username = list.get(0).get("USERNAME_").toString();
        Assert.assertEquals(username,"junegod");
    }

    /**
     * 测试hql分页查询
     */
    @Test
    public void testPagingQuery(){
        String hql = "From "+ User.class.getName()+" where cname=:cname";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cname","周勇");
        Page<?> page = this.getMasterDao().pagingQuery(hql,1,1,params);

        Assert.assertEquals(page.getPageCount(),2);
        Assert.assertEquals(page.getPageNo(),1);
        Assert.assertEquals(page.getEntityCount(),2);
        Assert.assertEquals(page.getEntities().size(),1);

    }

    /**
     * 测试hql分页查询
     */
    @Test
    public void testPagingQuery2(){
        String hql = "From "+ User.class.getName()+" where cname=:cname";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cname","周勇");
        Page<?> page  = new Page(1,1);
        this.getMasterDao().pagingQuery(page,hql,params);
        Assert.assertEquals(page.getPageCount(),2);
        Assert.assertEquals(page.getPageNo(),1);
        Assert.assertEquals(page.getEntityCount(),2);
        Assert.assertEquals(page.getEntities().size(),1);

    }

    /**
     * 测试sql分页查询
     */
    @Test
    public void testPagingQueryBySql(){
        String sql = "select * From "+ User.DBNAME+" where cname_=:cname";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cname","周勇");
        Page<?> page = this.getMasterDao().pagingQueryBySql(sql, 1, 1, params);

        Assert.assertEquals(page.getPageCount(),2);
        Assert.assertEquals(page.getPageNo(),1);
        Assert.assertEquals(page.getEntityCount(),2);
        Assert.assertEquals(page.getEntities().size(),1);

    }

    /**
     * 测试sql分页查询
     */
    @Test
    public void testPagingQueryBySql2(){
        String sql = "select * From "+ User.DBNAME+" where cname_=:cname";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cname","周勇");
        Page<Map<String, Object>> page  = new Page(1,1);

        this.getMasterDao().pagingQueryBySql(page, sql, params);

        Assert.assertEquals(page.getPageCount(),2);
        Assert.assertEquals(page.getPageNo(),1);
        Assert.assertEquals(page.getEntityCount(),2);
        Assert.assertEquals(page.getEntities().size(),1);

    }

    /**
     * 测试新增更新
     */
    @Test
    public void testSaveOrUpdate(){
        User user = new User();
        user.setAdministrator(false);
        user.setCname("测试222");
        user.setCompanyId("jifa");
        user.setEmail("2222@qq.com");
        user.setEname("june");
        user.setPassword("111111");
        user.setUsername("testtest");
        user.setMale(false);
        user.setSalt("aaaa");
        user.setEnabled(true);
        this.getMasterDao().saveOrUpdate(user);
    }

    /**
     * 测试删除
     */
    @Test
    public void testDelete(){
        User user = this.getMasterDao().getObjectById(User.class,"testtest");
        this.getMasterDao().delete(user);
    }

    /**
     * 测试执行HQL
     */
    @Test
    public void testExecuteHQL(){
        String hql = "delete From "+User.class.getName()+" where username_=:username";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username","testtest");
        this.getMasterDao().executeHQL(hql,params);
    }

    /**
     * 测试执行sql
     */
    @Test
    public void testExecuteSQL(){
        String sql = "delete From "+User.DBNAME+" where username_=:username";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username","testtest");
        this.getMasterDao().executeSQL(sql,params);
    }

}
