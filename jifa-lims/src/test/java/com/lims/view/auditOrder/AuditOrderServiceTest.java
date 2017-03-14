package com.lims.view.auditOrder;

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
    }

    public static void modify (String str) {
        str += "world";
    }

    public static void modify (Map<String,Object> map) {
        map.put("aa","aa");
    }
}