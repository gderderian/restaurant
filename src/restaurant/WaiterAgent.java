package restaurant;

import agent.Agent;
import restaurant.CustomerAgent.AgentEvent;
import restaurant.CustomerAgent.AgentState;
import restaurant.gui.HostGui;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Waiter Agent
 */

public class WaiterAgent extends Agent {
	
	public List<CustomerAgent> myCustomers
	= new ArrayList<CustomerAgent>();
	
	public Collection<Table> myTables;
	
	private String name;

	public enum AgentState
	{DoingNothing, waiting, deliveringOrder, acceptingOrder, goingToKitchen, comingToKitchen};
	private AgentState state = AgentState.DoingNothing; // The start state
	
	public enum AgentEvent
	{doingNothing, deliveredOrder, acceptedOrder};
	private AgentEvent event = AgentEvent.doingNothing; // The start state 
	
	public WaiterAgent(String name) {
		super();

		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List getCustomers() {
		return myCustomers;
	}

	public Collection getTables() {
		return myTables;
	}
	
	// Messages
	public void orderDone(Order o) {
		// Change state, inform gui to deliver order, tell customer food is done
	}

	public void hereIsFood(Order o) {
		// Tell customer food is done, change state to doingNothing until next task is sent
	}

	public void seatCustomer(CustomerAgent c, Table t) {
		// Add this customer and their table to this waiter's "personal" lists
	}
	
	public void readyToOrder(CustomerAgent c) {
		// c.state = waiting;
	}
	
	public void doneEating(CustomerAgent c) {
		// Clear from lists as customer finishes and leaves restaurant
	}
	
	public void msgAtDesk(){
		state = AgentState.AtDesk;
		stateChanged();
	}
	
	public void hereIsMyChoice(String choice) {
		
	}
	
	public void ImDone(CustomerAgent c) {
		
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		
		if (state == AgentState.DoingNothing) {
		
			// Search through customers and see if any are ready to order since there's nothing else to do right now
			// Then call takeOrder() on any of them
			
			// Also check to see if any customers are done eating in order to set them as done and clear from this
			// waiter's personal lists
			
			return false;
		
		}
		
		
		return true;
	
	}

	// Actions
	private void takeOrder(CustomerAgent c, Table t){
		// Stub - get a customer's order by calling whatDoYouWant() on them
	}

	private void sendToKitchen(Choice c, Order o){
		// Stub - takes the results of WhatDoYouWant() call on the customer and calls cookagent.HereIsOrder(Choice C, Order O)
		//		  Essentially passes the customer's order onto the cook agent after their order has been taken by the waiter.
	}
	
	// Misc. Utilities

	// Table to be made as global class, as well as food/choice
	private class Table {
		CustomerAgent occupiedBy;
		
		int tableNumber;
		int tableX;
		int tableY;

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
}

