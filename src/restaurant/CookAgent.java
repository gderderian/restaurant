package restaurant;

import agent.Agent;
import restaurant.CustomerAgent.AgentEvent;
import restaurant.CustomerAgent.AgentState;
import restaurant.gui.HostGui;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Cook Agent
 */

public class CookAgent extends Agent {
	
	public List<CustomerAgent> currentOrders
	= new ArrayList<CustomerAgent>();
	
	public Collection<Order> myOrders;
	
	private String name;

	public enum AgentState
	{DoingNothing, preparingOrder};
	private AgentState state = AgentState.DoingNothing; // The start state
	
	public CookAgent(String name) {
		super();

		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List getOrders() {
		return currentOrders;
	}
	
	// Messages
	public void hereIsOrder(WaiterAgent w, String choice) {
		// Accepts order and places it on this (cook) agent's waiting orders list
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		
		if (state == AgentState.DoingNothing) {
		
			// Search through all orders and prepares orders by starting their timer on each one
			// Only cooks one order at a time though.
			
			return false;
		
		} (state == AgentState.preparingOrder) {
			// Check to see if order is done. If it is, call orderDone();
		}
		
		
		return true;
	
	}

	// Actions
	private void hereIsOrder(Order o, CustomerAgent c){
		// Begins cooking the specified order and starts a timer based on the food item class' set cooking time
	}

	private void orderDone(Order o){
		// Tells the specific waiter that their order is done and removes that order from the cook's list of orders
	}
	
	// Misc. Utilities

} // end of cook class

// Order and food classes
class Order {
	CustomerAgent orderer;
	Food foodChoice;
	int forTable;
	public enum orderStatus {Pending, Cooking, Done};
}

class Food {
	String foodName;
	int cookingTime;
}
