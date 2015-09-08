/**
 * 
 */
package com.elective.view.user;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.bstek.bdf2.core.business.IDept;
import com.bstek.bdf2.importexcel.model.CellWrapper;
import com.bstek.bdf2.importexcel.model.ExcelDataWrapper;
import com.bstek.bdf2.importexcel.model.RowWrapper;
import com.bstek.bdf2.importexcel.processor.IExcelProcessor;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.elective.pojo.User;
import com.elective.service.DeptService;
import com.elective.service.UserService;

/**
 * 学生导入
 * @author june
 * 2015年9月8日
 */
@Service
public class UserExcelProcessor implements IExcelProcessor{

	@Resource
    private IMasterDao dao;
	
	@Resource
	private UserService userService;
	
	@Resource
	private DeptService deptService;
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public String getName() {
		return "学生导入";
	}
	
	@Override
	public int execute(ExcelDataWrapper excelDW) throws Exception {
		Collection<RowWrapper> rows = excelDW.getRowWrappers();
		int count = 0;
		//循环行
		for (RowWrapper row : rows) {
			try{
				//列
				CellWrapper[] cells = new CellWrapper[row.getCellWrappers().size()];
				Iterator<CellWrapper> iterator = row.getCellWrappers().iterator();
				int i = 0;
				while(iterator.hasNext()){
					cells[i++] = iterator.next();
				}
				//所属班级
				String deptName = cells[0].getValue()!=null?cells[0].getValue().toString():"";
				int index = deptName.indexOf(".");
				String dn = deptName;
				if(index!=-1){
					dn = deptName.replaceAll("\\.", "");
				}
				//学生姓名
				String userName = cells[1]!=null?cells[1].getValue().toString():"";
				String newUserName = userName+dn;
				//查询班级是否存在
				List<IDept> list = deptService.loadDeptsByName(deptName);
				if(list==null || list.size()==0){
					logger.warn("班级不存在，班级："+deptName+",学生姓名："+userName);
					continue;
					
				}
				if(list.size()>1){
					logger.warn("存在多个同名班级，班级："+deptName+",学生姓名："+userName);
					continue;
				}
				String deptId = list.get(0).getId();
				
				//查询学生是否存在
				User user = userService.queryUser(newUserName);
				if(user!=null){
					logger.warn("学生已存在，班级："+deptName+",学生用户名："+newUserName);
					continue;
				}
				user = new User();
				user.setCname(userName);
				user.setUsername(newUserName);
				
				//导入学生
				user.setType(1);
				userService.saveUser(deptId, user);
				count++;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return count;
	}

}
