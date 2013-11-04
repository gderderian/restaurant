package restaurant;

import agent.Agent;

import java.util.*;

import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.MockMarket;

/**
 * Restaurant Cashier Agent
 */
public class CashierAgent extends Agent {
	
	// Variable Declarations
	private String name;
	public List<Check> myChecks;
	private double checkAmount;
	public EventLog log;
	double myMoney;
	
	public CashierAgent(String name) {
		super();
		this.name = name;
		myChecks = Collections.synchronizedList(new ArrayList<Check>());
		checkAmount = 0;
		myMoney = Double.MAX_VALUE;
		log = new EventLog();
	}
	
	// Messages
	public void calculateCheck(Waiter w, Customer customer, String choice){
		Do("Calculating check for customer " + customer.getCustomerName() + " who ordered " + choice + ".");
		Check newCheck = new Check(w, customer, choice); // Add in new check to be calculated for this customer
		myChecks.add(newCheck);
		stateChanged();
	}
	
	public void acceptPayment(Customer c, double amountPaid){
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
	
	public void acceptMarketBill(Market market, double amountDue){
		// Create new check that needs to be paid
		Do("Creating check for market " + market.getName() + " based on incoming bill/message.");
		Check newCheck = new Check(market, (float)amountDue); // Add in new check to be calculated for this market
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
		Waiter w = c.waiter;
		w.hereIsCheck(c.customer, c.amount);
	}
	
	public void processCustomerPayment(Customer customer, double amountPaid, Check c){
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
		Market m = c.getMarket();
		m.acceptCashierPayment(this, c.amount);
		c.status = checkStatus.paid;
	}
	
	// Misc. utilities
	public enum checkStatus {pending, calculated, paid}; // Used in conjunction with Check class below
	public enum checkType {customerCheck, marketCheck}; // Used in conjunction with Check class below
	
	public class Check {
		
		public Customer customer;
		public Waiter waiter;
		public Market market;
		public double amount;
		public checkStatus status;
		public String choice;
		public checkType type;
		
		public Check(float checkAmount){
			amount = checkAmount;
			type = checkType.customerCheck;
		}
		
		public Check(Waiter w, Customer customer2, String ch){
			customer = customer2;
			waiter = w;
			choice = ch;
			status = checkStatus.pending;
			type = checkType.customerCheck;
		}
		
		public Check(Waiter w, CustomerAgent c, String ch, float checkAmount){
			customer = c;
			waiter = w;
			choice = ch;
			status = checkStatus.pending;
			amount = checkAmount;
			type = checkType.customerCheck;
		}
		
		public Check(Market market2, float checkAmount){
			market = market2;
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
		
		public Market getMarket(){
			return market;
		}
		
	}
	
	// Accessors
	public String getName() {
		return name;
	}

}