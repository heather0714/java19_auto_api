package com.lemon.Utils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;


public class SQLUtils {
    public static Object getSingleResult(String sql) {
        if(StringUtils.isBlank(sql)) {
            System.out.println("sql为空");
            return null;
        }
        Object result = null;
        QueryRunner runner = new QueryRunner();
        Connection conn = JDBCUtils.getConnection();
        try {
            ScalarHandler handler = new ScalarHandler();
            result = runner.query(conn, sql, handler);
            System.out.println(result );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn);
        }
        return result;
    }

    public static void mapHandler() {
        QueryRunner runner = new QueryRunner();
        //获取数据库连接
        Connection conn = JDBCUtils.getConnection();
        try {
            String sql = "SELECT * FROM member a where a.mobile_phone = '15670890431'";
            MapHandler handler = new MapHandler();
            Map<String, Object> map = runner.query(conn, sql, handler);
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn);
        }
    }

}