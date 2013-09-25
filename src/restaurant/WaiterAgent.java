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
	public void doneEating(CustomerAgent c) {
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.state = CustomerState.Done;
			}
		}
	}

	public void hereIsFood(Order o) {
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(o.recipientCustomer)){
				cust.state = CustomerState.FoodReady;
			}
		}
	}

	public void seatCustomer(CustomerAgent c, Table t, HostAgent h) {
		myHost = h;
		MyCustomer customer = new MyCustomer();
		customer.customer = c;
		customer.table = t;
		customer.state = CustomerState.Waiting;
	}
	
	public void readyToOrder(CustomerAgent c) {
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.state = CustomerState.ReadyToOrder;
			}
		}
	}
	
	public void hereIsMyChoice(String choice, CustomerAgent c) {
		Order o = new Order(c, this, choice);
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.order = o;
				cust.state = CustomerState.OrderedWaiting;
			}
		}
	}
	
	public void ImDone(CustomerAgent c) {
		myHost.msgLeavingTable(c);
		myCustomers.remove(c);
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.Waiting){
				seatCustomer(c);
				stateChanged();
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.ReadyToOrder){
				takeOrder(c, c.table);
				stateChanged();
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.OrderedWaiting){
				sendToKitchen(c, c.order);
				stateChanged();
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.FoodReady){
				deliverOrder(c, c.order);
				stateChanged();
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.Done){
				goodbyeCustomer(c);
				stateChanged();
			}
		}
		return false;
	}

	// Actions
	private void takeOrder(MyCustomer c, Table t){
		c.customer.msgWhatDoYouWant();
		c.state = CustomerState.Ordering;
	}

	private void sendToKitchen(MyCustomer c, Order o){
		myCook.hereIsOrder(o);
		c.state = CustomerState.WaitingForFood;
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
	
	public void seatCustomer(MyCustomer c){
		// Do gui
		c.state = CustomerState.Seated;
	}
	
	public void deliverOrder(MyCustomer c, Order o){
		c.customer.hereIsOrder(o);
		c.state = CustomerState.Eating;
	}
	
	public void goodbyeCustomer(MyCustomer c){
		myCustomers.remove(c);
	}
	
	// Misc. Utilities
	public enum CustomerState // Goes along with MyCustomer
	{Waiting, Seated, ReadyToOrder, Ordering, OrderedWaiting, WaitingForFood, FoodReady, Eating, Done};
	
	class MyCustomer {
		CustomerAgent customer;
		Table table;
		Order order;
		CustomerState state;
	
		MyCustomer(){
			state = CustomerState.Waiting;
		}
		
	}
	
	
}