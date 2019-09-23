package Model;
import java.util.ArrayList;

public class Host extends User {
	private Room room;
	
	public Host() {
	}
	
	public Host(String id, String password) {
		super(id, password);
		this.room = room;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	@Override
	public String toString() {
		return String.format("ID : %s  Password : %s ",this.getId(),this.getPassword());
	}
}
