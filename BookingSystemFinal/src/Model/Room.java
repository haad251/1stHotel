package Model;
import java.util.ArrayList;

public class Room extends Host{
	private int price;
	private String address;
	private ArrayList<Day>dateList = new ArrayList<Day>();	
	
	public Room() {
	}
	
	public Room(String id, String address, int price) {
		super.setId(id);
		this.address = address;
		this.price = price;
		this.dateList = dateList;
	}


	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public ArrayList<Day> getDateList() {
		return dateList;
	}
	public void setDateList(ArrayList<Day> dateList) {
		this.dateList = dateList;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return String.format("주소 : %s 가격 : %d원", address, price);
	}
	
	
	
	
}
