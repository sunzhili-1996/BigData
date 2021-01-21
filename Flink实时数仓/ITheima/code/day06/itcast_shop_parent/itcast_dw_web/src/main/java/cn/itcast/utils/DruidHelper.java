package cn.itcast.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Druid JDBC Helper
 * Created by: mengyao
 * 2019年8月29日
 */
public class DruidHelper {

	private String url = "jdbc:avatica:remote:url=http://node3:8888/druid/v2/sql/avatica/";
	private Properties conf = new Properties();
	private Connection connection;
	
	
	/**
	 * Get Druid SQL broker:8888/
	 * @return
	 */
	public Connection getConnection() {
		try {
			if (null == connection) {
				connection = DriverManager.getConnection(url, conf);				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	/**
	 * clean
	 * @param connection
	 * @param st
	 * @param rs
	 */
	public void close(Connection connection, Statement st, ResultSet rs) {
		try {
			if (rs!=null) {
				rs.close();
			}
			if (st!=null) {
				st.close();
			}
			if (connection!=null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
