package restaurant;

import agent.Agent;
import java.util.*;

/**
 * Restaurant Cashier Agent
 */
public class CashierAgent extends Agent {
	
	// Variable Declarations
	private String name;
	private List<Check> myChecks;
	private double checkAmount;
	
	public CashierAgent(String name) {
		super();
		this.name = name;
		myChecks = new ArrayList<Check>();
		checkAmount = 0;
	}
	
	// Messages
	public void calculateCheck(WaiterAgent w, CustomerAgent c, String choice){
		Do("Calculating check for customer " + c.getCustomerName() + " who ordered " + choice + ".");
		Check newCheck = new Check(w, c, choice); // Add in new check to be calculated for this customer
		myChecks.add(newCheck);
		stateChanged();
	}
	
	public void acceptPayment(CustomerAgent c, double amountPaid){
		Do("Accepting payment of $" + amountPaid + " from customer " + c.getCustomerName() + ".");
		// Lookup check to mark it as paid
		if (!myChecks.isEmpty()) {
			for (Check check : myChecks) {
				if (check.customer.equals(c)){
					processCustomerPayment(c, amountPaid, check); // Process this check with action below
				}
			}
		}
		stateChanged();
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		if (!myChecks.isEmpty()) {
			for (Check check : myChecks) {
				if (check.getStatus() == checkStatus.pending) {
					processCheckToWaiter(check);
					return true;
				}
			}
		}
		return false;
	}

	// Actions
	public void processCheckToWaiter(Check c){ // Mark check as calculated and send back to waiter
		Do("Processing check and sending it back to waiter.");
		Menu myMenu = new Menu();
		checkAmount = myMenu.getPriceofItem(c.choice);
		c.amount = checkAmount;
		c.status = checkStatus.calculated;
		WaiterAgent w = c.waiter;
		w.hereIsCheck(c.customer, c.amount);
	}
	
	public void processCustomerPayment(CustomerAgent customer, double amountPaid, Check c){
		Do("Processing payment of $" + amountPaid + " from customer " + customer.getCustomerName() + ".");
		if (amountPaid == c.amount){ // Customer paid exact amount
			c.status = checkStatus.paid;
		} else if (amountPaid > c.amount){ // Customer paid more than their order, dispense the difference to them in change
			customer.dispenseChange(amountPaid - c.amount);
			c.status = checkStatus.paid;
		} else if (amountPaid < c.amount){ // Customer cannot afford to pay for what they ordered! Send them a shame command.
			customer.goToCorner();
		}
	}
	
	// Misc. utilities
	public enum checkStatus {pending, calculated, paid}; // Used in conjunction with Check class below
	
	public class Check {
		
		CustomerAgent customer;
		WaiterAgent waiter;
		double amount;
		checkStatus status;
		String choice;
		
		public Check(float checkAmount){
			amount = checkAmount;
		}
		
		public Check(WaiterAgent w, CustomerAgent c, String ch){
			customer = c;
			waiter = w;
			choice = ch;
			status = checkStatus.pending;
		}
		
		public Check(WaiterAgent w, CustomerAgent c){
			customer = c;
			waiter = w;
			status = checkStatus.pending;
		}
		
		public void setCustomer(CustomerAgent c){
			customer = c;
		}
		
		public void setWaiter(WaiterAgent w){
			waiter = w;
		}
		
		public checkStatus getStatus(){
			return status;
		}
		
	}
	
	// Accessors
	public String getName() {
		return name;
	}

}