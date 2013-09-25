package restaurant;

import agent.Agent;
import restaurant.CustomerAgent.AgentEvent;
import restaurant.CustomerAgent.AgentState;
import restaurant.gui.HostGui;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
public class HostAgent extends Agent {
	
	// Variable Declarations
	static final int NTABLES = 3;
	
	public List<CustomerAgent> waitingCustomers = new ArrayList<CustomerAgent>();
	public List<WaiterAgent> myWaiters = new ArrayList<WaiterAgent>();
	public Collection<Table> tables;

	private String name;
	private Semaphore atTable = new Semaphore(0,true);
	public HostGui hostGui = null;

	public enum AgentState
	{DoingNothing, Working, GoingToDesk, AtDesk, seatingCustomer};
	private AgentState state = AgentState.DoingNothing;
	
	public enum AgentEvent
	{doingNothing, seatedCustomer, WaitingToSeat};
	private AgentEvent event = AgentEvent.doingNothing;
	
	public HostAgent(String name) {
		super();

		this.name = name;
		
		int table_x_start = 200;
		int table_y_start = 250;
		
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix, table_x_start, table_y_start));
			table_x_start = table_x_start + 50;
			table_y_start = table_y_start - 50;
		}
		
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection getTables() {
		return tables;
	}
	
	// Messages
	public void msgIWantFood(CustomerAgent cust) {
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
	}

	public void msgAtTable() {
		print("msgAtTable() called");
		atTable.release();
		stateChanged();
	}
	
	public void msgAtDesk(){
		state = AgentState.AtDesk;
		stateChanged();
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		
		if (state == AgentState.DoingNothing) {
			for (Table table : tables) {
				if (!table.isOccupied()) {
					if (!waitingCustomers.isEmpty()) {
						seatCustomer(waitingCustomers.get(0), table);
						state = AgentState.GoingToDesk;
						return true;
					}
				}
			}
		} else if (state == AgentState.GoingToDesk) {	
			System.out.println("going to desk");
			hostGui.DoLeaveCustomer();
			return true;
		} else if (state == AgentState.AtDesk) {
			System.out.println("at desk in scheduler");
			state = AgentState.DoingNothing;
			return true;
		}
		return false;
	}

	// Actions
	private void givetoWaiter(CustomerAgent c, Table t){
		// Stub - find the least busy waiter in the restaurant and assign this customer (and their table) to it
	}
	
	private void seatCustomer(CustomerAgent customer, Table table) {
		customer.msgSitAtTable(table, waiter, menu);
		DoSeatCustomer(customer, table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
		hostGui.DoLeaveCustomer();
	}

	private void DoSeatCustomer(CustomerAgent customer, Table table) {
		print("Seating " + customer + " at " + table + "x and y of this table:" + table.tableX + " | " + table.tableY);
		hostGui.DoBringToTable(customer, table.tableX, table.tableY);
	}

	// Misc. Utilities
	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}

}

