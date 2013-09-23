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
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class HostAgent extends Agent {
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<CustomerAgent> waitingCustomers
	= new ArrayList<CustomerAgent>();
	
	public Collection<Table> tables;
	 
	// v2 prototyping
	public List<Waiters> myWaiters
	= new ArrayList<WaiterAgent>();
	
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	private String name;
	private Semaphore atTable = new Semaphore(0,true);

	public HostGui hostGui = null;

	public enum AgentState
	{DoingNothing, Working, GoingToDesk, AtDesk, seatingCustomer};
	private AgentState state = AgentState.DoingNothing; // The start state
	
	public enum AgentEvent
	{doingNothing, seatedCustomer, WaitingToSeat};
	private AgentEvent event = AgentEvent.doingNothing; // The start state 
	
	//public enum AgentEvent 
	//{none, GoingToDesk};
	//AgentEvent event = AgentEvent.none;
	
	public HostAgent(String name) {
		super();

		this.name = name;
		
		// make some tables
		int table_x_start = 200;
		int table_y_start = 250;
		
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix, table_x_start, table_y_start)); //how you add to a collections
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

	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		atTable.release();// = true;
		stateChanged();
	}
	
	public void msgAtDesk(){
		state = AgentState.AtDesk;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		
		if (state == AgentState.DoingNothing) {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		for (Table table : tables) {
			if (!table.isOccupied()) {
				if (!waitingCustomers.isEmpty()) {
					seatCustomer(waitingCustomers.get(0), table);//the action
					state = AgentState.GoingToDesk;
					return true;//return true to the abstract agent to reinvoke the scheduler.
				}
			}
		}

		return false;
		
		} else if (state == AgentState.GoingToDesk) {
			
			System.out.println("going to desk");
			hostGui.DoLeaveCustomer();
			return true;
			
		} else if (state == AgentState.AtDesk) {
			
			System.out.println("at desk in scheduler");
			state = AgentState.DoingNothing;
			return true;
			
		}
		
		return true;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void givetoWaiter(CustomerAgent c, Table t){
		// Stub - find the least busy waiter in the restaurant and assign this customer (and their table) to it
	}
	
	private void seatCustomer(CustomerAgent customer, Table table) {
		customer.msgSitAtTable(table.tableX, table.tableY);
		DoSeatCustomer(customer, table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
		hostGui.DoLeaveCustomer();
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent customer, Table table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + table + "x and y of this table:" + table.tableX + " | " + table.tableY);
		hostGui.DoBringToTable(customer, table.tableX, table.tableY);

	}

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}

}

