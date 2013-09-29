package restaurant;

import agent.Agent;
import restaurant.gui.WaiterGui;

import java.util.*;

/**
 * Restaurant Host Agent
 */
public class HostAgent extends Agent {
	
	// Variable Declarations
	static final int NTABLES = 4;
	
	public List<CustomerAgent> waitingCustomers;
	public List<WaiterAgent> myWaiters;
	public Collection<Table> tables;

	private String name;
	public WaiterGui hostGui = null;
	String carryingOrderText = "";
	
	public HostAgent(String name) {
		
		super();
		this.name = name;
		
		myWaiters = new ArrayList<WaiterAgent>();
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
				// System.out.println("Adding in New Table: x:" + tableX + " - y:" + tableY + " - #" + tableNum);
				tables.add(new Table(tableNum, tableX, tableY));
			}
		}
		
	}

	// Accessors
	public String getName() {
		return name;
	}
	
	public void addWaiter(WaiterAgent w){
		myWaiters.add(w);
	}

	// Messages
	public void msgIWantFood(CustomerAgent cust) {
		Do(cust.getName() + " is here and wants food.");
		waitingCustomers.add(cust);
		stateChanged();
	}

	public void msgLeavingTable(CustomerAgent cust) {
		
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table + " - setting as unoccupied");
				table.setUnoccupied();
				stateChanged();
			}
		}
		
		for (WaiterAgent waiter : myWaiters) {
			if (waiter.hasCustomer(cust) == true){
				waiter.numCustomers--;
			}
		}
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
		return false;
	}

	// Actions
	private void seatCustomer(CustomerAgent customer, Table table) {
		// Find waiter and notify them
		int init_cust = myWaiters.get(0).numCustomers;
		WaiterAgent w_selected = null;
		for (WaiterAgent w : myWaiters){
			if (w.numCustomers <= init_cust){
				init_cust = w.numCustomers;
				w_selected = w;
			}
		}
		w_selected.msgSeatCustomer(customer, table, this);
		w_selected.numCustomers++;
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
	}

	// Misc. Utilities
	public void setGui(WaiterGui gui) {
		hostGui = gui;
	}

	public WaiterGui getGui() {
		return hostGui;
	}
	
    public void setCarryText(String carryText){
    	carryingOrderText = carryText;
    }
	
}