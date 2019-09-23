package View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

import Controller.BookingService;
import Model.BookingServiceDao;
import Model.Day;
import Model.Guest;
import Model.Host;
import Model.Room;
import Model.User;

public class ConsoleUI {
	ArrayList<Room> roomList = new ArrayList<Room>();
	BookingService service = new BookingService();
	Scanner sc = new Scanner(System.in);
	Calendar cal = Calendar.getInstance();
	BookingServiceDao dao = new BookingServiceDao();

	public ConsoleUI() {
		System.out.println("┏━━━━━━━━━━━━━━━━━┓");
		System.out.println("┃                                  ┃");
		System.out.println("┃  ReservationProgram Vesion. 1.0  ┃");
		System.out.println("┃                                  ┃");
		System.out.println("┗━━━━━━━━━━━━━━━━━┛");
		this.start();
	}

	public void start() {
		while (true) {
			if (mainMenu() == 0)
				break;
		}
	}

	public int mainMenu() {
		while (true) {
			try {
				System.out.println();
				System.out.println("====<<메인화면>>====");
				System.out.println("1. 로그인");
				System.out.println("2. 회원가입");
				System.out.println("0. 종료");
				System.out.println("====================");
				System.out.print("번호입력 >> ");

				int sw = sc.nextInt();
				if (sw == 1 || sw == 2 || sw == 0) {
				} else {
					sc.nextLine();
					System.out.println("지정된 숫자를 입력 해주세요.");
					sc.nextLine();
					continue;
				}
				switch (sw) {
				case 1:
					login();
					break;
				case 2:
					join();
					break;
				case 0:
					return sw;
				}
			} catch (InputMismatchException e) {
				sc.nextLine();
				System.out.println("지정된 숫자를 입력 해주세요.");
			} catch (Exception e) {
				sc.nextLine();
				mainMenu();
				System.out.println("오류 발생");
			}
		}
	}

	public void join() {
		Host host = new Host();
		Guest guest = new Guest();

		System.out.println();
		System.out.println("====<<회원등록>>====");

		String id = null;
		while (true) {
			System.out.print("아이디 : ");
			id = sc.next();
			User user = service.findUser(id);
			if (user != null) {
				System.out.println("이미 등록된 아이디입니다.");
			} else
				break;
		}

		System.out.print("비밀번호 : ");
		String pw = sc.next();
		while (true) {
			System.out.print("1. Host, 2. Guest : ");
			int ch = sc.nextInt();
			if (ch == 1) {
				host.setId(id);
				host.setPassword(pw);
				host.setRoom(new Room());
				service.joinService(host);
				System.out.printf("%s님, 회원가입을 환영합니다. 자동으로 로그인됩니다.",id);
				hostMenu(id);
				break;
			} else if (ch == 2) {
				guest.setId(id);
				guest.setPassword(pw);
				guest.setReserveList(new ArrayList<Day>());
				service.joinService(guest);
				System.out.printf("%s님, 회원가입을 환영합니다.자동으로 로그인됩니다.",id);
				guestMenu(id);
				break;
			} else {
				System.out.println("1, 2번 중 하나 선택");
				continue;
			}
		}
	}

	public void login() {
		System.out.println();
		System.out.println("====<<로그인화면>>====");
		System.out.print("아이디 : ");
		String id = sc.next();
		sc.nextLine();
		System.out.print("비밀번호 : ");
		String pw = sc.next();
		sc.nextLine();

		User user = service.findUser(id);

		if (user == null) {
			System.out.println("등록된 아이디가 없습니다.");
			return;
		}

		if (user.getId().equals(id) && user.getPassword().equals(pw)) {
			System.out.printf("**%s님, 환영합니다.**",id);
		} else {
			System.out.println("비밀번호를 다시 입력해주세요.");
			return;
		}

		if (user instanceof Host) {
			
			hostMenu(((Host) user).getId());
		} else if (user instanceof Guest) {
			guestMenu(((Guest) user).getId());
		} else {
			System.out.println("등록된 아이디가 없습니다.");
		}

	}

	public void hostMenu(String id) {
		while (true) {
			System.out.println();
			System.out.println("====<<호스트메뉴>>====");
			System.out.println("1. 숙소등록/수정");
			System.out.println("2. 예약관리");
			System.out.println("3. 마이페이지");
			System.out.println("4. 로그아웃");
			System.out.println("======================");
			System.out.print("번호입력 >> ");
			int sw = 0;
			try {
				sw = sc.nextInt();
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("지정된 숫자를 입력해 주세요.");
				continue;
			}
			switch (sw) {
			case 1:
				Host host = (Host) service.findUser(id);
				if (host.getRoom().getAddress() == null)
					registHouse(id);
				else
					changeHouse(id);
				break;
			case 2:
				reserMgmt(id);
				break;
			case 3:
				myPage(id);
				break;
			case 4:
				return;
			default:
				System.out.println("지정된 숫자를 입력해 주세요.");
				continue;
			}
		}

	}

	public void registHouse(String id) {
		User user = service.findUser(id);
		System.out.println();
		System.out.println("====<<숙소등록>>====");
		System.out.print("주소 : ");
		sc.nextLine();
		String address = sc.nextLine();
		int price = 0;
		while (true) {
			try {
				System.out.print("가격 : ");

				price = sc.nextInt();
				service.registHouseService(new Room(id, address, price), user);
				System.out.println("숙소등록에 성공했습니다.");
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("가격은 숫자로만 입력 할 수 있습니다.");
			}
			break;
		}
	}

	private void changeHouse(String id) {
		Host host = (Host) service.findUser(id);
		System.out.println();
		System.out.println("====<<숙소수정>>====");
		System.out.println(host.getRoom());
		System.out.print("수정할 주소 : ");
		sc.nextLine();
		String address = sc.nextLine();
		System.out.print("수정할 가격 : ");
		while (true) {
			try {
				int price = sc.nextInt();
				service.changeHouseService(id, address, price);
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("가격은 숫자로만 입력 할 수 있습니다.");
			}
			break;
		}
	}

	public void reserMgmt(String id) {
		User user = service.findUser(id);
		Host host = (Host) user;

		if (host.getRoom().getAddress() != null) {
			String won = Currency.getInstance(Locale.KOREA).getSymbol();
			System.out.println();
			System.out.println("====<<예약관리>>====");     ////여기 모양 수정
			System.out.printf("%-10s   %10s  %-20s \n","ID","Price("+won+")", "Address");
			System.out.printf("%-10s %10s %-23s \n","----------","------------", "  ------------------------------");
			System.out.printf("%-10.8s %,10d원   %-20s\n", host.getId(), host.getRoom().getPrice(), host.getRoom().getAddress());
			System.out.printf("%-10s %10s %-23s \n","----------","------------", "  ------------------------------");
			System.out.println();
			service.showCalendar(host);
			System.out.println();
			System.out.println("1. 예약목록");
			System.out.println("2. 예약취소");
			System.out.println("3. 상위메뉴");
			System.out.println("====================");
			System.out.print("번호입력 >> ");
			int ch = sc.nextInt();
			sc.nextLine();
			switch (ch) {
			case 1:
				service.hostPageService(id);
				break;
			case 2:
				this.hostReserveCancel(id);
				break;
			case 3:
				return;
			default:
				System.out.println("지정된 숫자를 입력해 주세요.");
			}

		} else
			System.out.println("등록된 숙소가 없습니다.");
	}

	public void guestMenu(String guestid) {
		while (true) {
			System.out.println();
			System.out.println("====<<메뉴>>====");
			System.out.println("1. 숙소예약");
			System.out.println("2. 예약확인");
			System.out.println("3. 예약취소");
			System.out.println("4. 마이페이지");
			System.out.println("5. 로그아웃");
			System.out.println("================");
			System.out.print("번호입력 >> ");
			int sw = 0;
			try {
				sw = sc.nextInt();
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("지정된 숫자를 입력해 주세요.");
				continue;
			}
			switch (sw) {
			case 1:
				this.searchMenu(guestid);
				break;
			case 2:
				guestPage(guestid);
				break;
			case 3:
				this.guestReserveCancel(guestid);
				break;
			case 4:
				myPage(guestid);
				break;
			case 5:
				return;
			default:
				System.out.println("지정된 숫자를 입력해 주세요.");
			}
		}

	}

	private void searchMenu(String guestid) {
		while (true) {
			System.out.println();
			System.out.println("====<<숙소검색>>====");
			System.out.println("1. 전체숙소리스트");
			System.out.println("2. 상세조건 검색");
			System.out.println("3. 상위메뉴");
			System.out.println("====================");
			System.out.print("번호입력 >> ");
			int ch = 0;
			try {
				ch = sc.nextInt();
				sc.nextLine();
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("지정된 숫자를 입력해 주세요.");
				continue;
			}
			ArrayList<Room>tList = null;
			switch (ch) {
			case 1:
				service.printHouseService(tList); // 전체출력
				this.reserveHouse(guestid);
				break;
			case 2:
				tList = this.reserveDetail(tList,guestid);
				service.printHouseService(tList);
				this.reserveHouse(guestid);
				break;
			case 3:
				return;
			default:
				System.out.println("지정된 숫자를 입력해 주세요.");
			}
		}
	}

	private ArrayList<Room> reserveDetail(ArrayList<Room> tList, String guestid) { // null인 tList 받아옴
		System.out.println();
		System.out.println("===<<상세조건검색>>===");
		String sDay = "";
		String sAddress = "";
		String sPrice = "";
		while (true) {
			//if (sDay == "" && sAddress == "" && sPrice == "")
			//	System.out.println("[현재 선택된 조건] 모든 숙소 ");
			//else System.out.printf("[현재 선택된 조건] %s   %s   %s\n", sDay, sAddress, sPrice);
			System.out.printf("%-10s %s\n","1. 날짜",sDay);
			System.out.printf("%-10s %s\n","2. 지역",sAddress);
			System.out.printf("%-10s %s\n","3. 가격대",sPrice);
			System.out.println("----------------------");
			System.out.println("[[4.검색]]");
			System.out.println("======================");
			System.out.print("번호입력 >> ");			
			int ch = 0;
			try {
				ch = sc.nextInt();
				sc.nextLine();
		} catch (Exception e) {
			sc.nextLine();
			System.out.println("지정된 숫자를 입력해 주세요.");
			continue;
		}
		//ArrayList<Room>tempList = null;
		switch (ch) {
		case 1:
			ArrayList<Object>o1List = this.reserveByDayMenu(tList);
			ArrayList<Room>n1List = new ArrayList<Room>();
			for(int i = 1 ; i < o1List.size() ; i++) {
				n1List.add((Room)o1List.get(i));
			}
			tList =n1List; 
			sDay = (String)o1List.get(0);
			break;
		case 2:
			ArrayList<Object>o2List = this.reserveByAddressMenu(tList);
			ArrayList<Room>n2List = new ArrayList<Room>();
			for(int i = 1 ; i < o2List.size() ; i++) {
				n2List.add((Room)o2List.get(i));
			}
			tList =n2List; 
			sAddress = (String)o2List.get(0);
			break;
		case 3:
			ArrayList<Object>o3List = this.reserveByPriceMenu(tList);
			ArrayList<Room>n3List = new ArrayList<Room>();
			for(int i = 1 ; i < o3List.size() ; i++) {
				n3List.add((Room)o3List.get(i));
			}
			tList =n3List; 
			sPrice = (String)o3List.get(0);
			break;
		case 4 : 
			return tList;
		default:
			System.out.println("지정된 숫자를 입력해 주세요.");
		}
	}
}
	private ArrayList<Object> reserveByAddressMenu(ArrayList<Room>tList) {
		System.out.println("검색할 지역을 입력하세요");
		String selectAddress = sc.next();
		sc.nextLine();
		ArrayList<Room>tempList = service.searchByAdress(tList, selectAddress);
		String sAddress = selectAddress;
		ArrayList<Object>oList = new ArrayList<Object>();
		oList.add(sAddress);
		oList.addAll(tempList);
		return oList;
	}

	private ArrayList<Object> reserveByDayMenu(ArrayList<Room>tList) {
		while(true) {
			System.out.println();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
			System.out.print("예약가능한 날짜 : " + sdf.format(cal.getTime()));
			int tom  = Integer.parseInt(sdf.format(cal.getTime()).toString().substring(7,8)) - 1;
			cal.add(Calendar.DATE, 29);
			System.out.println(" ~ " + sdf.format(cal.getTime()));
			int nxm  = Integer.parseInt(sdf.format(cal.getTime()).toString().substring(7,8)) - 1;
			int nxmld = Integer.parseInt(sdf.format(cal.getTime()).toString().substring(11, 12));
			cal.add(Calendar.DATE, -29);
			
			int month = cal.get(cal.MONTH) + 1;
			int date = cal.get(cal.DATE);
			
				System.out.println("예약 할 첫날짜를 입력해 주세요.");
				System.out.print("월 : ");
				int minmonth = sc.nextInt() - 1;
				System.out.print("일 : ");
				int minday = sc.nextInt() ;
				System.out.println("예약 할 마지막날짜를 입력해 주세요.");
				System.out.print("월 : ");
				int maxmonth =sc.nextInt() - 1;
				System.out.print("일 : ");
				int maxday =sc.nextInt();
		 
			int cha = maxday - minday;
			if (minmonth < maxmonth) {
				cha = cal.getActualMaximum(Calendar.DATE) + maxday - minday;
			}
			//ArrayList<Room>tempList = service.searchByAdress(tList,select);
			ArrayList<Room>tempList = service.searchByDay(tList,minmonth,minday,cha);
			String sDay =  (minmonth+1)+"월" + minday + "일 ~ " + (maxmonth+1)+"월" + maxday + "일" ;
			
			ArrayList<Object>oList = new ArrayList<Object>();
			oList.add(sDay);
			oList.addAll(tempList);
			return oList;
		}
	}

	private ArrayList<Object> reserveByPriceMenu(ArrayList<Room> tList) {
		System.out.println("검색할 가격대를 입력하세요");
		System.out.print("최저금액 >>");
		int first = sc.nextInt();
		sc.nextLine();
		System.out.print("최고금액 >>");
		int second = sc.nextInt();
		sc.nextLine();
		
		ArrayList<Room>tempList = service.searchByPrice(tList, first,second);
		String sPrice = first + "원 ~ " + second + "원";
		ArrayList<Object>oList = new ArrayList<Object>();
		oList.add(sPrice);
		oList.addAll(tempList);
		return oList;
	}

	
	private void reserveHouse(String guestid) {
		while (true) {
			System.out.println("=========================");
			System.out.println("예약할 숙소의 ID를 입력하세요. [뒤로가기(R)] ");
			System.out.print(">>");
			String hostId = sc.next();
			if(service.findUser(hostId) instanceof Guest) {
				System.out.println("등록 돼 있지 않은 숙소 입니다. 다시 입력해주세요.");
				continue;
			}
			if ((Host) service.findUser(hostId) != null) {
				service.showCalendar((Host) service.findUser(hostId));
			} else if(hostId.toUpperCase().contentEquals("R")) {
				return;
			} else {
				System.out.println("등록 돼 있지 않은 숙소 입니다. 다시 입력해주세요.");
				continue;
			}
			// Room selectRoom = service.roomSearch(hostId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
			System.out.print("예약가능한 날짜 : " + sdf.format(cal.getTime()));
			int tom = Integer.parseInt(sdf.format(cal.getTime()).toString().substring(7, 8)) - 1;
			cal.add(Calendar.DATE, 29);
			System.out.println(" ~ " + sdf.format(cal.getTime()));
			int nxm = Integer.parseInt(sdf.format(cal.getTime()).toString().substring(7, 8)) - 1;
			int nxmld = Integer.parseInt(sdf.format(cal.getTime()).toString().substring(11, 12));
			cal.add(Calendar.DATE, -29);

			int month = cal.get(cal.MONTH) + 1;
			int date = cal.get(cal.DATE);

			int minmonth = 0;
			while (true) {
				System.out.println("예약 할 첫날짜를 입력해 주세요. [뒤로가기(0)]");
				System.out.print("월 : ");
				try {
					minmonth = sc.nextInt() - 1;
					if(minmonth<0) return;
				} catch (Exception e) {
					sc.nextLine();
					System.out.println("숫자만 입력 가능 합니다.");// ***
				}
				if (minmonth == tom || minmonth == nxm) {
					break;
				} else {
					System.out.printf("%d월, %d월이 입력 가능합니다.\n", tom + 1, nxm + 1);
				}
			}

			int minday = 0;
			while (true) {
				System.out.print("일 : ");
				try {
					minday = sc.nextInt();
					if(minday == 0) return;
				} catch (Exception e) {
					System.out.println("숫자만 입력 가능 합니다.");// ***
				}
				if (minmonth == tom) {
					if (minday >= date && minday <= cal.getActualMaximum(Calendar.DATE)) {
					} else {
						sc.nextLine();
						System.out.printf("%d일부터 ~ %d일까지 입력 가능 합니다.\n", date, cal.getActualMaximum(Calendar.DATE));
						continue;
					}
				} else {
					if (minday >= 1 && minday <= nxmld) {
					} else {
						sc.nextLine();
						System.out.printf("%d일부터 ~ %d일까지 입력 가능 합니다.\n", 1, nxmld);
						continue;
					}
				}
				break;
			}

			int maxmonth = 0;
			while (true) {
				System.out.println("예약 할 마지막날짜를 입력해 주세요. [뒤로가기(0)]");
				System.out.print("월 : ");
				try {
					maxmonth = sc.nextInt() - 1;
					if(maxmonth<0) return;
				} catch (Exception e) {
					sc.nextLine();
					System.out.println("숫자만 입력 가능 합니다.");
					continue;
				}
				if (minmonth == month - 1 && minday != cal.getActualMaximum(Calendar.DATE)) {
					if (maxmonth == month - 1 || maxmonth == month) {
					} else {
						System.out.printf("%d월, %d월을 입력 가능합니다.\n", month, month + 1);
						continue;
					}
				} else if (minmonth > month - 1) {
					if(maxmonth == month) break;
					System.out.printf("%d월만 입력 가능 합니다.\n", month + 1);
					continue;
				} else if (minday == cal.getActualMaximum(Calendar.DATE)) {
					if (maxmonth == month)
						break;
					System.out.printf("%d월만 입력 가능 합니다.\n", month + 1);
					continue;
				}
				break;
			}

			int maxday = 0;
			while (true) {
				System.out.print("일 : ");
				try {
					maxday = sc.nextInt();
					if(maxday == 0) return;
				} catch (Exception e) {
					sc.nextLine();
					System.out.println("숫자만 입력 가능 합니다.");
					continue;
				}
				
				if (minmonth == maxmonth) {//**
					
					if(minmonth == month) {
						if(maxday > minday && maxday <= nxmld+1) {
							break;
						}else if(minday == nxmld) {
							System.out.printf("%d일만 입력 가능합니다.\n",nxmld+1);
						}else {
							System.out.printf("%d일부터 ~ %d일까지 입력 가능 합니다.\n", minday+1, nxmld);
						}
						continue;
					}
					
					if (maxday > minday && maxday <= cal.getActualMaximum(Calendar.DATE)) {//**
					}else if(minday + 1 == cal.getActualMaximum(Calendar.DATE)) {
						System.out.printf("%d일까지 입력 가능 합니다.\n", cal.getActualMaximum(Calendar.DATE));
						continue;
					}else if(minmonth == month && maxday > nxmld) {
						System.out.printf("%d일부터 ~ %d일까지 입력 가능 합니다.\n", minday+1, nxmld);
						continue;
					}else { ///****
						System.out.printf("%d일부터 ~ %d일까지 입력 가능 합니다.\n", minday + 1,
								cal.getActualMaximum(Calendar.DATE));
						continue;
					}
				} else if(minmonth < maxmonth) {
					if(maxday >= 1 && maxday <= nxmld) {
					}else {
						System.out.printf("%d일부터 ~ %d일까지 입력 가능 합니다.\n", 1, nxmld);
						continue;
					}
				} else if(minday == cal.getActualMaximum(Calendar.DATE)) {
					System.out.printf("%d일부터 ~ %d일까지 입력 가능 합니다.\n", 1, nxmld);
					continue;
				}
					
				break;
			}

			int mind = (minmonth+1) * 100 + minday;
			int maxd = (maxmonth+1) * 100 + maxday;
			if(service.rcheckService(hostId, mind, maxd)) {
				System.out.println("이미 예약된 날짜가 포함 되어 있습니다."); 
				return;
			}
			
			int cha = maxday - minday;
			if (minmonth < maxmonth) {
				cha = cal.getActualMaximum(Calendar.DATE) + maxday - minday;
			}
			String reserNo = hostId.substring(0,1) + (minmonth + 1) + (minday);
			service.reserveService(minmonth, minday, cha, hostId, guestid, reserNo);
			break;
		}

	}

	private void guestPage(String guestid) { 
		System.out.println();
		System.out.println("====<<예약확인>>====");
		service.guestPageService(guestid);
		System.out.println("아무키나 누르면 메뉴로 돌아갑니다.");
		sc.nextLine();
		sc.nextLine();
	}

	private void guestReserveCancel(String id) {
		System.out.println();
		System.out.println("====<<예약취소>>====");
		if(!service.guestPageService(id)) return;
		System.out.println("취소할 예약번호 >> ");
		String select = sc.next();
		sc.nextLine();
		service.ReserveCancelService(select, id);
	}

	private void hostReserveCancel(String id) {
		System.out.println();
		System.out.println("====<<예약취소>>====");
		if(!service.hostPageService(id)) return;
		System.out.println("취소할 예약번호 >> ");
		String select = sc.next();
		sc.nextLine();
		service.ReserveCancelService(select, id);
	}

	private void myPage(String id) {
		while (true) {
			User user = service.findUser(id);
			System.out.println();
			System.out.println("====<<마이페이지>>====");
			System.out.println(user);
			System.out.println("1. 비밀번호 변경"); 
			System.out.println("2. 회원 탈퇴");
			System.out.println("3. 상위메뉴");
			System.out.println("======================");
			System.out.print("번호입력 >> ");
			int ch = 0;
			try {
				ch = sc.nextInt();
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("지정된 숫자를 입력해 주세요.");
				continue;
			}
			switch (ch) {
			case 1:
				System.out.println("변경할 비밀번호를 입력하세요.");
				String pass = sc.next();
				sc.nextLine();
				service.changePass(user, pass);
				break;
			case 2:
				if (service.deleteUser(user))
					this.mainMenu();
				else
					break;
			case 3:
				return;
			default:
				System.out.println("지정된 숫자를 입력해 주세요.");
			}
		}
	}
}
