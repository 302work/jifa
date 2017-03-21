package com.lims.view.auditOrder;

import com.dosola.core.common.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author june
 */
public class AuditOrderServiceTest {


    public static void main(String[] args) {
        for(int i = 1;i<=9;i++){
            for(int j = 1;j<=i;j++){
                System.out.print(j+"*"+i+"="+j*i+"\t");
            }
            System.out.println("");
        }

        String str = "Hello";
        String str2 = new String("Hello");
        modify(str);
        modify(str2);
        System.out.println("Str:"+str);
        System.out.println("Str2:"+str2);

        Map<String,Object> map = new HashMap<>();
        modify(map);
        System.out.println(map);
        String pre = "QDTTC-"+DateUtil.getDateStr(new Date(),"yy");
        String str0 = "0000000";
        int count = 1;
        int count2 = 10;
        int count3 = 111;
        int count4 = 1222;
        int count5 = 12222;
        int count6 = 123222;
        int count7 = 1333333;
        String result = str0.substring(0,str0.length()-((count+"").length()))+count;
        System.out.println("result0:"+result);

        String result2 = str0.substring(0,str0.length()-((count2+"").length()))+count2;
        System.out.println("result2:"+result2);

        String result3 = str0.substring(0,str0.length()-((count3+"").length()))+count3;
        System.out.println("result3:"+result3);

        String result4 = str0.substring(0,str0.length()-((count4+"").length()))+count4;
        System.out.println("result4:"+result4);

        String result5 = str0.substring(0,str0.length()-((count5+"").length()))+count5;
        System.out.println("result5:"+result5);

        String result6 = str0.substring(0,str0.length()-((count6+"").length()))+count6;
        System.out.println("result6:"+result6);

        String result7 = str0.substring(0,str0.length()-((count7+"").length()))+count7;
        System.out.println("result7:"+result7);


    }

    public static void modify (String str) {
        str += "world";
    }

    public static void modify (Map<String,Object> map) {
        map.put("aa","aa");
    }
}