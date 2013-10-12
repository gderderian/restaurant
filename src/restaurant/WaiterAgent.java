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
	boolean wantBreak;
	public boolean onBreak;
	public CashierAgent myCashier;
	
	private WaiterGui waiterGui;
	private Semaphore isAnimating = new Semaphore(0,true);
	
	public WaiterAgent(String name) {
		super();
		this.name = name;
		myCustomers = new ArrayList<MyCustomer>();
		wantBreak = false;
		onBreak = false;
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
	
	public void setCook(CookAgent cook){
		myCook = cook;
	}
	
	public void setCashier(CashierAgent cashier){
		myCashier = cashier;
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
	
	public void needNewChoice(int tableNum, String choice) {
		for (MyCustomer cust : myCustomers) {
			if (cust.tableNum == tableNum){
				cust.state = CustomerState.NeedNewChoice;
			}
		}
		stateChanged();
	}
	
	public void breakApproved(){
		onBreak = true;
		wantBreak = false;
	}
	
	public void breakRejected(){
		wantBreak = false;
	}
	
	public void hereIsCheck(CustomerAgent c, double checkAmount){
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.payAmount = checkAmount;
				cust.state = CustomerState.needCheckDelivered;
			}
		}
		stateChanged();
	}
	
	public void readyForCheck(CustomerAgent c){
		Do("Received ready for check message from customer");
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.state = CustomerState.wantCheck;
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
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.NeedNewChoice){
				repickFood(c);
				return true;
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.wantCheck){
				Do("About to call request check function");
				requestCheckForCustomer(c);
				return true;
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.needCheckDelivered){
				deliverCheck(c);
				return true;
			}
		}
		goHome();
		return false;
	}

	// Actions
	private void takeOrder(MyCustomer c, int tableNum){
		Do("Going to take order from customer " + c.customer.getName() + " at table #" + tableNum + ".");
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
		Do("Sending order " + choice + " from customer " + c.customer.getName() + " to kitchen.");
		c.state = CustomerState.WaitingForFood;
		waiterGui.setDestination(500, 230);
		waiterGui.beginAnimate();
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		myCook.hereIsOrder(choice, this, c.tableNum);
	}

	public void seatCustomer(MyCustomer c){
		Do("Seating customer " + c.customer.getName() + ".");
		waiterGui.setDestination(-20, -20);
		waiterGui.beginAnimate();
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
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
		Do("Delivering order " + choice + " to customer " + c.customer.getName() + ".");
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
		Do("Removing customer " + c.customer.getName() + "from my lists and saying goodbye.");
		myCustomers.remove(c);
		c.customer.getHost().msgLeavingTable(c.customer);
	}
	
	private void goHome(){
		Do("Going back to home position as there are no tasks for me to do right now.");
		waiterGui.setDestination(230, 230);
	}
	
	private void repickFood(MyCustomer c){
		Do("Telling the customer they need to repick an item because their previous choice is not in stock (according to cook).");
		Menu newMenu = new Menu();
		newMenu.removeItem(c.choice);
		c.customer.repickFood(newMenu);
		c.state = CustomerState.Ordering;
	}
	
	private void deliverCheck(MyCustomer c){
		Do("DELIVERING CHECK!!!");
		double needToPay = 0;
		needToPay = c.payAmount;
		Do("Delivering check and customer needs to pay $" + needToPay);
		c.customer.hereIsCheck(needToPay);
		c.state = CustomerState.payingCheck;
	}
	
	private void requestCheckForCustomer(MyCustomer c){
		Do("About to request check from cashier");
		myCashier.calculateCheck(this, c.customer, c.choice);
		c.state = CustomerState.waitingForCheck;
	}
	
	// Misc. Utilities
	public enum CustomerState // Goes along with MyCustomer below
	{Waiting, Seated, ReadyToOrder, Ordering, OrderedWaiting, WaitingForFood, FoodReady, Eating, Done, NeedNewChoice, wantCheck, waitingForCheck, payingCheck, needCheckDelivered};
	
	class MyCustomer {
		CustomerAgent customer;
		int tableNum;
		String choice;
		CustomerState state;
		double payAmount;
	
		MyCustomer(){
			state = CustomerState.Waiting;
			payAmount = 0;
		}
		
	}
	
	public void releaseSemaphore(){
		isAnimating.release();
	}
	
	public boolean hasCustomer(CustomerAgent c){
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				return true;
			}
		}
		return false;
	}
	
}