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

	// Accessors
	public CashierAgent(String name) {

		super();
		this.name = name;
		myChecks = new ArrayList<Check>();
		Menu myMenu = new Menu();
		
	}

	public String getName() {
		return name;
	}
	
	// Messages
	public void calculateCheck(WaiterAgent w, CustomerAgent c, String choice){
		double checkAmount = myMenu.getPriceofItem(choice);
		Check newCheck = new Check(w, c, checkAmount);
		myChecks.add(newCheck);
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
		
		
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		if (!myChecks.isEmpty()) {
			for (Check check : myChecks) {
				if (check.getStatus() == checkStatus.pending) {
					giveCheckToWaiter(check);
					return true;
				} else if (check.getStatus() == checkStatus.paid){
					// prepareFood(check);
					return true;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	// Actions
	public void giveCheckToWaiter(Check c){
		WaiterAgent w = c.waiter;
		w.hereIsCheck(c.customer, c.amount);
		c.status = checkStatus.calculated;
	}
	
	public void processCustomerPayment(CustomerAgent customer, double amountPaid, Check c){
		if (amountPaid == c.amount){
			c.status = checkStatus.paid;
		} else if (amountPaid > c.amount){
			customer.dispenseChange(amountPaid - c.amount);
			c.status = checkStatus.paid;
		} else if (amountPaid < c.amount){
			// customer.makeWorkAtRestaurant();
		}
	}
	
	public enum checkStatus {pending, calculated, paid};
	
	public class Check {
		
		CustomerAgent customer;
		WaiterAgent waiter;
		double amount;
		checkStatus status;
		
		public Check(float checkAmount){
			amount = checkAmount;
		}
		
		public Check(WaiterAgent w, CustomerAgent c, double checkAmount){
			customer = c;
			waiter = w;
			amount = checkAmount;
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