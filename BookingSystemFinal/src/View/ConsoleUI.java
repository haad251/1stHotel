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
		System.out.println("��������������������������������������");
		System.out.println("��                                  ��");
		System.out.println("��  ReservationProgram Vesion. 1.0  ��");
		System.out.println("��                                  ��");
		System.out.println("��������������������������������������");
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
				System.out.println("====<<����ȭ��>>====");
				System.out.println("1. �α���");
				System.out.println("2. ȸ������");
				System.out.println("0. ����");
				System.out.println("====================");
				System.out.print("��ȣ�Է� >> ");

				int sw = sc.nextInt();
				if (sw == 1 || sw == 2 || sw == 0) {
				} else {
					sc.nextLine();
					System.out.println("������ ���ڸ� �Է� ���ּ���.");
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
				System.out.println("������ ���ڸ� �Է� ���ּ���.");
			} catch (Exception e) {
				sc.nextLine();
				mainMenu();
				System.out.println("���� �߻�");
			}
		}
	}

	public void join() {
		Host host = new Host();
		Guest guest = new Guest();

		System.out.println();
		System.out.println("====<<ȸ�����>>====");

		String id = null;
		while (true) {
			System.out.print("���̵� : ");
			id = sc.next();
			User user = service.findUser(id);
			if (user != null) {
				System.out.println("�̹� ��ϵ� ���̵��Դϴ�.");
			} else
				break;
		}

		System.out.print("��й�ȣ : ");
		String pw = sc.next();
		while (true) {
			System.out.print("1. Host, 2. Guest : ");
			int ch = sc.nextInt();
			if (ch == 1) {
				host.setId(id);
				host.setPassword(pw);
				host.setRoom(new Room());
				service.joinService(host);
				System.out.printf("%s��, ȸ�������� ȯ���մϴ�. �ڵ����� �α��ε˴ϴ�.",id);
				hostMenu(id);
				break;
			} else if (ch == 2) {
				guest.setId(id);
				guest.setPassword(pw);
				guest.setReserveList(new ArrayList<Day>());
				service.joinService(guest);
				System.out.printf("%s��, ȸ�������� ȯ���մϴ�.�ڵ����� �α��ε˴ϴ�.",id);
				guestMenu(id);
				break;
			} else {
				System.out.println("1, 2�� �� �ϳ� ����");
				continue;
			}
		}
	}

	public void login() {
		System.out.println();
		System.out.println("====<<�α���ȭ��>>====");
		System.out.print("���̵� : ");
		String id = sc.next();
		sc.nextLine();
		System.out.print("��й�ȣ : ");
		String pw = sc.next();
		sc.nextLine();

		User user = service.findUser(id);

		if (user == null) {
			System.out.println("��ϵ� ���̵� �����ϴ�.");
			return;
		}

		if (user.getId().equals(id) && user.getPassword().equals(pw)) {
			System.out.printf("**%s��, ȯ���մϴ�.**",id);
		} else {
			System.out.println("��й�ȣ�� �ٽ� �Է����ּ���.");
			return;
		}

		if (user instanceof Host) {
			
			hostMenu(((Host) user).getId());
		} else if (user instanceof Guest) {
			guestMenu(((Guest) user).getId());
		} else {
			System.out.println("��ϵ� ���̵� �����ϴ�.");
		}

	}

	public void hostMenu(String id) {
		while (true) {
			System.out.println();
			System.out.println("====<<ȣ��Ʈ�޴�>>====");
			System.out.println("1. ���ҵ��/����");
			System.out.println("2. �������");
			System.out.println("3. ����������");
			System.out.println("4. �α׾ƿ�");
			System.out.println("======================");
			System.out.print("��ȣ�Է� >> ");
			int sw = 0;
			try {
				sw = sc.nextInt();
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("������ ���ڸ� �Է��� �ּ���.");
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
				System.out.println("������ ���ڸ� �Է��� �ּ���.");
				continue;
			}
		}

	}

	public void registHouse(String id) {
		User user = service.findUser(id);
		System.out.println();
		System.out.println("====<<���ҵ��>>====");
		System.out.print("�ּ� : ");
		sc.nextLine();
		String address = sc.nextLine();
		int price = 0;
		while (true) {
			try {
				System.out.print("���� : ");

				price = sc.nextInt();
				service.registHouseService(new Room(id, address, price), user);
				System.out.println("���ҵ�Ͽ� �����߽��ϴ�.");
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("������ ���ڷθ� �Է� �� �� �ֽ��ϴ�.");
			}
			break;
		}
	}

	private void changeHouse(String id) {
		Host host = (Host) service.findUser(id);
		System.out.println();
		System.out.println("====<<���Ҽ���>>====");
		System.out.println(host.getRoom());
		System.out.print("������ �ּ� : ");
		sc.nextLine();
		String address = sc.nextLine();
		System.out.print("������ ���� : ");
		while (true) {
			try {
				int price = sc.nextInt();
				service.changeHouseService(id, address, price);
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("������ ���ڷθ� �Է� �� �� �ֽ��ϴ�.");
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
			System.out.println("====<<�������>>====");     ////���� ��� ����
			System.out.printf("%-10s   %10s  %-20s \n","ID","Price("+won+")", "Address");
			System.out.printf("%-10s %10s %-23s \n","----------","------------", "  ------------------------------");
			System.out.printf("%-10.8s %,10d��   %-20s\n", host.getId(), host.getRoom().getPrice(), host.getRoom().getAddress());
			System.out.printf("%-10s %10s %-23s \n","----------","------------", "  ------------------------------");
			System.out.println();
			service.showCalendar(host);
			System.out.println();
			System.out.println("1. ������");
			System.out.println("2. �������");
			System.out.println("3. �����޴�");
			System.out.println("====================");
			System.out.print("��ȣ�Է� >> ");
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
				System.out.println("������ ���ڸ� �Է��� �ּ���.");
			}

		} else
			System.out.println("��ϵ� ���Ұ� �����ϴ�.");
	}

	public void guestMenu(String guestid) {
		while (true) {
			System.out.println();
			System.out.println("====<<�޴�>>====");
			System.out.println("1. ���ҿ���");
			System.out.println("2. ����Ȯ��");
			System.out.println("3. �������");
			System.out.println("4. ����������");
			System.out.println("5. �α׾ƿ�");
			System.out.println("================");
			System.out.print("��ȣ�Է� >> ");
			int sw = 0;
			try {
				sw = sc.nextInt();
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("������ ���ڸ� �Է��� �ּ���.");
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
				System.out.println("������ ���ڸ� �Է��� �ּ���.");
			}
		}

	}

	private void searchMenu(String guestid) {
		while (true) {
			System.out.println();
			System.out.println("====<<���Ұ˻�>>====");
			System.out.println("1. ��ü���Ҹ���Ʈ");
			System.out.println("2. ������ �˻�");
			System.out.println("3. �����޴�");
			System.out.println("====================");
			System.out.print("��ȣ�Է� >> ");
			int ch = 0;
			try {
				ch = sc.nextInt();
				sc.nextLine();
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("������ ���ڸ� �Է��� �ּ���.");
				continue;
			}
			ArrayList<Room>tList = null;
			switch (ch) {
			case 1:
				service.printHouseService(tList); // ��ü���
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
				System.out.println("������ ���ڸ� �Է��� �ּ���.");
			}
		}
	}

	private ArrayList<Room> reserveDetail(ArrayList<Room> tList, String guestid) { // null�� tList �޾ƿ�
		System.out.println();
		System.out.println("===<<�����ǰ˻�>>===");
		String sDay = "";
		String sAddress = "";
		String sPrice = "";
		while (true) {
			//if (sDay == "" && sAddress == "" && sPrice == "")
			//	System.out.println("[���� ���õ� ����] ��� ���� ");
			//else System.out.printf("[���� ���õ� ����] %s   %s   %s\n", sDay, sAddress, sPrice);
			System.out.printf("%-10s %s\n","1. ��¥",sDay);
			System.out.printf("%-10s %s\n","2. ����",sAddress);
			System.out.printf("%-10s %s\n","3. ���ݴ�",sPrice);
			System.out.println("----------------------");
			System.out.println("[[4.�˻�]]");
			System.out.println("======================");
			System.out.print("��ȣ�Է� >> ");			
			int ch = 0;
			try {
				ch = sc.nextInt();
				sc.nextLine();
		} catch (Exception e) {
			sc.nextLine();
			System.out.println("������ ���ڸ� �Է��� �ּ���.");
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
			System.out.println("������ ���ڸ� �Է��� �ּ���.");
		}
	}
}
	private ArrayList<Object> reserveByAddressMenu(ArrayList<Room>tList) {
		System.out.println("�˻��� ������ �Է��ϼ���");
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy�� MM�� dd��");
			System.out.print("���డ���� ��¥ : " + sdf.format(cal.getTime()));
			int tom  = Integer.parseInt(sdf.format(cal.getTime()).toString().substring(7,8)) - 1;
			cal.add(Calendar.DATE, 29);
			System.out.println(" ~ " + sdf.format(cal.getTime()));
			int nxm  = Integer.parseInt(sdf.format(cal.getTime()).toString().substring(7,8)) - 1;
			int nxmld = Integer.parseInt(sdf.format(cal.getTime()).toString().substring(11, 12));
			cal.add(Calendar.DATE, -29);
			
			int month = cal.get(cal.MONTH) + 1;
			int date = cal.get(cal.DATE);
			
				System.out.println("���� �� ù��¥�� �Է��� �ּ���.");
				System.out.print("�� : ");
				int minmonth = sc.nextInt() - 1;
				System.out.print("�� : ");
				int minday = sc.nextInt() ;
				System.out.println("���� �� ��������¥�� �Է��� �ּ���.");
				System.out.print("�� : ");
				int maxmonth =sc.nextInt() - 1;
				System.out.print("�� : ");
				int maxday =sc.nextInt();
		 
			int cha = maxday - minday;
			if (minmonth < maxmonth) {
				cha = cal.getActualMaximum(Calendar.DATE) + maxday - minday;
			}
			//ArrayList<Room>tempList = service.searchByAdress(tList,select);
			ArrayList<Room>tempList = service.searchByDay(tList,minmonth,minday,cha);
			String sDay =  (minmonth+1)+"��" + minday + "�� ~ " + (maxmonth+1)+"��" + maxday + "��" ;
			
			ArrayList<Object>oList = new ArrayList<Object>();
			oList.add(sDay);
			oList.addAll(tempList);
			return oList;
		}
	}

	private ArrayList<Object> reserveByPriceMenu(ArrayList<Room> tList) {
		System.out.println("�˻��� ���ݴ븦 �Է��ϼ���");
		System.out.print("�����ݾ� >>");
		int first = sc.nextInt();
		sc.nextLine();
		System.out.print("�ְ�ݾ� >>");
		int second = sc.nextInt();
		sc.nextLine();
		
		ArrayList<Room>tempList = service.searchByPrice(tList, first,second);
		String sPrice = first + "�� ~ " + second + "��";
		ArrayList<Object>oList = new ArrayList<Object>();
		oList.add(sPrice);
		oList.addAll(tempList);
		return oList;
	}

	
	private void reserveHouse(String guestid) {
		while (true) {
			System.out.println("=========================");
			System.out.println("������ ������ ID�� �Է��ϼ���. [�ڷΰ���(R)] ");
			System.out.print(">>");
			String hostId = sc.next();
			if(service.findUser(hostId) instanceof Guest) {
				System.out.println("��� �� ���� ���� ���� �Դϴ�. �ٽ� �Է����ּ���.");
				continue;
			}
			if ((Host) service.findUser(hostId) != null) {
				service.showCalendar((Host) service.findUser(hostId));
			} else if(hostId.toUpperCase().contentEquals("R")) {
				return;
			} else {
				System.out.println("��� �� ���� ���� ���� �Դϴ�. �ٽ� �Է����ּ���.");
				continue;
			}
			// Room selectRoom = service.roomSearch(hostId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy�� MM�� dd��");
			System.out.print("���డ���� ��¥ : " + sdf.format(cal.getTime()));
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
				System.out.println("���� �� ù��¥�� �Է��� �ּ���. [�ڷΰ���(0)]");
				System.out.print("�� : ");
				try {
					minmonth = sc.nextInt() - 1;
					if(minmonth<0) return;
				} catch (Exception e) {
					sc.nextLine();
					System.out.println("���ڸ� �Է� ���� �մϴ�.");// ***
				}
				if (minmonth == tom || minmonth == nxm) {
					break;
				} else {
					System.out.printf("%d��, %d���� �Է� �����մϴ�.\n", tom + 1, nxm + 1);
				}
			}

			int minday = 0;
			while (true) {
				System.out.print("�� : ");
				try {
					minday = sc.nextInt();
					if(minday == 0) return;
				} catch (Exception e) {
					System.out.println("���ڸ� �Է� ���� �մϴ�.");// ***
				}
				if (minmonth == tom) {
					if (minday >= date && minday <= cal.getActualMaximum(Calendar.DATE)) {
					} else {
						sc.nextLine();
						System.out.printf("%d�Ϻ��� ~ %d�ϱ��� �Է� ���� �մϴ�.\n", date, cal.getActualMaximum(Calendar.DATE));
						continue;
					}
				} else {
					if (minday >= 1 && minday <= nxmld) {
					} else {
						sc.nextLine();
						System.out.printf("%d�Ϻ��� ~ %d�ϱ��� �Է� ���� �մϴ�.\n", 1, nxmld);
						continue;
					}
				}
				break;
			}

			int maxmonth = 0;
			while (true) {
				System.out.println("���� �� ��������¥�� �Է��� �ּ���. [�ڷΰ���(0)]");
				System.out.print("�� : ");
				try {
					maxmonth = sc.nextInt() - 1;
					if(maxmonth<0) return;
				} catch (Exception e) {
					sc.nextLine();
					System.out.println("���ڸ� �Է� ���� �մϴ�.");
					continue;
				}
				if (minmonth == month - 1 && minday != cal.getActualMaximum(Calendar.DATE)) {
					if (maxmonth == month - 1 || maxmonth == month) {
					} else {
						System.out.printf("%d��, %d���� �Է� �����մϴ�.\n", month, month + 1);
						continue;
					}
				} else if (minmonth > month - 1) {
					if(maxmonth == month) break;
					System.out.printf("%d���� �Է� ���� �մϴ�.\n", month + 1);
					continue;
				} else if (minday == cal.getActualMaximum(Calendar.DATE)) {
					if (maxmonth == month)
						break;
					System.out.printf("%d���� �Է� ���� �մϴ�.\n", month + 1);
					continue;
				}
				break;
			}

			int maxday = 0;
			while (true) {
				System.out.print("�� : ");
				try {
					maxday = sc.nextInt();
					if(maxday == 0) return;
				} catch (Exception e) {
					sc.nextLine();
					System.out.println("���ڸ� �Է� ���� �մϴ�.");
					continue;
				}
				
				if (minmonth == maxmonth) {//**
					
					if(minmonth == month) {
						if(maxday > minday && maxday <= nxmld+1) {
							break;
						}else if(minday == nxmld) {
							System.out.printf("%d�ϸ� �Է� �����մϴ�.\n",nxmld+1);
						}else {
							System.out.printf("%d�Ϻ��� ~ %d�ϱ��� �Է� ���� �մϴ�.\n", minday+1, nxmld);
						}
						continue;
					}
					
					if (maxday > minday && maxday <= cal.getActualMaximum(Calendar.DATE)) {//**
					}else if(minday + 1 == cal.getActualMaximum(Calendar.DATE)) {
						System.out.printf("%d�ϱ��� �Է� ���� �մϴ�.\n", cal.getActualMaximum(Calendar.DATE));
						continue;
					}else if(minmonth == month && maxday > nxmld) {
						System.out.printf("%d�Ϻ��� ~ %d�ϱ��� �Է� ���� �մϴ�.\n", minday+1, nxmld);
						continue;
					}else { ///****
						System.out.printf("%d�Ϻ��� ~ %d�ϱ��� �Է� ���� �մϴ�.\n", minday + 1,
								cal.getActualMaximum(Calendar.DATE));
						continue;
					}
				} else if(minmonth < maxmonth) {
					if(maxday >= 1 && maxday <= nxmld) {
					}else {
						System.out.printf("%d�Ϻ��� ~ %d�ϱ��� �Է� ���� �մϴ�.\n", 1, nxmld);
						continue;
					}
				} else if(minday == cal.getActualMaximum(Calendar.DATE)) {
					System.out.printf("%d�Ϻ��� ~ %d�ϱ��� �Է� ���� �մϴ�.\n", 1, nxmld);
					continue;
				}
					
				break;
			}

			int mind = (minmonth+1) * 100 + minday;
			int maxd = (maxmonth+1) * 100 + maxday;
			if(service.rcheckService(hostId, mind, maxd)) {
				System.out.println("�̹� ����� ��¥�� ���� �Ǿ� �ֽ��ϴ�."); 
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
		System.out.println("====<<����Ȯ��>>====");
		service.guestPageService(guestid);
		System.out.println("�ƹ�Ű�� ������ �޴��� ���ư��ϴ�.");
		sc.nextLine();
		sc.nextLine();
	}

	private void guestReserveCancel(String id) {
		System.out.println();
		System.out.println("====<<�������>>====");
		if(!service.guestPageService(id)) return;
		System.out.println("����� �����ȣ >> ");
		String select = sc.next();
		sc.nextLine();
		service.ReserveCancelService(select, id);
	}

	private void hostReserveCancel(String id) {
		System.out.println();
		System.out.println("====<<�������>>====");
		if(!service.hostPageService(id)) return;
		System.out.println("����� �����ȣ >> ");
		String select = sc.next();
		sc.nextLine();
		service.ReserveCancelService(select, id);
	}

	private void myPage(String id) {
		while (true) {
			User user = service.findUser(id);
			System.out.println();
			System.out.println("====<<����������>>====");
			System.out.println(user);
			System.out.println("1. ��й�ȣ ����"); 
			System.out.println("2. ȸ�� Ż��");
			System.out.println("3. �����޴�");
			System.out.println("======================");
			System.out.print("��ȣ�Է� >> ");
			int ch = 0;
			try {
				ch = sc.nextInt();
			} catch (Exception e) {
				sc.nextLine();
				System.out.println("������ ���ڸ� �Է��� �ּ���.");
				continue;
			}
			switch (ch) {
			case 1:
				System.out.println("������ ��й�ȣ�� �Է��ϼ���.");
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
				System.out.println("������ ���ڸ� �Է��� �ּ���.");
			}
		}
	}
}
