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
	Hashtable<String, Integer> inventoryCount;

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
		
		// Initial inventory
		inventoryCount = new Hashtable<String, Integer>();
		inventoryCount.put("Chicken", 0);
		inventoryCount.put("Mac & Cheese", 0);
		inventoryCount.put("French Fries", 0);
		inventoryCount.put("Pizza", 1);
		inventoryCount.put("Pasta", 1);
		inventoryCount.put("Cobbler", 3);
		
	}

	public String getName() {
		return name;
	}

	public List<Order> getOrders() {
		return currentOrders;
	}
	
	// Messages
	public void hereIsOrder(String choice, WaiterAgent waiter, int tableNum) {
		
		Do("Here is order to cook.");
		
		// Determine if there is enough inventory of this item to fulfill this order
		if (inventoryCount.get(choice) >= 1) { // Able to fulfill order, dock one from that item's inventory
			Order o = new Order();
			o.foodItem = choice;
			o.requestingWaiter = waiter;
			o.recipTable = tableNum;
			currentOrders.add(o);
			stateChanged();
		} else { // Unable to fulfill order, create it and have it marked as bounceback
			Order o = new Order();
			o.foodItem = choice;
			o.requestingWaiter = waiter;
			o.recipTable = tableNum;
			o.status = orderStatus.bounceBack;
			currentOrders.add(o);
			stateChanged();
		}
		
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
				} else if (order.getStatus() == orderStatus.bounceBack) { // Item is out, send choice back to waiter
					orderOut(order);
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
		int foodInventoryCount = inventoryCount.get(o.foodItem);
		inventoryCount.put(o.foodItem, foodInventoryCount - 1); // After preparing, there is one less of this item available
		Do("Beginning to prepare food " + o.getFoodName() + ".");
	}

	private void orderDone(Order o){ // Tells the specific waiter that their customer's order is done and removes that order from the cook's list of orders
		o.getWaiter().hereIsFood(o.recipTable, o.foodItem);
		currentOrders.remove(o);
		Do("Notifying waiter that " + o.getFoodName() + " is done.");
	}
	
	private void orderOut(Order o){ // Tells the specific waiter that their customer's order cannot be fulfilled
		o.getWaiter().needNewChoice(o.recipTable, o.foodItem);
		currentOrders.remove(o);
		Do("Notifying waiter that " + o.getFoodName() + " is out an the customer who ordered it needs to rechoose.");
	}
	
	public enum orderStatus {waiting, preparing, ready, bounceBack};
	
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
			      }
			});
			foodTimer.start();
		}
			
	}

}