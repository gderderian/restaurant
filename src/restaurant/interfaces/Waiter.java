package restaurant.interfaces;

import restaurant.CustomerAgent;
import restaurant.HostAgent;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Waiter {
	
	// New
	public abstract void hereIsCheck(CustomerAgent c, double checkAmount);
	public abstract void readyForCheck(CustomerAgent c);

}