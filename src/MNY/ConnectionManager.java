package MNY;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

	private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
	private static final String ID = "PROJECT1";
	private static final String PW = "1234";
	
	private ConnectionManager(){}

	 //Connection 생성하여 리턴
	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, ID, PW);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
