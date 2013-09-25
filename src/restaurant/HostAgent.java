package restaurant;

import agent.Agent;
import restaurant.CustomerAgent.AgentEvent;
import restaurant.CustomerAgent.AgentState;
import restaurant.WaiterAgent.CustomerState;
import restaurant.WaiterAgent.MyCustomer;
import restaurant.gui.HostGui;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
public class HostAgent extends Agent {
	
	// Variable Declarations
	static final int NTABLES = 3;
	
	public List<CustomerAgent> waitingCustomers;
	public List<WaiterAgent> myWaiters;
	public Collection<Table> tables;

	private String name;
	public HostGui hostGui = null;
	
	public HostAgent(String name) {
		
		super();
		this.name = name;
		
		myWaiters = new ArrayList<WaiterAgent>();
		waitingCustomers = new ArrayList<CustomerAgent>();
		
		int table_x_start = 200;
		int table_y_start = 250;
		
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix, table_x_start, table_y_start));
			table_x_start = table_x_start + 50;
			table_y_start = table_y_start - 50;
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
		Do(cust.getName() + " is here and wants food!!!");
		waitingCustomers.add(cust);
		stateChanged();
	}

	public void msgLeavingTable(CustomerAgent cust) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table);
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
	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}
	
}