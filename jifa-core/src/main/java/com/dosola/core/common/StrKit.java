package com.dosola.core.common;

/**
 * @author june
 * 2015年07月25日 10:56
 */
public class StrKit {
    /**
     * 驼峰字符串改为_分割
     * @return
     */
    public static String camel2underscore(String camelName){
        //先把第一个字母大写
        camelName = capitalize(camelName);

        String regex = "([A-Z][a-z]+)";
        String replacement = "$1_";

        String underscoreName = camelName.replaceAll(regex, replacement);
        //output: Pur_Order_Id_ 接下来把最后一个_去掉，然后全部改小写

        underscoreName = underscoreName.toLowerCase().substring(0, underscoreName.length()-1);

        return underscoreName;
    }

    /**
     * _分割转为驼峰字符串
     * @param underscoreName
     * @return
     */
    public static String underscore2camel(String underscoreName){
        String[] sections = underscoreName.split("_");
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<sections.length;i++){
            String s = sections[i];
            if(i==0){
                sb.append(s);
            }else{
                sb.append(capitalize(s));
            }
        }
        return sb.toString();
    }

    /**
     * 首字母大写
     * @param str
     * @return
     */
    public static String capitalize(String str) {
        if(isEmpty(str)){
            return str;
        }
        return new StringBuilder(str.length())
                .append(Character.toUpperCase(str.charAt(0)))
                .append(str.substring(1))
                .toString();
    }

    /**
     * 首字母小写
     * @param str
     * @return
     */
    public static String lowerCase(String str) {
        if(isEmpty(str)){
            return str;
        }
        return new StringBuilder(str.length())
                .append(Character.toLowerCase(str.charAt(0)))
                .append(str.substring(1))
                .toString();
    }

    /**
     * 判断对象是否为Null或空
     * @param str
     * @return
     */
    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    /**
     * 判断字符串是否不为Null或空
     * @param str
     * @return
     */
    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

    /**
     * 字符串数组转成字符串
     * @param strs [a,b,c]
     * @return a,b,c
     */
    public static String toString(String[] strs){
        StringBuilder sb = new StringBuilder();
        for (String str : strs){
            sb.append(str+",");
        }
        String result = sb.toString();
        return result.substring(0, result.length() - 1);
    }
    /**
     * 转字符串
     * @param obj
     * @return
     */
    public static String toSafeString(Object obj)
    {
        return obj == null ? "" : obj.toString();
    }
    /**
     * 转整型
     * @param obj
     * @return
     */
    public static Integer toSafeInteger(Object obj)
    {
        return obj == null ? 0 : Integer.valueOf(obj.toString());
    }
    /**
     * 转整型
     * @param obj
     * @return
     */
    public static Double toSafeDouble(Object obj)
    {
        return obj == null ? 0.0 : Double.valueOf(obj.toString());
    }
}
