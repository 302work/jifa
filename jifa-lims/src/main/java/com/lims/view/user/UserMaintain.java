/**
 * 
 */
package com.lims.view.user;

import com.bstek.bdf2.core.security.UserShaPasswordEncoder;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.bstek.dorado.uploader.UploadFile;
import com.bstek.dorado.uploader.annotation.FileResolver;
import com.bstek.dorado.web.DoradoContext;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.common.DateUtil;
import com.dosola.core.common.StringUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.DeptUser;
import com.lims.pojo.User;
import com.lims.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * 用户维护
 * @author june
 * 2015年6月11日
 */
@SuppressWarnings("deprecation")
@Service
public class UserMaintain {
	@Resource
    private IMasterDao dao;
	
	@Resource
	private UserService userService;
	
	@Resource(name= UserShaPasswordEncoder.BEAN_ID)
    private PasswordEncoder passwordEncoder;
	
	@DataResolver
	public void saveUser(Collection<User> users) throws Exception {
		for (User user : users) {
			EntityState state = EntityUtils.getState(user);
			if(EntityState.NEW.equals(state)){
				userService.saveUser(user);
			}else if(EntityState.MODIFIED.equals(state)){
				dao.saveOrUpdate(user);
			}else if (EntityState.DELETED.equals(state)) {
				userService.deleteUser(user);
			}
		}
	}
	
	 /**
	  * 查询部门下的所有用户
	  * @param page
	  * @param deptId
	  * @param criteria
	  */
    @DataProvider
    public void getUsersByDeptId(Page<User> page, String deptId, Criteria criteria){
    	StringBuilder sb = new StringBuilder();
    	sb.append(" select u From "+DeptUser.class.getName()+" as du ");
    	sb.append(" inner join du.user as u ");
    	sb.append(" where u.id=du.userId ");
    	sb.append(" and du.deptId=:deptId ");
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("deptId",deptId);
        ParseResult result = SqlKit.parseCriteria(criteria,true,"u",false);
        String orderSql = SqlKit.buildOrderHql(criteria,"u");
        if(result!=null){
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params.putAll(result.getValueMap());
        }
        if(StringUtils.isEmpty(orderSql)){
            sb.append(" ORDER BY u.crTime desc");
        }
        sb.append(orderSql);
        dao.pagingQuery(page,sb.toString(),params);
    }

	//上传电子签名
	@FileResolver
	public String processFile(UploadFile file, Map<String, Object> parameter) {
		String fjlj = null;
		try {
			String fileName = file.getFileName();
			// 文件扩展名
			String extName = fileName.substring(fileName.indexOf(".") + 1, fileName
					.length());
			if (StringUtil.isEmpty(extName)
					|| (!extName.equalsIgnoreCase("jpg")
					&& !extName.equalsIgnoreCase("png")
					&& !extName.equalsIgnoreCase("jpeg") && !extName
					.equalsIgnoreCase("bmp"))) {
				throw new RuntimeException("只能上传图片格式为jpg、png、bmp的文件！");
			}
			String folderName = DateUtil.getDateStr(new Date()).substring(0, 7);
			//当天
			String todayName = DateUtil.getDateStr(new Date()).substring(8, 10);
			//新文件名
			String newName = new Date().getTime() + "." + extName;
			// 获取系统路径
			String ctxDir = null;
			ResourceBundle rb = ResourceBundle.getBundle("application");
			if(rb.containsKey("upload.path") && !StringUtil.isEmpty(rb.getString("upload.path"))){
				ctxDir = rb.getString("upload.path");
			}else{
				ctxDir = DoradoContext.getCurrent().getServletContext().getRealPath("/");
			}
			if (!ctxDir.endsWith(String.valueOf(File.separatorChar))) {
				ctxDir = ctxDir + File.separatorChar;
			}
			String savePath = ctxDir + "upload" + File.separator + folderName
					+ File.separator + todayName;
			File folderFile = new File(savePath);
			if (!folderFile.exists()) {
				folderFile.mkdirs();
			}
			// 新文件路径
			String newFilePath = savePath + File.separator + newName;
			// 保存上传的文件
			File newFile = new File(newFilePath);
			// 返回前台的路径
			fjlj = "upload" + File.separator + folderName + File.separator
					+ todayName + File.separator + newName;
			// 如果文件存在则删除
			if (newFile.exists()) {
				newFile.delete();
			}
			file.transferTo(newFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fjlj;
	}
    
}
