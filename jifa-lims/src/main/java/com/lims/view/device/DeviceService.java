package com.lims.view.device;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.dorado.common.ParseResult;
import com.dorado.common.SqlKit;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.Device;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备维护
 * @author june
 * 2015年09月20日 18:11
 */
@Service
public class DeviceService {

    @Resource
    private IMasterDao dao;

    @DataProvider
    public void queryDevice(Page<Device> page, Criteria criteria) {
        StringBuilder sb = new StringBuilder();
        sb.append( " From "+ Device.class.getName() +" where 1=1 ");
        ParseResult result = SqlKit.parseCriteria(criteria, true, null, false);
        String orderSql = SqlKit.buildOrderHql(criteria,null);
        Map<String,Object> params = new HashMap<String, Object>();
        if(result!=null){
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params = result.getValueMap();
        }
        if(StringUtils.isEmpty(orderSql)){
            sb.append(" ORDER BY crTime desc");
        }
        sb.append(orderSql);

        dao.pagingQuery(page,sb.toString(),params);
    }


    @DataResolver
    public void saveDevice(Collection<Device> devices){
        for (Device device : devices) {
            EntityState state = EntityUtils.getState(device);
            IUser user2 = ContextHolder.getLoginUser();
            String userName = user2.getUsername();
            if(EntityState.NEW.equals(state)){
                device.setCrTime(new Date());
                device.setCrUser(userName);
                //默认启用
                device.setStatus(1);
                dao.saveOrUpdate(device);
            }else if(EntityState.MODIFIED.equals(state)){
                dao.saveOrUpdate(device);
            }else if (EntityState.DELETED.equals(state)) {
                //删除，校验是否被占用，占用无法删除

            }
        }
    }

    @Expose
    public void changeStatus(long deviceId,int type){
        if(type!=1 && type!=2){
            return;
        }
        String sql = "update "+Device.TABLENAME+" set status=:status where id=:deviceId";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("deviceId",deviceId);
        params.put("status",type);
        dao.executeSQL(sql,params);
    }
}
