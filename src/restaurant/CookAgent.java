package restaurant;

import agent.Agent;
import restaurant.Order.orderStatus;
import java.util.*;
import restaurant.Order;


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
	public void hereIsOrder(Order o) {
		Do("I have order!");
		currentOrders.add(o);
		Do("Order food name: " + o.getFoodName());
		stateChanged();
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		if (!currentOrders.isEmpty()) {
			// Do("Orders not empty");
			for (Order order : currentOrders) {
				// Do("Order status: " + order.status);
				if (order.getStatus() == orderStatus.ready) {
					Do("Order is ready!");
					orderDone(order);
					return true;
				} else if (order.getStatus() == orderStatus.waiting){
					Do("Order is waiting!");
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
		Do("Preparing food!");
		o.status = orderStatus.preparing;
		Do("Order Food Name: " + o.getFoodName());
		o.setCooking(timerList.get(o.getFoodName()));
	}

	private void orderDone(Order o){ // Tells the specific waiter that their customer's order is done and removes that order from the cook's list of orders
		Do("Notifying waiter order is done");
		o.getWaiter().hereIsFood(o);
		currentOrders.remove(o);
	}
	

}