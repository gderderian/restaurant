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
		System.out.println("Order alive! 1");
		recipientCustomer = c;
		requestingWaiter = w;
		status = orderStatus.waiting;
	}
	
	public Order(CustomerAgent c, WaiterAgent w, String foodChoice){
		System.out.println("Order alive! 2");
		recipientCustomer = c;
		requestingWaiter = w;
		foodItem = foodChoice;
		status = orderStatus.waiting;
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
				new ActionListener() { public void actionPerformed(ActionEvent event) {
		          status = orderStatus.ready;
		          foodTimer.stop();
		          System.out.println("Order has finished");
		      }
		});
		foodTimer.start();
	}
		
}