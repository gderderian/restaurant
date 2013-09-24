package restaurant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class Order {
	
	String foodItem;
	public enum orderStatus {waiting, preparing, ready};
	CustomerAgent recipientCustomer;
	WaiterAgent requestingWaiter;
	Timer foodTimer;
	Table forTable;
	orderStatus status;
	
	public Order(CustomerAgent c, WaiterAgent w){
		recipientCustomer = c;
		requestingWaiter = w;
		status = orderStatus.preparing;
	}
	
	public Order(Order o){
		recipientCustomer = o.recipientCustomer;
		requestingWaiter = o.requestingWaiter;
		status = o.status;
		forTable = o.forTable;
	}
	
	public void setPreparing(){
		status = orderStatus.preparing;
	}
	
	public orderStatus getStatus(){
		return status;
	}
	
	public String getFoodName(){
		return foodItem;
	}
	
	public WaiterAgent getWaiter(){
		return requestingWaiter;
	}
	
	public void setCooking(int cookTime){
		foodTimer = new Timer(cookTime,
				new ActionListener() { public void actionPerformed(ActionEvent evt) {
		          status = orderStatus.ready;
		      }
		});
		foodTimer.start();
	}
	
	
}