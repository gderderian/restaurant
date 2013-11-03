package restaurant;

import agent.Agent;

import java.util.*;

import restaurant.interfaces.Customer;
import restaurant.test.mock.LoggedEvent;

/**
 * Restaurant Cashier Agent
 */
public class CashierAgent extends Agent {
	
	// Variable Declarations
	private String name;
	public List<Check> myChecks;
	private double checkAmount;
	public ArrayList<LoggedEvent> log;
	double myMoney;
	
	public CashierAgent(String name) {
		super();
		this.name = name;
		myChecks = Collections.synchronizedList(new ArrayList<Check>());
		checkAmount = 0;
		myMoney = Double.MAX_VALUE;
		log = new ArrayList<LoggedEvent>();
	}
	
	// Messages
	public void calculateCheck(WaiterAgent w, Customer customer, String choice){
		Do("Calculating check for customer " + customer.getCustomerName() + " who ordered " + choice + ".");
		Check newCheck = new Check(w, customer, choice); // Add in new check to be calculated for this customer
		myChecks.add(newCheck);
		stateChanged();
	}
	
	public void acceptPayment(CustomerAgent c, double amountPaid){
		Do("Accepting payment of $" + amountPaid + " from customer " + c.getCustomerName() + ".");
		// Lookup check to mark it as paid
		if (!myChecks.isEmpty()) {
			synchronized(myChecks){
				for (Check check : myChecks) {
					if (!check.type.equals(checkType.marketCheck)) {
						if (check.customer.equals(c)){
							processCustomerPayment(c, amountPaid, check); // Process this check with action below
						}
					}
				}
			}
		}
		stateChanged();
	}
	
	public void acceptMarketBill(MarketAgent m, double amountDue){
		// Create new check that needs to be paid
		Do("Creating check for market " + m.getName() + " based on incoming bill/message.");
		Check newCheck = new Check(m, (float)amountDue); // Add in new check to be calculated for this market
		myChecks.add(newCheck);
		stateChanged();
	}

	// Scheduler
	public boolean pickAndExecuteAnAction() {
		if (!myChecks.isEmpty()) {
			synchronized(myChecks){
			for (Check check : myChecks) {
					if (check.getStatus() == checkStatus.pending && check.getType() == checkType.customerCheck) {
						processCheckToWaiter(check);
						return true;
					}
				}
			}
			synchronized(myChecks){
				for (Check check : myChecks) {
					if (check.getStatus() == checkStatus.pending && check.getType() == checkType.marketCheck) {
						payMarket(check);
						return true;
					}
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
			myMoney = myMoney + c.amount;
		} else if (amountPaid > c.amount){ // Customer paid more than their order, dispense the difference to them in change
			customer.dispenseChange(amountPaid - c.amount);
			myMoney = myMoney + c.amount; // Double-test
			c.status = checkStatus.paid;
		} else if (amountPaid < c.amount){ // Customer cannot afford to pay for what they ordered! Send them a shame command.
			customer.goToCorner();
			myMoney = myMoney + amountPaid;
		}
	}
	
	public void payMarket(Check c){ // Complete processing of check
		Do("Completing payment of marketCheck by sending money back to market.");
		MarketAgent m = c.getMarket();
		m.acceptCashierPayment(this, c.amount);
		c.status = checkStatus.paid;
	}
	
	// Misc. utilities
	public enum checkStatus {pending, calculated, paid}; // Used in conjunction with Check class below
	public enum checkType {customerCheck, marketCheck}; // Used in conjunction with Check class below
	
	public class Check {
		
		public Customer customer;
		WaiterAgent waiter;
		MarketAgent market;
		public double amount;
		public checkStatus status;
		String choice;
		public checkType type;
		
		public Check(float checkAmount){
			amount = checkAmount;
			type = checkType.customerCheck;
		}
		
		public Check(WaiterAgent w, Customer customer2, String ch){
			customer = customer2;
			waiter = w;
			choice = ch;
			status = checkStatus.pending;
			type = checkType.customerCheck;
		}
		
		public Check(WaiterAgent w, CustomerAgent c, String ch, float checkAmount){
			customer = c;
			waiter = w;
			choice = ch;
			status = checkStatus.pending;
			amount = checkAmount;
			type = checkType.customerCheck;
		}
		
		public Check(MarketAgent m, float checkAmount){
			market = m;
			status = checkStatus.pending;
			amount = checkAmount;
			type = checkType.marketCheck;
		}
		
		public Check(WaiterAgent w, CustomerAgent c){
			customer = c;
			waiter = w;
			status = checkStatus.pending;
			type = checkType.customerCheck;
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
		
		public checkType getType(){
			return type;
		}
		
		public MarketAgent getMarket(){
			return market;
		}
		
	}
	
	// Accessors
	public String getName() {
		return name;
	}

}