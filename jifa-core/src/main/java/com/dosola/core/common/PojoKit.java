package com.dosola.core.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dosola.core.dao.interfaces.IPojo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实体类相关工具类
 * @author june
 * 2015年08月19日 16:14
 */
public class PojoKit {

    /**
     * 根据map的值 构建pojo
     * @param t
     * @param resultMap
     * @return
     */
    public static <T extends IPojo> T build(T t,Map<String,Object> resultMap){
        if(t==null || resultMap==null || resultMap.isEmpty()){
            return t;
        }
        Class<?> clz = t.getClass();
        Field[] fields = clz.getDeclaredFields();
        //将map转为json
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(resultMap));
        for (Field field : fields){
            String name = field.getName();
            //序列化字段
            if(name.toLowerCase().equals("serialVersionUID")){
                continue;
            }
            //声明的表名字段
            if(name.toLowerCase().equals("tablename")){
                continue;
            }
            //声明的用于操作数据库的me字段
            if(name.toLowerCase().equals("me")){
                continue;
            }
            if (resultMap.containsKey(name)){
                //字段的类型
                Class<?> type = field.getType();
                //map里的值转成字符串
                String value = jsonObject.getString(name);
                Object objValue = ClassKit.getObjectValue(value, type.getName());
                ClassKit.inject(t, name, objValue);
            }

        }
        return t;
    }
    /**
     * 根据map构建pojo
     * @param clazz
     * @param resultMap
     * @return
     */
    public static <T extends IPojo> T build(Class<T> clazz,Map<String,Object> resultMap){
        if(clazz==null || resultMap==null){
            return null;
        }
        return build(ClassKit.newInstance(clazz),resultMap);
    }

    /**
     * 根据map构建pojo集合
     * @param clazz
     * @param resultList
     * @return
     */
    public static <T extends IPojo> List<T> build(Class<T> clazz,List<Map<String,Object>> resultList){
        List<T> ts = new ArrayList<>();
        for (Map<String,Object> resultMap:resultList){
            ts.add(build(clazz,resultMap));
        }
        return ts;
    }






}
