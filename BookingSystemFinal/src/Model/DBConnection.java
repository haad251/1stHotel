package Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
	private Connection conn;
	private Properties info;
	
	public DBConnection() {  //Constructor
		String path = "config/dbinfo.properties";
		File file = new File(path);
		info = new Properties();
		try {
			info.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public Connection getConnection() {
		//Step 2 : Driver loading
		try {
			Class.forName(this.info.getProperty("db.driverClass"));
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found");
		}
		//Step 3 : Connection
		try {
			this.conn = DriverManager.getConnection(this.info.getProperty("db.url"),
					                                                  this.info.getProperty("db.username"),
					                                                  this.info.getProperty("db.password"));
		} catch (SQLException e) {
			System.out.println("Connection Failure");
		}
		return this.conn;
	}
	
	
	
	
	
}
