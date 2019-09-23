/*
 * Driver Loading 방법
 * Class.forName("oracle.jdbc.driver.OracleDriver");
	DriverManager.registerDriver(
				new oracle.jdbc.driver.OracleDriver());
	Enumeration<Driver> enums = 
			DriverManager.getDrivers();
	while(enums.hasMoreElements()) {
		System.out.println(enums.nextElement());
	}
 */
package Model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingServiceDao {
	private DBConnection dbconn;

	public BookingServiceDao() { // Constructor
		dbconn = new DBConnection();
	}

	
	public boolean updateDay(String hostid) throws SQLException {
		Calendar cal = Calendar.getInstance();
		Calendar calto = Calendar.getInstance();
		boolean flag = false;
		cal.add(Calendar.DATE, -1);
		Connection conn = this.dbconn.getConnection();
		conn.setAutoCommit(false);
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE day WHERE month = ? and day = ? and hostid = ?");
		PreparedStatement pstmt = conn.prepareStatement(sb.toString());
		pstmt.setInt(1, cal.get(Calendar.MONTH) + 1);
		pstmt.setInt(2, cal.get(Calendar.DATE));
		pstmt.setString(3, hostid);

		cal.add(Calendar.DATE, 30);
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM day WHERE hostid = '" + hostid + "' ";
		ResultSet rs = stmt.executeQuery(sql);
		boolean f1 = true;
		boolean f2 = false;
		
		while (rs.next()) {  //첫날(오늘날짜) 있으면 false
			if(rs.getInt("month") == (calto.get(Calendar.MONTH) + 1) && rs.getInt("day") == calto.get(Calendar.DATE) ) {
				f1 = false;
			}
			 //이미 추가했으면 true
			if(rs.getInt("month") == (cal.get(Calendar.MONTH) + 1) && rs.getInt("day") == cal.get(Calendar.DATE) ) {
				f2 = true;
			}
		}
		
		if (f1==false && f2==false) {
			//System.out.println("if안");
			StringBuffer sb2 = new StringBuffer();
			sb2.append("INSERT INTO day(no,hostid,month,day) VALUES (?,?,?,?)");
			PreparedStatement pstmt2 = conn.prepareStatement(sb2.toString());
			pstmt2.setString(1, hostid + "_" + (String.format("%02d", cal.get(Calendar.MONTH) + 1))
					+ (String.format("%02d", cal.get(Calendar.DATE))));
			pstmt2.setString(2, hostid);
			pstmt2.setInt(3, cal.get(Calendar.MONTH) + 1);
			pstmt2.setInt(4, cal.get(Calendar.DATE));
			pstmt2.executeUpdate();
		}
		int row1 = pstmt.executeUpdate();
	//	if (row1 >= 1 ) {
		flag = true;
		conn.commit();
//		}
		if (pstmt != null) {
			pstmt.close();}
		DBClose.close(conn);
		return flag;
	}

	public ArrayList<User> loadTempUserList() throws SQLException {
		ArrayList<User> tempList = new ArrayList<User>();
		Connection conn = this.dbconn.getConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM buser";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			if(rs.getString("room") != null) {
				User host = new Host();
				host.setId(rs.getString("id"));
				host.setPassword(rs.getString("password"));
				tempList.add(host); 
			}else {
				User guest = new Guest();
				guest.setId(rs.getString("id"));
				guest.setPassword(rs.getString("password"));
				tempList.add(guest);
			}
		}
		if (rs != null)
			rs.close();
		if (stmt != null)
			stmt.close();
		DBClose.close(conn);
		return tempList;
		
	}
	
	public ArrayList<Day> dayCollect(String hostId) throws SQLException {
		ArrayList<Day> hdList = new ArrayList<Day>();
		Connection conn = this.dbconn.getConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM day WHERE hostid = '" + hostId + "' ";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			Day day = new Day();
			day.setId(hostId);
			day.setGuestId(rs.getString("guestid"));
			day.setMonth(rs.getInt("month")-1); //***0
			day.setDay(rs.getInt("day"));
			day.setReserNo(rs.getString("reserNo"));
			hdList.add(day);
		}
		if (rs != null)
			rs.close();
		if (stmt != null)
			stmt.close();
		DBClose.close(conn);
		return hdList;
	}
	
	public Room roomCollect(String hostId) throws SQLException {
		Room room = new Room();
		Connection conn = this.dbconn.getConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM room WHERE id = '" + hostId + "' ";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			room.setId(hostId);
			room.setAddress(rs.getString("address"));
			room.setPrice(rs.getInt("price"));
		}
		if (rs != null)
			rs.close();
		if (stmt != null)
			stmt.close();
		DBClose.close(conn);
		return room;
	}
	
	public ArrayList<Day> reserveDayCollect(String guestId) throws SQLException {
		ArrayList<Day> rrList = new ArrayList<Day>();
		Connection conn = this.dbconn.getConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM day WHERE guestid = '" + guestId + "' ";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			Day day = new Day(rs.getString("hostid"),rs.getInt("month")-1,rs.getInt("day"),rs.getString("reserno"),rs.getString("guestid"));
			day.setId(rs.getString("hostid"));
			day.setAddress(this.addressSearch(rs.getString("hostid"), guestId));
		//	day.setGuestId(rs.getString("guestid"));
		//	day.setMonth(rs.getInt("month"));
		//	day.setDay(rs.getInt("day"));
			rrList.add(day);
		}
		if (rs != null)
			rs.close();
		if (stmt != null)
			stmt.close();
		DBClose.close(conn);
		return rrList;
	}
	
	public String addressSearch(String hostId, String guestId) throws SQLException{
		String address = null;
		Connection conn = this.dbconn.getConnection();
		Statement stmt = conn.createStatement();
		String sql = "SELECT DISTINCT address "
					+  "FROM day INNER JOIN room ON hostid = id "
					+  "WHERE day.hostid = '" + hostId + "' AND guestid = '" + guestId + "' ";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			address = rs.getString("address");
		}
		if (rs != null)
			rs.close();
		if (stmt != null)
			stmt.close();
		DBClose.close(conn);
		return address;
	}

	public boolean joinDao(User user) throws SQLException {
		Connection conn = this.dbconn.getConnection();
		conn.setAutoCommit(false);

		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO buser (id, password, room) VALUES (?, ?, ?)");
		PreparedStatement pstmt = conn.prepareStatement(sb.toString());
		pstmt.setString(1, user.getId());
		pstmt.setString(2, user.getPassword());
		if (user instanceof Host) {
			pstmt.setString(3, "Y"); // Room?
		} else if (user instanceof Guest) {
			pstmt.setString(3, "");
		}

		int row = pstmt.executeUpdate();
		boolean flag = false;
		if (row == 1) {
			flag = true;
			conn.commit();
		}
		if (pstmt != null)
			pstmt.close();
		DBClose.close(conn);
		return flag;
	}

	public boolean initializeRoom(User user) throws SQLException {
		Connection conn = this.dbconn.getConnection();
		// conn.setAutoCommit(false);

		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO room (id, address, price) VALUES (?, ?, ?)");
		PreparedStatement pstmt = conn.prepareStatement(sb.toString());
		pstmt.setString(1, user.getId());
		pstmt.setString(2, ((Host) user).getRoom().getAddress());
		pstmt.setInt(3, ((Host) user).getRoom().getPrice());

		int row = pstmt.executeUpdate();
		boolean flag = false;
		if (row == 1) {
			flag = true;
			conn.commit();
		}
		if (pstmt != null)
			pstmt.close();
		DBClose.close(conn);
		return flag;
	}
	
	public boolean registHouseDao(Room room) throws SQLException {
		Connection conn = this.dbconn.getConnection();
		conn.setAutoCommit(false);
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE room SET address = ?,  price = ? WHERE id = ?");
		PreparedStatement pstmt = conn.prepareStatement(sb.toString());
		pstmt.setString(1, room.getAddress());
		pstmt.setInt(2, room.getPrice());
		pstmt.setString(3, room.getId());

		int row = pstmt.executeUpdate();
		boolean flag = false;
		if (row == 1) {
			flag = true;
			conn.commit();
		}
		if (pstmt != null)
			pstmt.close();
		DBClose.close(conn);
		
		return flag;
	}

	public boolean intializeDay(Room room,int i) throws SQLException {
		boolean flag = false;
		Connection conn = this.dbconn.getConnection();
		conn.setAutoCommit(false);
		StringBuffer sb = new StringBuffer();
		PreparedStatement pstmt = null;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, i);
		sb.append("INSERT INTO day (no, hostid, guestid, month, day) VALUES (?, ?, ?, ?, ?)");
		pstmt = conn.prepareStatement(sb.toString());
		pstmt.setString(1, room.getId() + "_" + (String.format("%02d", cal.get(Calendar.MONTH) + 1))
				+ (String.format("%02d", cal.get(Calendar.DATE))));
		pstmt.setString(2, room.getId());
		pstmt.setString(3, "");
		pstmt.setInt(4, cal.get(Calendar.MONTH) + 1);
		pstmt.setInt(5, cal.get(Calendar.DATE));
		int row = pstmt.executeUpdate();
		if (row == 1) {
			flag = true;
			conn.commit();
		}
		if (pstmt != null)
			pstmt.close();
	DBClose.close(conn);return flag;
	}

	public boolean guestReserveHouse(String id, String hostId, int minmonth, int minday, String reserno) throws SQLException{
		Connection conn = this.dbconn.getConnection();
		conn.setAutoCommit(false);
		StringBuffer sb = new StringBuffer();
		PreparedStatement pstmt = null;
		
		sb.append("UPDATE day SET guestid = ?, reserno = ? WHERE month = ? AND day = ? AND hostid = ?");
		pstmt = conn.prepareStatement(sb.toString());
		pstmt.setString(1, id);
		pstmt.setString(2, reserno);
		pstmt.setInt(3, minmonth + 1);
		pstmt.setInt(4, minday);
		pstmt.setString(5, hostId);

		int row = pstmt.executeUpdate();
		boolean flag = false;
		if (row >= 1) {
			flag = true;
			conn.commit();
		}
		if (pstmt != null)
			pstmt.close();
		DBClose.close(conn);
		return flag;
		
	}
	
		public boolean deleteUser(User user) throws SQLException{
			boolean flag = false;
			Connection conn = this.dbconn.getConnection();
			conn.setAutoCommit(false);
			StringBuffer sb1 = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			StringBuffer sb3 = new StringBuffer();
			sb1.append("DELETE FROM buser WHERE ID = ?");
			sb2.append("DELETE FROM day WHERE HOSTID = ? or GUESTID =?");
			sb3.append("DELETE FROM room WHERE ID = ?");
			
			PreparedStatement pstmt1 = conn.prepareStatement(sb1.toString());
			PreparedStatement pstmt2 = conn.prepareStatement(sb2.toString());
			PreparedStatement pstmt3 = conn.prepareStatement(sb3.toString());
			
			pstmt1.setString(1, user.getId());
			pstmt2.setString(1, user.getId());
			pstmt2.setString(2, user.getId());
			pstmt3.setString(1, user.getId());
			
			int row = pstmt1.executeUpdate();
			pstmt2.executeUpdate();
			pstmt3.executeUpdate();

			if (row == 1) {
			conn.commit();	
			flag = true; 
			}
			if (pstmt1 != null)
				pstmt1.close(); 
			if (pstmt2 != null)
				pstmt2.close(); 
			if (pstmt3 != null)
				pstmt3.close(); 
			DBClose.close(conn);
			return flag;
		}

		public boolean changePass(User user,String pass) throws SQLException{
			boolean flag = false;
			Connection conn = this.dbconn.getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE buser SET password = ? WHERE id = ?");
			PreparedStatement pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, pass);
			pstmt.setString(2, user.getId());
			int row = pstmt.executeUpdate();
			if(row == 1) flag = true;
			if(pstmt != null) pstmt.close();
			DBClose.close(conn);
			return flag;
		}

		public boolean changeHouse(String id, String address, int price) throws SQLException {
			boolean flag = false;
			Connection conn = this.dbconn.getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE room SET address = ?, price = ? WHERE id = ?");
			PreparedStatement pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, address);
			pstmt.setInt(2, price);
			pstmt.setString(3, id);
			int row = pstmt.executeUpdate();
			if(row == 1) flag = true;
			if(pstmt != null) pstmt.close();
			DBClose.close(conn);
			return flag;
			
		}

		public boolean ReserveCancelDao(String select) throws SQLException {
			boolean flag = false;
			Connection conn = this.dbconn.getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE day SET guestid = NULL, reserno = NULL WHERE reserno = ?");
			PreparedStatement pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, select);
			int row = pstmt.executeUpdate();
			if(row >= 1) flag = true;
			if(pstmt != null) pstmt.close();
			DBClose.close(conn);
			return flag;
		}
		
		public ArrayList<String> rcheckDao(String hostId, int mind, int maxd) throws SQLException{
			ArrayList<String> rcheckList = new ArrayList<String>();
			boolean flag = false;
			Connection conn = this.dbconn.getConnection();
			String sql = "{ call sp_rcheck(?,?,?,?) }"; 
			CallableStatement cstmt = conn.prepareCall(sql);
			cstmt.setString(1, hostId);
			cstmt.setInt(2, mind);
			cstmt.setInt(3, maxd);
			cstmt.registerOutParameter(4, oracle.jdbc.OracleTypes.CURSOR);
			cstmt.executeUpdate();
			ResultSet rs = (ResultSet)cstmt.getObject(4);
			if(!rs.next()) rcheckList = null;
			else {
				do {
					String hid = rs.getString("guestid");
					rcheckList.add(hid);
				}while(rs.next());
			}
			if(rs != null) rs.close();
			if(cstmt != null) cstmt.close();
			DBClose.close(conn);
			return rcheckList;
		}
		
		public ArrayList<String> rcheckDao1(String hostId, int mind, int maxd) throws SQLException{
			ArrayList<String> rcheckList = new ArrayList<String>();
			Connection conn = this.dbconn.getConnection();
			Statement stmt = conn.createStatement();
			String sql = "select guestid from day where (month *100) + day between " + mind + " and " + maxd +" and hostid = '"+hostId+"'";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				String guestid = rs.getString("guestid");
				System.out.println(guestid);
				rcheckList.add(guestid);
			}
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			DBClose.close(conn);
			return rcheckList;
		}

}
