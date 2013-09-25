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
	public List<MyWaiter> myWaiters;
	public Collection<Table> tables;

	private String name;
	public HostGui hostGui = null;

	public enum AgentState
	{DoingNothing, Working, GoingToDesk, AtDesk, seatingCustomer};
	private AgentState state = AgentState.DoingNothing;
	
	public HostAgent(String name) {
		
		super();
		this.name = name;
		
		myWaiters = new ArrayList<MyWaiter>();
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

	// Messages
	public void msgIWantFood(CustomerAgent cust) {
		waitingCustomers.add(cust);
		// Select waiter
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
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		if (state == AgentState.DoingNothing) {
			for (Table table : tables) {
				if (!table.isOccupied()) {
					if (!waitingCustomers.isEmpty()) {
						seatCustomer(waitingCustomers.get(0), table);
						return true;
					}
				}
			}
		}
		return false;
	}

	// Actions
	private void seatCustomer(CustomerAgent customer, Table table) {
		
		// Find waiter and notify them
		
		
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

	class MyWaiter {
		WaiterAgent waiter;
		int customerLoad;
	}
	
}