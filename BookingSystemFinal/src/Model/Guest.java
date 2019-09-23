package Model;
import java.util.ArrayList;

public class Guest extends User{
	private ArrayList<Day> reserveList = new ArrayList<Day>();

	public Guest() {
		super();
		this.reserveList = reserveList;
	}

	public ArrayList<Day> getReserveList() {
		return reserveList;
	}

	public void setReserveList(ArrayList<Day> reserveList) {
		this.reserveList = reserveList;
	}
	
	@Override
	public String toString() {
		return String.format("ID : %s  Password : %s ",this.getId(),this.getPassword());
	}
	
}
