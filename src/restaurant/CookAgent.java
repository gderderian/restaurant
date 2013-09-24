package restaurant;

import agent.Agent;

import restaurant.CustomerAgent.AgentEvent;
import restaurant.CustomerAgent.AgentState;
import restaurant.Order.orderStatus;
import restaurant.gui.HostGui;

import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant.Order;
import restaurant.Table;


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
		timerList.put("Lemonade", 1500);
		timerList.put("Water", 1000);
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
		currentOrders.add(new Order(o));
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
				}
			}
		}
		return false;
	}

	// Actions
	private void prepareFood(Order o){ // Begins cooking the specified order and starts a timer based on the food item class' set cooking time
		// Animation
		o.status = orderStatus.preparing;
		o.setCooking(timerList.get(o.getFoodName()));
		stateChanged();
	}

	private void orderDone(Order o){ // Tells the specific waiter that their customer's order is done and removes that order from the cook's list of orders
		o.getWaiter().hereIsFood(o);
		currentOrders.remove(o);
		stateChanged();
	}
	

}