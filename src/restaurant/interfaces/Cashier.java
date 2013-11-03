package restaurant.interfaces;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Cashier {
	
	// New
	public abstract void calculateCheck(CustomerAgent c, String choice);
	public abstract void acceptPayment(CustomerAgent c, double amountPaid);

}