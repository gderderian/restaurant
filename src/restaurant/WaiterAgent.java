package restaurant;

import agent.Agent;
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
	
	private WaiterGui waiterGui;
	private Semaphore isAnimating = new Semaphore(0,true);
	
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
	
	public void setGui(WaiterGui g){
		waiterGui = g;
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
		Do("Customer " + c.getName() + " has finished and is done.");
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.state = CustomerState.Done;
			}
		}
		stateChanged();
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		for (MyCustomer c : myCustomers) {
			Do("Customer " + c.customer.getCustomerName() + " is " + c.state);
			if (c.state == CustomerState.Waiting){
				seatCustomer(c);
				return true;
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.ReadyToOrder){
				Do("Customer is ready to order!");
				takeOrder(c, c.table);
				return true;
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.OrderedWaiting){
				Do("Sending " + c.customer.getName() + " order of " + c.order.getFoodName() + " to cook");
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
		goHome();
		return false;
	}

	// Actions
	private void takeOrder(MyCustomer c, Table t){

		Do("About to tell waiter to go to x/y:" + c.customer.getGui().getX() + "/" + c.customer.getGui().getY());
		waiterGui.setDestination(c.customer.getGui().getX(), c.customer.getGui().getY());
		waiterGui.beginAnimate();
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.customer.msgWhatDoYouWant();
		c.state = CustomerState.Ordering;
		
	}

	private void sendToKitchen(MyCustomer c, Order o){
		c.state = CustomerState.WaitingForFood;
		Do("Order sent to cook, headed to kitchen!");
		waiterGui.setDestination(500, 230);
		waiterGui.beginAnimate();
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		myCook.hereIsOrder(o);
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
		
		waiterGui.setDestination(-20, -20);
		waiterGui.beginAnimate();
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Do("Going to fetch customer and then set their state");
		
		c.customer.msgSitAtTable(new Menu(), this);
		c.customer.getGui().setDestination(c.table.tableX, c.table.tableY);
		
		waiterGui.setDestination(c.table.tableX, c.table.tableY);
		waiterGui.beginAnimate();
		
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		c.state = CustomerState.Seated;
		
	}
	
	public void deliverOrder(MyCustomer c, Order o){
		
		waiterGui.setDestination(500, 230);
		waiterGui.beginAnimate();
		
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String carryText = "";
		
		switch(o.getFoodName()){
		case "Chicken":
			carryText = "CHK";
			break;
		case "Mac & Cheese":
			carryText = "M&C";
			break;
		case "French Fries":
			carryText = "FRF";
			break;
		case "Pizza":
			carryText = "PZA";
			break;
		case "Pasta":
			carryText = "PST";
			break;
		case "Cobbler":
			carryText = "CBL";
			break;
		}
		
		waiterGui.setCarryText(carryText);
		waiterGui.setDestination(c.table.tableX, c.table.tableY);
		waiterGui.beginAnimate();
		
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		c.customer.hereIsOrder(o);
		c.state = CustomerState.Eating;
		waiterGui.setCarryText("");
		
	}
	
	public void goodbyeCustomer(MyCustomer c){
		myCustomers.remove(c);
		c.customer.getHost().msgLeavingTable(c.customer);
	}
	
	private void goHome(){
		waiterGui.setDestination(230, 230);
	}
	
	// Misc. Utilities
	public enum CustomerState // Goes along with MyCustomer below
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
	
	public void releaseSemaphore(){
		//System.out.println("Releasing semaphore");
		isAnimating.release();
	}
	
}