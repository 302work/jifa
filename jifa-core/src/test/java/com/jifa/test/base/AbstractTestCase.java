/**
 * 2013-4-1
 */
package com.jifa.test.base;


import com.dosola.core.dao.interfaces.IMasterDao;
import org.junit.runner.RunWith;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 单元测试基类，可自动回滚
 * @author june
 *
 */
@SuppressWarnings("deprecation")
@ContextConfiguration
  (
       locations ={
       "classpath:context.xml"
    }
  )
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(transactionManager = "masterTransactionManager", defaultRollback = false)
public abstract class AbstractTestCase extends AbstractTransactionalDataSourceSpringContextTests {

    @Resource
	private IMasterDao masterDao;

    public IMasterDao getMasterDao() {
        return masterDao;
    }
}
