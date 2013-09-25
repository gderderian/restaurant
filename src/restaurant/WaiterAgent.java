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
	
	public List<MyCustomer> myCustomers;
	public HostAgent myHost;
	public CookAgent myCook;
	private String name;
	public int numCustomers;

	public enum AgentState
	{DoingNothing, waiting, deliveringOrder, acceptingOrder, goingToKitchen, comingToKitchen, atDesk};
	private AgentState state = AgentState.DoingNothing;
	
	public enum AgentEvent
	{doingNothing, deliveredOrder, acceptedOrder};
	private AgentEvent event = AgentEvent.doingNothing;
	
	public WaiterAgent(String name) {
		super();
		this.name = name;
		myCustomers = new ArrayList<MyCustomer>();
	}

	// Accessors
	public String getName() {
		return name;
	}
	
	// Messages
	public void orderDone(Order o) {
		
		// Change state, inform gui to deliver order, tell customer food is done
	}

	public void hereIsFood(Order o) {
		// Tell customer food is done, change state to doingNothing until next task is sent
	}

	public void seatCustomer(CustomerAgent c, Table t, HostAgent h) {
		// Notify customer
		// c.goToTable(myMenu, this);
		myHost = h;
	}
	
	public void readyToOrder(CustomerAgent c) {
		// c.state = waiting;
	}
	
	public void msgAtDesk(){
		state = AgentState.atDesk;
		stateChanged();
	}
	
	public void hereIsMyChoice(String choice) {
		
	}
	
	public void ImDone(CustomerAgent c) {
		myHost.msgLeavingTable(c);
		myCustomers.remove(c);
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		
		if (state == AgentState.DoingNothing) {
			for (MyCustomer c : myCustomers) {
				
			}
			return true;
		
		}
		
		return false;
	
	}

	// Actions
	private void takeOrder(CustomerAgent c, Table t){
		c.msgWhatDoYouWant();
	}

	private void sendToKitchen(Order o){
		
	}
	
	public boolean hasCustomer(CustomerAgent c){
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				return true;
			}
		}
		return false;
	}
	
	public void setCook(CookAgent cook){
		myCook = cook;
	}
	
	// Misc. Utilities
	public enum CustomerState // Goes along with MyCustomer
	{ReadyToOrder, Ordered, Eating, Done};
	
	class MyCustomer {
		CustomerAgent customer;
		Table t;
		Order o;
		CustomerState state;
	}
	
	
}