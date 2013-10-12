package restaurant;

import agent.Agent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.Timer;

/**
 * Restaurant Cashier Agent
 */
public class CashierAgent extends Agent {
	
	// Variable Declarations
	private String name;
	private List<Check> myChecks;
	private Menu myMenu;
	private double checkAmount;

	// Accessors
	public CashierAgent(String name) {

		super();
		this.name = name;
		myChecks = new ArrayList<Check>();
		Menu myMenu = new Menu();
		checkAmount = 0;
		
	}

	public String getName() {
		return name;
	}
	
	// Messages
	public void calculateCheck(WaiterAgent w, CustomerAgent c, String choice){
		Do("Calculating check for customer");
		Check newCheck = new Check(w, c, choice);
		myChecks.add(newCheck);
		stateChanged();
	}
	
	public void acceptPayment(CustomerAgent c, double amountPaid){
		// Lookup check to mark it as paid
		if (!myChecks.isEmpty()) {
			for (Check check : myChecks) {
				if (check.customer.equals(c)){ // Successful lookup
					processCustomerPayment(c, amountPaid, check);
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
	public void processCheckToWaiter(Check c){
		Do("Processing check back to waiter");
		Menu myMenu = new Menu();
		checkAmount = myMenu.getPriceofItem(c.choice);
		c.amount = checkAmount;
		c.status = checkStatus.calculated;
		WaiterAgent w = c.waiter;
		w.hereIsCheck(c.customer, c.amount);
	}
	
	public void processCustomerPayment(CustomerAgent customer, double amountPaid, Check c){
		Do("Amount paid: $" + amountPaid + " - Amount of item: $" + c.amount);
		if (amountPaid == c.amount){
			Do("Can afford");
			c.status = checkStatus.paid;
		} else if (amountPaid > c.amount){
			Do("Can afford");
			customer.dispenseChange(amountPaid - c.amount);
			c.status = checkStatus.paid;
		} else if (amountPaid < c.amount){
			// customer.makeWorkAtRestaurant();
			Do("Can't afford");
		}
	}
	
	public enum checkStatus {pending, calculated, paid};
	
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

}