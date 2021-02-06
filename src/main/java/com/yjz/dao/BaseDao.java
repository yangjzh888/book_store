package com.yjz.dao;

import com.yjz.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseDao {

    // 数据库连接句柄
    protected Connection conn;

    // 获取数据库连接
    protected void openConnection(){
        try{
            if (conn == null || conn.isClosed()){
                // 使用反射技术动态加载数据库的驱动
                Class.forName("oracle.jdbc.driver.OracleDriver");
                conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "root", "root");
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            Log.logger.error(throwables.getMessage());
            throwables.printStackTrace();
        }
    }


    public void closeResource(){
        if (conn != null){
            try{
                conn.close();
                conn = null;
            } catch (Exception e){
                Log.logger.error(e.getMessage());
            }
        }
    }


    public void beginTransaction() throws SQLException {
        this.openConnection();
        if (conn != null){
            conn.setAutoCommit(false);
        }
    }


    public void commit() throws SQLException {
        if (conn != null){
            conn.commit();
        }
    }

}
