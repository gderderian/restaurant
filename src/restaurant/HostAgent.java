package restaurant;

import agent.Agent;
import restaurant.gui.WaiterGui;

import java.util.*;

/**
 * Restaurant Host Agent
 */
public class HostAgent extends Agent {
	
	static final int NTABLES = 4;
	
	public List<CustomerAgent> waitingCustomers;
	public List<MyWaiter> myWaiters;
	public Collection<Table> tables;

	private String name;
	public WaiterGui hostGui = null;
	String carryingOrderText = "";
	
	public HostAgent(String name) {
		
		super();
		this.name = name;
		
		myWaiters = new ArrayList<MyWaiter>();
		waitingCustomers = new ArrayList<CustomerAgent>();
		
		// Generate all new tables
		tables = new ArrayList<Table>(NTABLES);
		int tableRoot = (int)Math.sqrt(NTABLES);
		int startingCoord = 150;
		int tableDistance = 125;
		
		for (int i = 0; i < tableRoot; i++) {
			for (int j = 0; j < tableRoot; j++){
				int tableNum = tableRoot * i + j + 1;
				int tableX = startingCoord + i*tableDistance;
				int tableY = startingCoord + j*tableDistance;
				tables.add(new Table(tableNum, tableX, tableY));
			}
		}
		
	}
	
	// Messages
	public void msgIWantFood(CustomerAgent cust) {
		Do("Received message msgIWantFood from customer " + cust.getCustomerName() + ".");
		waitingCustomers.add(cust);
		stateChanged();
	}

	public void msgLeavingTable(CustomerAgent cust) {
		Do("Received message msgLeavingTable from customer " + cust.getCustomerName() + ".");
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				table.setUnoccupied();
				stateChanged();
			}
		}
	}
	
	public void wantBreak(WaiterAgent w){
		Do("Received request to go on break from waiter " + w.getName() + ".");
		for (MyWaiter waiter : myWaiters) {
			if (waiter.waiter.equals(w)){
				waiter.state = WaiterState.wantBreak;
			}
		}
		stateChanged();
	}
	
	public void decrementCustomer(WaiterAgent w){
		Do("Received notification one customer left the restaurant.");
		for (MyWaiter waiter : myWaiters) {
			if (waiter.waiter.equals(w)){
				waiter.numCustomers--;
			}
		}
		stateChanged();
	}
	
	public void returnedFromBreak(WaiterAgent w){
		Do("Notified that waiter " + w.getName() + " has now returned from break.");
		for (MyWaiter waiter : myWaiters) {
			if (waiter.waiter.equals(w)){
				waiter.state = WaiterState.none;
			}
		}
		stateChanged();
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		if (!waitingCustomers.isEmpty() && checkAllTablesOccupied() == true) { // Ask customer if they want to stay
			waitingCustomers.get(0).restaurantFull();
			waitingCustomers.remove(0);
			return true;
		}
		for (Table table : tables) {
			if (!table.isOccupied()) {
				if (!waitingCustomers.isEmpty()) {
					seatCustomer(waitingCustomers.get(0), table);
					return true;
				}
			}
		}
		for (MyWaiter waiter : myWaiters) {
			if (waiter.state == WaiterState.wantBreak){
				processBreakRequest(waiter);
				return true;
			}
		}
		return false;
	}

	// Actions
	private void seatCustomer(CustomerAgent customer, Table table) {
		// Find waiter and notify them
		//Do("Seating customer " + customer.getCustomerName() + " at table #" + table.tableNumber + ".");
		if (myWaiters.size() != 0) {
			int init_cust = myWaiters.get(0).numCustomers;
			MyWaiter w_selected = null;
			for (MyWaiter w : myWaiters){
				if (w.numCustomers <= init_cust && w.isOnBreak() == false){
					init_cust = w.numCustomers;
					w_selected = w;
				}
			}
			w_selected.waiter.msgSeatCustomer(customer, table.tableNumber, this);
			w_selected.numCustomers++;
			table.setOccupant(customer);
			waitingCustomers.remove(customer);
		}
	}
	
	public void processBreakRequest(MyWaiter w){
		int onBreakNow = getNumWaitersOnBreak();
		if (myWaiters.size() <= 1 || (onBreakNow == myWaiters.size() - 1)){ // One waiter also always has to be left!
			Do("Rejecting request for waiter " + w.name + " to go on break.");
			w.waiter.breakRejected();
			w.state = WaiterState.none;
		} else {
			Do("Approving request for waiter " + w.name + " to go on break.");
			w.waiter.breakApproved();
			w.state = WaiterState.onBreak;
		}
	}
	
	// Accessors
	public String getName() {
		return name;
	}
	
	public void addWaiter(WaiterAgent w){
		MyWaiter waiter = new MyWaiter();
		waiter.waiter = w;
		waiter.name = w.getName();
		myWaiters.add(waiter);
	}
	
	public void setGui(WaiterGui gui) {
		hostGui = gui;
	}

	public WaiterGui getGui() {
		return hostGui;
	}
	
    public void setCarryText(String carryText){
    	carryingOrderText = carryText;
    }
    
    public Collection<Table> getTables(){
    	return tables;
    }
    
	// Misc. Utilities
	public enum WaiterState // Goes along with MyWaiter below
	{none, wantBreak, onBreak};
	
	class MyWaiter {
		WaiterAgent waiter;
		String name;
		int numCustomers;
		WaiterState state;
	
		MyWaiter(){
			state = WaiterState.none;
			numCustomers = 0;
		}
		
		public boolean isOnBreak(){
			if (state == WaiterState.onBreak){
				return true;
			}
			return false;
		}
		
	}
	
	public int getNumWaitersOnBreak() {
		int onBreakNow = 0;
		for (MyWaiter w : myWaiters){
			if (w.state == WaiterState.onBreak){
				onBreakNow++;
			}
		}
		return onBreakNow;
	}
	
	public boolean checkAllTablesOccupied() {
		int totalOccupied = 0;
		for (Table table : tables) {
			if (table.isOccupied()) {
				totalOccupied++;
			}
		}
		if (totalOccupied == tables.size()){
			return true; // All tables are occupied
		} else {
			return false; // There are still free tables
		}
	}
	
}