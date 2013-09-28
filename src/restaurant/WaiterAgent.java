package restaurant;

import agent.Agent;
import restaurant.CustomerAgent.AgentEvent;
import restaurant.CustomerAgent.AgentState;
import restaurant.gui.WaiterGui;

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
	
	public Menu getMenu(){
		return new Menu();
	}
	
	// Messages
	public void doneEating(CustomerAgent c) {
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.state = CustomerState.Done;
			}
		}
		stateChanged();
	}

	public void hereIsFood(Order o) {
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(o.recipientCustomer)){
				cust.state = CustomerState.FoodReady;
			}
		}
		stateChanged();
	}

	public void msgSeatCustomer(CustomerAgent c, Table t, HostAgent h) {
		myHost = h;
		MyCustomer customer = new MyCustomer();
		customer.customer = c;
		customer.table = t;
		myCustomers.add(customer);
		stateChanged();
	}
	
	public void readyToOrder(CustomerAgent c) {
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.state = CustomerState.ReadyToOrder;
			}
		}
		stateChanged();
	}
	
	public void hereIsMyChoice(String choice, CustomerAgent c) {
		Do("Accepted choice");
		Order o = new Order(c, this, choice);
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.order = o;
				cust.state = CustomerState.OrderedWaiting;
			}
		}
		stateChanged();
	}
	
	public void ImDone(CustomerAgent c) {
		Do("Customer " + c.getName() + " has finished and is DONE!");
		myHost.msgLeavingTable(c);
		myCustomers.remove(c);
		stateChanged();
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.Waiting){
				seatCustomer(c);
				return true;
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.ReadyToOrder){
				takeOrder(c, c.table);
				return true;
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.OrderedWaiting){
				Do("Sending " + c.customer.getName() + " order of " + c.order.getFoodName() + " to cook yay");
				sendToKitchen(c, c.order);
				return true;
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.FoodReady){
				deliverOrder(c, c.order);
				return true;
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.Done){
				goodbyeCustomer(c);
				return true;
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
		Do("Order sent to cook!");
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
		c.customer.msgSitAtTable(new Menu(), this);
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