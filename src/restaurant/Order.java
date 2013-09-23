package restaurant;

import java.util.Timer;

public class Order {
	
	String foodItem;
	public enum orderStatus {waiting, preparing, ready};
	CustomerAgent recipientCustomer;
	WaiterAgent requestingWaiter;
	Timer foodTimer;
	Table forTable;
	orderStatus status;
	
	public Order(){
		// Default constructor with nothing specified
	}
	
	public Order(CustomerAgent c, WaiterAgent w){
		recipientCustomer = c;
		requestingWaiter = w;
		status = orderStatus.preparing;
	}
	
}