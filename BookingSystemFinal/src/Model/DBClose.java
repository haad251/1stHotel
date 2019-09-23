package Model;

import java.sql.Connection;
import java.sql.SQLException;

public class DBClose {
	public static void close(Connection conn) {
		try {
			if(conn != null) conn.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
}
