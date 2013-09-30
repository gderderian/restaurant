package restaurant;

import agent.Agent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.Timer;

/**
 * Restaurant Cook Agent
 */
public class CookAgent extends Agent {
	
	// Variable Declarations
	private String name;
	private List<Order> currentOrders;
	Hashtable<String, Integer> timerList;

	// Accessors
	public CookAgent(String name) {

		super();
		this.name = name;
		currentOrders = new ArrayList<Order>();

		timerList = new Hashtable<String, Integer>();
		timerList.put("Chicken", 3000);
		timerList.put("Mac & Cheese", 2500);
		timerList.put("French Fries", 4000);
		timerList.put("Pizza", 7000);
		timerList.put("Pasta", 6000);
		timerList.put("Cobbler", 5000);
		
	}

	public String getName() {
		return name;
	}

	public List<Order> getOrders() {
		return currentOrders;
	}
	
	// Messages
	public void hereIsOrder(String choice, WaiterAgent waiter, int tableNum) {
		Order o = new Order();
		o.foodItem = choice;
		o.requestingWaiter = waiter;
		o.recipTable = tableNum;
		currentOrders.add(o);
		stateChanged();
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		if (!currentOrders.isEmpty()) {
			for (Order order : currentOrders) {
				if (order.getStatus() == orderStatus.ready) {
					orderDone(order);
					return true;
				} else if (order.getStatus() == orderStatus.waiting){
					prepareFood(order);
					return true;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	// Actions
	private void prepareFood(Order o){ // Begins cooking the specified order and starts a timer based on the food item class' set cooking time
		o.status = orderStatus.preparing;
		o.setCooking(timerList.get(o.getFoodName()));
		Do("Beginning to prepare food " + o.getFoodName() + ".");
	}

	private void orderDone(Order o){ // Tells the specific waiter that their customer's order is done and removes that order from the cook's list of orders
		o.getWaiter().hereIsFood(o.recipTable, o.foodItem);
		currentOrders.remove(o);
		Do("Notifying waiter that " + o.getFoodName() + " is done.");
	}
	
	public enum orderStatus {waiting, preparing, ready};
	
	public class Order {
		
		String foodItem;
		int recipTable;
		WaiterAgent requestingWaiter;
		Timer foodTimer;
		orderStatus status;
		
		public Order(WaiterAgent w){
			requestingWaiter = w;
			status = orderStatus.waiting;
		}
		
		public Order(){
			status = orderStatus.waiting;
		}
		
		public Order(CustomerAgent c, WaiterAgent w, String foodChoice){
			requestingWaiter = w;
			foodItem = foodChoice;
			status = orderStatus.waiting;
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
			          System.out.println("Order has finished cooking!");
			      }
			});
			foodTimer.start();
		}
			
	}

}