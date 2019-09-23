package Model;
public class Day extends Room{
	private int month, day;
	private String reserNo, guestId;

	public Day() {
	
	}

	public Day(int month, int day) {
		this.month = month;
		this.day = day;
	}
	
	public Day(String id, int month, int day, String reserNo, String guestId) {
		
		this.month = month;
		this.day = day;
		this.reserNo = reserNo;
		this.guestId = guestId;
	}

	public Day(String id, String address, int price) {
		super(id, address, price);
	}

	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	
	public String getReserNo() {
		return reserNo;
	}

	public void setReserNo(String reserNo) {
		this.reserNo = reserNo;
	}

	public String getGuestId() {
		return guestId;
	}

	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}

	@Override
	public String toString() {
		return String.format("Day [month=%s, day=%s, reserNo=%s, guestId=%s]", month, day, reserNo, guestId);
	}
	
}
