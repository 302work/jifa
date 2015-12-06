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
import com.dosola.core.common.StringUtil;
import com.dosola.core.dao.interfaces.IMasterDao;
import com.lims.pojo.Device;
import com.lims.pojo.Record;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
    public void queryDevice(Page<Device> page, Criteria criteria,Long recordId) {
        StringBuilder sb = new StringBuilder();
        sb.append( " From "+ Device.class.getName() +" where isDeleted<>1 ");
        ParseResult result = SqlKit.parseCriteria(criteria, true, null, false);
        String orderSql = SqlKit.buildOrderHql(criteria,null);
        Map<String,Object> params = new HashMap<String, Object>();
        if(recordId!=null){
            sb.append(" AND id in (:deviceIds)");
            Record record = dao.getObjectById(Record.class,recordId);
            if(record==null){
                return;
            }
            String deviceIds = record.getDeviceIds();
            List<Long> list = new ArrayList<Long>();
            if(!StringUtil.isEmpty(deviceIds)){
                String[] array = deviceIds.split(",");
                for(String str : array){
                    list.add(Long.valueOf(str));
                }
            }
            if(list.size()==0){
                list.add(-1l);
            }
            params.put("deviceIds",list);
        }
        if(result!=null){
            sb.append(" AND ");
            sb.append(result.getAssemblySql());
            params.putAll(result.getValueMap());
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
                device.setIsDeleted(0);
                dao.saveOrUpdate(device);
            }else if(EntityState.MODIFIED.equals(state)){
                dao.saveOrUpdate(device);
            }else if (EntityState.DELETED.equals(state)) {
                //删除，逻辑删除
                device.setIsDeleted(1);
                dao.saveOrUpdate(device);

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

    @Expose
    public void addDevice(Long deviceId,Long recordId){
        if(deviceId==null || recordId==null){
            return;
        }
        Record record = dao.getObjectById(Record.class,recordId);
        if(record==null){
            return;
        }
        String deviceIds = record.getDeviceIds();
        if(StringUtil.isEmpty(deviceIds)){
            deviceIds = deviceId.toString();
        }else{
            List<String> list = Arrays.asList(deviceIds.split(","));
            if(!list.contains(deviceId.toString())){
                deviceIds += ","+deviceId;
            }
        }
        record.setDeviceIds(deviceIds);
        dao.saveOrUpdate(record);
    }

    @Expose
    public void deleteDevice(Long deviceId,Long recordId){
        if(deviceId==null || recordId==null){
            return;
        }
        Record record = dao.getObjectById(Record.class,recordId);
        if(record==null){
            return;
        }
        String deviceIds = record.getDeviceIds();
        if(StringUtil.isEmpty(deviceIds)){
            return;
        }else{
            List<String> list = Arrays.asList(deviceIds.split(","));
            String newDeviceIds = null;
            for(String str : list){
                if(str.equals(deviceId.toString())){
                    continue;
                }
                if(newDeviceIds==null){
                    newDeviceIds = str;
                }else{
                    newDeviceIds += ","+str;
                }
            }
            record.setDeviceIds(newDeviceIds);
            dao.saveOrUpdate(record);
        }
    }
}
