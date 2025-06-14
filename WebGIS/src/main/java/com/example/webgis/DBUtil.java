package com.example.webgis;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBUtil {
    public static Connection getConnection() throws Exception {
        Properties props = new Properties();
        InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
        props.load(in);

        String driver = props.getProperty("driver");
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) throws Exception {
        Connection conn = DBUtil.getConnection();
        System.out.println("连接成功：" + (conn != null));
    }
}

