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
	
	public void setHost(HostAgent h){
		myHost = h;
	}
	
	public int getNumCustomers() {
		return myCustomers.size();
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

	public void hereIsFood(int tableNum, String choice) {
		for (MyCustomer cust : myCustomers) {
			if (cust.tableNum == tableNum){
				cust.state = CustomerState.FoodReady;
			}
		}
		stateChanged();
	}

	public void msgSeatCustomer(CustomerAgent c, int tableNum, HostAgent h) {
		myHost = h;
		MyCustomer customer = new MyCustomer();
		customer.customer = c;
		customer.tableNum = tableNum;
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
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.choice = choice;
				cust.state = CustomerState.OrderedWaiting;
			}
		}
		stateChanged();
	}
	
	public void ImDone(CustomerAgent c) {
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
			if (c.state == CustomerState.Waiting){
				seatCustomer(c);
				return true;
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.ReadyToOrder){
				takeOrder(c, c.tableNum);
				return true;
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.OrderedWaiting){
				sendToKitchen(c, c.choice);
				return true;
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.FoodReady){
				deliverOrder(c, c.choice);
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
	private void takeOrder(MyCustomer c, int tableNum){
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

	private void sendToKitchen(MyCustomer c, String choice){
		c.state = CustomerState.WaitingForFood;
		Do("Order sent to cook, headed to kitchen!");
		waiterGui.setDestination(500, 230);
		waiterGui.beginAnimate();
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		myCook.hereIsOrder(choice, this, c.tableNum);
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
		
		int destX = 0, destY = 0;
		
		for (Table t : myHost.getTables()) {
			if (c.tableNum == t.tableNumber){
				destX = t.tableX;
				destY = t.tableY;
			}
		}
		
		c.customer.getGui().setDestination(destX, destY);
		
		waiterGui.setDestination(destX, destY);
		waiterGui.beginAnimate();
		
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		c.state = CustomerState.Seated;
		
	}
	
	public void deliverOrder(MyCustomer c, String choice){
		
		waiterGui.setDestination(500, 230);
		waiterGui.beginAnimate();
		
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String carryText = "";
		
		switch(choice){
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
		waiterGui.setDestination(c.customer.getGui().getX(), c.customer.getGui().getY());
		waiterGui.beginAnimate();
		
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		c.customer.hereIsOrder(choice);
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
		int tableNum;
		String choice;
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