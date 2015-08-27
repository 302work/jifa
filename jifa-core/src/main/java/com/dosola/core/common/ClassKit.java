package com.dosola.core.common;


import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author june
 * 2015年07月25日 18:32
 */
public class ClassKit {

    public static final String SET = "set";
    public static final String GET = "get";

    private static Logger logger = Logger.getLogger(ClassKit.class);

    /**
     * 初始化实例
     * @param clazz
     * @return
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化实例
     * @param clazz
     * @return
     */
    public static Object newInstance(String clazz) {
        try {
            Class<?> loadClass = getDefaultClassLoader().loadClass(clazz);
            return loadClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载类
     * @param clazz
     * @return
     */
    public static Class<?> loadClass(String clazz) {
        try {
            Class<?> loadClass = getDefaultClassLoader().loadClass(clazz);
            return loadClass;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 当前线程的classLoader
     * @return
     */
    public static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }


    /**
     * 返回属性Setter方法名
     * @param property 属性名
     * @return 属性Setter方法名
     */
    public static String getSetterMethodName(String property) {
        if(StrKit.isEmpty(property)){
            return null;
        }
        return SET + StrKit.capitalize(property);
    }

    /**
     * 返回属性Getter方法名
     * @param property 属性名
     * @return 属性Getter方法名
     */
    public static String getGetterMethodName(String property) {
        if(StrKit.isEmpty(property)){
            return null;
        }
        return GET + StrKit.capitalize(property);
    }

    /**
     * 是否存在该字段的get方法
     * @param clz
     * @param property
     * @return
     */
    public static boolean hasGetMethod(Class<?> clz,String property){
        try {
            clz.getDeclaredMethod(getGetterMethodName(property),null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 是否存在该字段的set方法
     * @param clz
     * @param property
     * @return
     */
    public static boolean hasSetMethod(Class<?> clz,String property){
        try {
            Field field = clz.getDeclaredField(property);
            clz.getDeclaredMethod(getSetterMethodName(property),field.getType());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 设置属性值
     * @param object 对象
     * @param property 属性名
     * @param parameterValue 方法参数值
     */
    public static boolean inject(Object object, String property, Object... parameterValue) {
        if(object==null || StrKit.isEmpty(property) || parameterValue==null || parameterValue[0]==null){
            return false;
        }
        Class<?> clz = object.getClass();
        Field field = null;
        try{
            field = clz.getDeclaredField(property);
        }catch (Exception e){
            logger.warn("对象:"+object.getClass()+" 不存在"+property+"字段,error:"+e.getMessage());
            return false;
        }
        if(field!=null){
            try{
                Method setMethod = clz.getDeclaredMethod(getSetterMethodName(property),field.getType());
                setMethod.invoke(object, parameterValue);
                return true;
            }catch (Exception e){
//                logger.warn("对象:"+object.getClass()+" "+property+"字段不存在get或set方法,error:"+e.getMessage());
//                e.printStackTrace();
//                return false;
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    /**
     * 获取对象的声明的字段的集合
     * @param clz
     * @return
     */
    public static List<String> getFieldList(Class<?> clz){
        if(StrKit.isEmpty(clz)){
            return null;
        }
        String[] strs = getFieldMap(clz).keySet().toArray(new String[0]);
        return Arrays.asList(getFieldMap(clz).keySet().toArray(new String[0]));
    }

    /**
     * 获取对象的声明的所有字段
     * @param clz
     * @return
     */
    public static Map<String, Class<?>> getFieldMap(Class<?> clz){
        if(StrKit.isEmpty(clz)){
            return null;
        }
        Field[] fields = clz.getDeclaredFields();
        List<String> list = new ArrayList<String>();
        Map<String, Class<?>> map= new HashMap<String, Class<?>>();
        for (Field field : fields){
            String name = field.getName();
            Class<?> clazz = field.getType();
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
            //没有get set方法的排除

            map.put(name,clazz);
        }
        return map;
    }

    /**
     * 通过实体类的get方法获取属性值
     * @param object
     * @param property
     * @param <T>
     * @return
     */
    public static <T> T getValue(Object object, String property) {
        try{
            return (T) object.getClass().getDeclaredMethod(getGetterMethodName(property)).invoke(object);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据不同类型转换成不同的类型的值
     * @param value
     * @param type
     * @return
     */
    public static Object getObjectValue(String value,String type){

        if(StrKit.isEmpty(type) || StrKit.isEmpty(value)){
            return null;
        }
        //去掉开头的class
        int index = type.toLowerCase().indexOf("class");
        if(index==0){
            type = type.substring(index+5).trim();
        }
        type = type.toLowerCase();
        //int
        if(type.equals("int")){
            return Integer.parseInt(value);
        }
        if(type.equals("java.lang.integer")){
            return Integer.valueOf(value);
        }
        //long
        if(type.equals("long")){
            return Long.parseLong(value);
        }
        if(type.equals("java.lang.long")){
            return Long.valueOf(value);
        }
        //double
        if(type.equals("double")){
            return Double.parseDouble(value);
        }
        if(type.equals("java.lang.double")){
            return Double.valueOf(value);
        }
        //short
        if(type.equals("short")){
          return Short.parseShort(value);
        }
        if(type.equals("java.lang.short")){
            return Short.valueOf(value);
        }
        //float
        if(type.equals("float")){
            return Float.parseFloat(value);
        }
        if(type.equals("java.lang.float")){
            return Float.valueOf(value);
        }
        //boolean
        if(type.equals("boolean")){
            return Boolean.parseBoolean(value);
        }
        if(type.equals("java.lang.boolean")){
            return Boolean.valueOf(value);
        }
        //String
        if(type.equals("string") || type.equals("java.lang.string")){
            return value;
        }
        //BigDecimal
        if(type.equals("java.math.bigdecimal")){
            return new BigDecimal(value);
        }
        //byte
        if(type.equals("byte")){
            return Byte.parseByte(value);
        }
        if(type.equals("java.lang.byte")){
            return Byte.valueOf(value);
        }
        //byte数组
        if(type.equals("[b")){
            return value.getBytes();
        }
        if(type.equals("java.sql.date") || type.equals("java.util.date") || type.equals("java.sql.timestamp") || type.equals("java.sql.time")){
            Date date = null;
            if(value.indexOf(":")>0 && value.indexOf("-")>0){
                date = DateKit.getDate("yyyy-MM-dd HH:mm:ss", value);
            }else
            if(value.indexOf(":")==-1 && value.indexOf("-")>0){
                date = DateKit.getDate("yyyy-MM-dd", value);
            }else
            if(value.indexOf(":")==-1 && value.indexOf("/")>0){
                date = DateKit.getDate("yyyy/MM/dd", value);
            }else
            if(value.indexOf(":")>0 && value.indexOf("/")>0){
                date = DateKit.getDate("yyyy/MM/dd HH:mm:ss", value);
            }else
            if(value.indexOf("年")>0 && value.indexOf("秒")>0){
                date = DateKit.getDate("yyyy年MM月dd日  HH时mm分ss秒", value);
            }else
            if(value.indexOf("年")>0 && value.indexOf("秒")==-1){
                date = DateKit.getDate("yyyy年MM月dd日", value);
            }
            if(date == null){
                try{
                    date = new Date();
                    date.setTime(Long.valueOf(value));
                    return date;
                }catch (Exception e){}
            }
            return date;
        }
        return null;
    }
}
