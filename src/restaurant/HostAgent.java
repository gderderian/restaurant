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
		waitingCustomers.add(cust);
		stateChanged();
	}

	public void msgLeavingTable(CustomerAgent cust) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				table.setUnoccupied();
				stateChanged();
			}
		}
	}
	
	public void wantBreak(WaiterAgent w){
		Do("Received request to go on break from waiter");
		for (MyWaiter waiter : myWaiters) {
			if (waiter.waiter.equals(w)){
				waiter.state = WaiterState.wantBreak;
			}
		}
		stateChanged();
	}
	
	public void decrementCustomer(WaiterAgent w){
		for (MyWaiter waiter : myWaiters) {
			if (waiter.waiter.equals(w)){
				waiter.numCustomers--;
			}
		}
		stateChanged();
	}
	
	public void returnedFromBreak(WaiterAgent w){
		for (MyWaiter waiter : myWaiters) {
			if (waiter.waiter.equals(w)){
				waiter.state = WaiterState.none;
			}
		}
		stateChanged();
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
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
			}
		}
		return false;
	}

	// Actions
	private void seatCustomer(CustomerAgent customer, Table table) {
		// Find waiter and notify them
		if (myWaiters.size() != 0) {
			int init_cust = myWaiters.get(0).numCustomers;
			MyWaiter w_selected = null;
			for (MyWaiter w : myWaiters){
				if (w.numCustomers <= init_cust && w.isOnBreak() == false){
					init_cust = w.numCustomers;
					w_selected = w;
				}
			}
			Do("Selected Waiter Data: " + w_selected.name);
			Do("Selected Customer Data: " + customer.getName());
			Do("Selected Table Data: " + table.tableNumber);
			w_selected.waiter.msgSeatCustomer(customer, table.tableNumber, this);
			w_selected.numCustomers++;
			table.setOccupant(customer);
			waitingCustomers.remove(customer);
		}
	}
	
	public void processBreakRequest(MyWaiter w){
		int onBreakNow = getNumWaitersOnBreak();
		if (myWaiters.size() <= 1 || (onBreakNow == myWaiters.size() - 1)){ // One waiter also always has to be left!
			Do("Rejecting request for waiter to go on break.");
			w.waiter.breakRejected();
			w.state = WaiterState.none;
		} else {
			Do("Approving request for waite to go on break.");
			w.waiter.breakApproved();
			w.state = WaiterState.onBreak;
		}
	}
	
	public int getNumWaitersOnBreak(){
		int onBreakNow = 0;
		for (MyWaiter w : myWaiters){
			if (w.state == WaiterState.onBreak){
				onBreakNow++;
			}
		}
		return onBreakNow;
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
	
}