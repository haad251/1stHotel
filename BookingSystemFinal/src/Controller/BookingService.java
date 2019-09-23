package Controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import Model.BookingServiceDao;
import Model.Day;
import Model.Guest;
import Model.Host;
import Model.Room;
import Model.User;

public class BookingService {
	ArrayList<User> uList = new ArrayList<User>();
	ArrayList<User> tempList = new ArrayList<User>();
	ArrayList<Room> roomList = new ArrayList<Room>();
	Calendar cal = Calendar.getInstance();
	BookingServiceDao dao = new BookingServiceDao();
	String won = Currency.getInstance(Locale.KOREA).getSymbol();

	public BookingService() {
		try {
			this.tempList = dao.loadTempUserList();
			for (User user : tempList) {
				if (user instanceof Host) {
					dao.updateDay(user.getId());
					//hostid로 day(리스트)묶어서 room(주소, 가격) 만들고 host 룸에 추가
					ArrayList<Day> hdList = new ArrayList<Day>();
					hdList = dao.dayCollect(((Host)user).getId());
					Room room = dao.roomCollect(((Host)user).getId());
					for(Day day : hdList) {
						day.setAddress(room.getAddress());
						day.setPrice(room.getPrice());
					}
					room.setDateList(hdList);
					((Host)user).setRoom(room);
					roomList.add(room);
					uList.add(user);
				} else {
					//guestid로 hosid찾고 그 다음에 주소찾고 밑에 추가
					//day테이블에서 guestid로 검색 null아니면 추가 
					ArrayList<Day> rrList = dao.reserveDayCollect(((Guest)user).getId());
					((Guest)user).setReserveList(rrList);
					uList.add(user);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e);
		}

	}

	public void joinService(User user) {
		try {
			dao.joinDao(user);
			if (user instanceof Host) {
				dao.initializeRoom(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e);
		}
		//
		uList.add(user);
		//
	}

	public User findUser(String id) {
		for (User user : uList) {
			if (user.getId().equals(id))
				return user;
		}
		return null;
	}

	public void registHouseService(Room room, User user) {
		ArrayList<Day> dList = new ArrayList<Day>();
		for (int i = 0; i < 30; i++) {
			int tom = cal.get(Calendar.MONTH);
			int tod = cal.get(Calendar.DATE) + i;
			if (tod > cal.getActualMaximum(Calendar.DATE)) {
				tom = tom + 1;
				tod = tod - cal.getActualMaximum(Calendar.DATE);
			}
			Day day2 = new Day(tom, tod);
			dList.add(i, day2);
		}
		room.setDateList(dList);
		roomList.add(room);
		((Host) user).setRoom(room);
		try {
		//	dao.emptyRoomDelete(room);
			dao.registHouseDao(room);
			for (int i = 0; i < dList.size(); i++) {
				dao.intializeDay(room, i);
				dList.get(i).setAddress(room.getAddress());
				dList.get(i).setPrice(room.getPrice());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	public boolean guestPageService(String id) {   
		Guest guest = (Guest) findUser(id);
		try {
			ArrayList<Day> rList = dao.reserveDayCollect(id);
			guest.setReserveList(rList);
			if(rList.size()==0) {
				System.out.println("예약한 숙소가 없습니다"); return false;
			}
			int firstmonth = 0;
			int firstday = 0;
			int count = 0;
			for (int i = 0; i < rList.size(); ) {
				firstmonth = rList.get(i).getMonth() + 1;
				firstday = rList.get(i).getDay();
				for(int j = i ;j<rList.size() ; ) {
					if( j == rList.size()-1) {
						System.out.printf("%d월 %d일 ~ %d월 %d일 (%d박) \n호스트ID : %s 주소 : %s  예약번호 : %s \n", firstmonth, firstday, 
								rList.get(j).getMonth()+1,rList.get(j).getDay()+1, j-i+1, rList.get(i).getId(),
								rList.get(i).getAddress(),rList.get(i).getReserNo());
					i = j;	
					break;
					}else if(!rList.get(i).getReserNo().equals(rList.get(j+1).getReserNo())) {
						System.out.printf("%d월 %d일 ~ %d월 %d일 (%d박) \n호스트ID : %s 주소 : %s  예약번호 : %s \n", firstmonth, firstday, rList.get(j).getMonth()+1,
								rList.get(j).getDay()+1, j-i+1, rList.get(i).getId(),
								rList.get(i).getAddress(),rList.get(i).getReserNo());
					i = j;	
					break;	
					}else j++;
				}
				i++;
			}
			}
		 catch (SQLException e) {
			System.out.println(e);
		}
		return true;
	}
	
	public boolean hostPageService(String id) {   
		Room room = this.roomSearch(id);
		Host host = (Host) findUser(id);
			ArrayList<Day>aList = host.getRoom().getDateList();
			ArrayList<Day>dList = new ArrayList<Day>();
			for(int i = 0 ; i < aList.size() ; i++) {
				if(aList.get(i).getGuestId()!=null) {
					dList.add(aList.get(i));
				} 
			}
			if(dList.size()==0) {System.out.println("예약이 없습니다."); return false;}
			int firstmonth = 0;
			int firstday = 0;
			int count = 0;
			for (int i = 0; i < dList.size(); ) {
				firstmonth = dList.get(i).getMonth() + 1;
				firstday = dList.get(i).getDay();
				for(int j = i ;j<dList.size() ; ) {
					if( j == dList.size()-1) { 
						System.out.printf("%d월 %d일 ~ %d월 %d일 (%d박) \t \n게스트ID : %s 주소 : %s  예약번호 : %s \n", firstmonth, firstday, 
								dList.get(j).getMonth()+1,dList.get(j).getDay()+1, j-i+1, dList.get(i).getGuestId(),
								dList.get(i).getAddress(),dList.get(i).getReserNo());
					i = j;	
					break;
					}else if(!dList.get(i).getReserNo().equals(dList.get(j+1).getReserNo())) {
						System.out.printf("%d월 %d일 ~ %d월 %d일 (%d박) \t \n게스트ID : %s 주소 : %s  예약번호 : %s \n", firstmonth, firstday, dList.get(j).getMonth()+1,
								dList.get(j).getDay()+1, j-i+1, dList.get(i).getGuestId(),
								dList.get(i).getAddress(),dList.get(i).getReserNo());
					i = j;	
					break;	
					}else j++;
				}
				i++;
			}
			return true;
	}
	
	public void reserveService(int minmonth, int minday,int cha,String hostid,String id,String reserNo) {
		boolean flag = false;
		Guest guest = (Guest) this.findUser(id);
		Room room = this.roomSearch(hostid);
		for (int j = 0; j < cha; j++) {
			for (int i = 0; i < room.getDateList().size(); i++) {
				if (room.getDateList().get(i).getMonth() == minmonth
						&& room.getDateList().get(i).getDay() == minday) {
					room.getDateList().get(i).setGuestId(id); 
					room.getDateList().get(i).setReserNo(reserNo);
					try {
						if(dao.guestReserveHouse(id, room.getId(), minmonth, minday,reserNo))
							flag = true;
					} catch (SQLException e) {
						System.out.println(e);
					}
				}
			}
			minday++;
			if (minday > cal.getActualMaximum(Calendar.DATE)) {
				minday = 1;
				minmonth++;
			}
		}
		if(flag == true)System.out.println("예약 성공");
	}


	public void showCalendar(Host host) {
		//ArrayList<Day> tList = host.getRoom().getDateList();//orig1
		
		ArrayList<Day> tList = new ArrayList<Day>();
		try {
			tList = dao.dayCollect(host.getId());
		} catch (SQLException e) {
			System.out.println(e);
		}
		
		Calendar first = Calendar.getInstance();
		Calendar second = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		first.set(first.get(Calendar.YEAR), first.get(Calendar.MONTH), 1);
		second.set(first.get(Calendar.YEAR), first.get(Calendar.MONTH) + 1, 1);
		System.out.printf("%17s<%d월>%36s<%d월>\n", " ", first.get(Calendar.MONTH) + 1, " ",
				first.get(Calendar.MONTH) + 2);
		System.out.printf("%4s%4s%4s%4s%4s%4s%4s%5s%4s%4s%4s%4s%4s%4s%4s\n", "일", "월", "화", "수", "목", "금", "토", " ",
				"일", "월", "화", "수", "목", "금", "토");
		int count1 = 0;
		int count2 = 0;
		int j = 1;
		int k = 1;
		while (true) {
			if (count1 == 0) {
				for (int i = 0; i < first.get(Calendar.DAY_OF_WEEK) - 1; i++) {
					System.out.printf("%5s", " ");
					count1++;
				}
			}

			for (j = j; j <= first.getActualMaximum(Calendar.DATE);) {
				boolean flag = true;
				for (int i = 0; i < tList.size(); i++) {
					if (tList.get(i).getMonth() == first.get(Calendar.MONTH)  && tList.get(i).getDay() == j
							&& tList.get(i).getGuestId() != null) {
						System.out.printf("%5s", "X");
						flag = false;
					}
				}
				if (flag == true) {
					System.out.printf("%5d", j);
				}
				count1++;
				j++;
				if (count1 % 7 == 0) {
					System.out.printf("%5s", " ");
					break;
				}
			}
			if (j > first.getActualMaximum(Calendar.DATE) && k < second.getActualMaximum(Calendar.DATE)) {
				System.out.printf("%40s", " ");
			} else {
				for (int i = first.get(Calendar.DAY_OF_WEEK); i < 7; i++) {
					System.out.printf("%5s", " ");
					count1++;
					j++;
					if (count1 % 7 == 0) {
						System.out.printf("%5s", " ");
						break;
					}
				}
			}
			if (count2 == 0) {
				for (int i = 0; i < second.get(Calendar.DAY_OF_WEEK) - 1; i++) {
					System.out.printf("%5s", " ");
					count2++;
				}
			}
			for (k = k; k <= second.getActualMaximum(Calendar.DATE);) {
				boolean flag2 = true;
				for (Day day : tList) {
					if (day.getMonth() == second.get(Calendar.MONTH)  && day.getDay() == k && day.getGuestId() != null) {
						System.out.printf("%5s", "X");
						flag2 = false;
					}
				}
				if (flag2 == true) {
					System.out.printf("%5d", k);
				}
				count2++;
				k++;
				if (count2 % 7 == 0) {
					System.out.println();
					break;
				}
			}
			if (k > second.getActualMaximum(Calendar.DATE) && j <= first.getActualMaximum(Calendar.DATE)) {
				for (int i = second.get(Calendar.DAY_OF_WEEK); i < 7; i++) {
					System.out.printf("%5s", " ");
					count2++;
					k++;
					if (count2 % 7 == 0) {
						System.out.println();
						break;
					}
				}
			}
			if (k > second.getActualMaximum(Calendar.DATE) && j > first.getActualMaximum(Calendar.DATE)) {
				System.out.println();
				break;
			}
		}
	}

	public Room roomSearch(String hostId) {
		try {
			Room selectRoom = null;
			for (Room room : roomList) {
				if (room.getId().equals(hostId)) {
					selectRoom = room;
				}
			}
			return selectRoom;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public boolean deleteUser(User user) {
		try {
				if (dao.deleteUser(user)) {
					System.out.println("회원을 탈퇴합니다.");
					uList.remove(user);
					if(user instanceof Guest) {
						for(Room room : this.roomList) {
							for(Day day : room.getDateList()) {
								if(user.getId().equals(day.getGuestId())){
									day.setGuestId(null);
								}
							}
						}
					}
					return true;
				} else {
					System.out.println("탈퇴 실패");
					return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void changePass(User user, String pass) {
		try {
			if(dao.changePass(user,pass)) {
				user.setPassword(pass);
				System.out.println("비밀번호를 변경했습니다.");
			}
			else System.out.println("변경 실패");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void changeHouseService(String id, String address, int price) {
		try {
			if(dao.changeHouse(id, address, price)){
				Host host = (Host)this.findUser(id);
				host.getRoom().setAddress(address);
				host.getRoom().setPrice(price);
				System.out.println("숙소정보를 수정했습니다.");
			} else System.out.println("수정 실패");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void ReserveCancelService(String select, String id) {  //입력한번호 없을경우익셉션??
		boolean flag = false;
		try { 
			User user = this.findUser(id);
			if (user instanceof Host) {
				for (Day day : ((Host) user).getRoom().getDateList()) {
					if (day.getReserNo() != null && day.getReserNo().equals(select)) {
						flag = true;
						day.setGuestId(null);
						day.setReserNo(null);
						if (dao.ReserveCancelDao(select))
							System.out.println("예약을 취소했습니다.");
					}
				}
				if (flag == false)
					{System.out.println("해당하는 예약번호가 없습니다.");return;}
			}
			Room room = null;
			if (user instanceof Guest) {
				for (Day day : ((Guest) user).getReserveList()) {
					if (day.getReserNo() != null && day.getReserNo().equals(select)) {
						flag = true;
						day.setGuestId(null);
						day.setReserNo(null);
						room = this.roomSearch(day.getId());
					}
				}
				if (flag == false)
					{System.out.println("해당하는 예약번호가 없습니다.");return;}
				for (Day day : room.getDateList()) {
					if (day.getReserNo() != null && day.getReserNo().equals(select)) {
						day.setGuestId(null);
						day.setReserNo(null);
						if (dao.ReserveCancelDao(select)) {
							System.out.println("예약을 취소했습니다.");
						}
					}
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void printHouseService(ArrayList<Room>tList) {
		ArrayList<Room> tempList = new ArrayList<Room>();
		if(tList == null) {
			tList = this.roomList;
		}
//		System.out.println("====<<검색결과>>===="); 
//		System.out.println("ID\t주소\t가격");
//		for (Room room : tList) {
//			if(room.getAddress() != null) {
//				System.out.println(room.getId() + "\t" + room.getAddress() + "\t" + room.getPrice());
//			}
//		}
		System.out.println();
		System.out.println("====<<검색결과>>===="); 
		System.out.printf("%-10s %10s  %-20s \n","ID","Price("+won+")", "Address");
		System.out.printf("%-10s %10s %-23s \n","----------","------------", "  ------------------------------");
		for (Room room : tList) {
			if(room.getAddress() != null) {
				System.out.printf("%-10.8s %,10d원   %-20s\n", room.getId(), room.getPrice(), room.getAddress());
			}
		}
		System.out.printf("%-10s %10s %-23s \n","----------","------------", "  ------------------------------");
	}
	
	public ArrayList<Room> searchByAdress(ArrayList<Room> tList , String selectAddress) {
		ArrayList<Room> tempList = new ArrayList<Room>();
		if(tList == null) {
			tList = this.roomList;
		}
		for (Room room : tList) {
			if (room.getAddress() != null && room.getAddress().contains(selectAddress)) {
				tempList.add(room);
			}
		}
		return tempList;
	}
	
	
	public ArrayList<Room> searchByDay(ArrayList<Room> tList , int minmonth, int minday, int cha) {
		ArrayList<Room> tempList = new ArrayList<Room>();
		if(tList == null) {
			tList = this.roomList;
		}
		int tmonth = minmonth;
		int tday = minday;

		for (Room room : tList) {
			boolean flag = false;
			minmonth = tmonth;
			minday = tday;
			for (int i = 0; i <= cha; i++) {
				for (Day day : room.getDateList()) {
					if (day.getMonth() == minmonth && day.getDay() == minday && day.getGuestId() != null) {
						flag = true;
						break;
					}
				}
				if (flag == true)break;
					else	{
						minday++;
				if (minday > cal.getActualMaximum(Calendar.DATE))
					minmonth++;
				}
			}
			
			if (flag == false) {
				tempList.add(room);
			}
		}
		return tempList;
	}

	public ArrayList<Room> searchByPrice(ArrayList<Room> tList, int min, int max) {
		ArrayList<Room> tempList = new ArrayList<Room>();
		if(tList == null) {
			tList = this.roomList;
		}
		for (Room room : tList) {
			if (room.getPrice()>= min && room.getPrice()<= max) {
				tempList.add(room);
			}
		}
		return tempList;
	}
	
	public boolean rcheckService(String hostId, int mind, int maxd) {
		ArrayList<String> rcheckList = new ArrayList<String>();
		try {
			rcheckList = dao.rcheckDao(hostId, mind, maxd);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		boolean flag = false;
		for (String string : rcheckList) {
			if(string != null) flag = true;
		}
		return flag;
	}
}



