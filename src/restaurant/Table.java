package restaurant;

public class Table {
	
	int tableNumber;
	int tableX;
	int tableY;
	CustomerAgent occupiedBy;
	
	Table(int tableNumber) {
		this.tableNumber = tableNumber;
	}
	
	Table(int tableNumber, int tableX, int tableY) {
		this.tableNumber = tableNumber;
		this.tableX = tableX;
		this.tableY = tableY;
	}

	void setOccupant(CustomerAgent cust) {
		occupiedBy = cust;
	}

	void setUnoccupied() {
		occupiedBy = null;
	}

	CustomerAgent getOccupant() {
		return occupiedBy;
	}

	boolean isOccupied() {
		return occupiedBy != null;
	}

	public String toString() {
		return "table " + tableNumber;
	}
	
}