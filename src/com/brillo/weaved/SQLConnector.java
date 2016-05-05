package com.brillo.weaved;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class SQLConnector {
	
	Connection conn;
	public Connection SQLConnectorURLgenerator(String databaseName) throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://127.0.0.1:3306/"+databaseName;
		String user 		= "root";
		String password 	= "root";			
		conn = getConnection(url, user, password);
		return conn;
	}
	
	private Connection getConnection(String url, String user, String password){
		try {
			Connection conn =  DriverManager.getConnection(url,user,password);
			return conn;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
}
