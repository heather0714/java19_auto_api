package com.lemon.constants;

import java.util.HashMap;
import java.util.Map;

public class Constants {
        //相对路径
        // public static final String EXCEL_PATH = Constants.class.getClassLoader().getResource("./cases_v3.xlsx").getPath();

        //绝对路径
        public static final String EXCEL_PATH = System.getProperty("user.dir")+"/src/test/resources/cases_v3.xlsx";
        //public static final String EXCEL_PATH = "D:\\IdeaProjects\\java19_api\\java19_auto_api_v8\\src\\test\\resources\\cases_v3.xlsx";

        //创建一个map用来存请求头  选择静态存，这样所有的case都可以直接调用
        public static final Map<String,String> HEADERS = new HashMap<>();

        //excel 响应回写列 常量 第9列
        public static final int RESPONSE_WRTIE_BACK_CELLNUM = 8;
        //excel 断言回写列 常量 第11列
        public static final int ASSERT_WRTIE_BACK_CELLNUM = 10;

        public static final String JDBC_URL = "jdbc:mysql://api.lemonban.com:3306/futureloan?useUnicode=true&characterEncoding=utf-8";
        //数据库用户名
        public static final String JDBC_USERNAME = "future";
        //数据库密码
        public static final String JDBC_PASSWORD = "123456";


    }

