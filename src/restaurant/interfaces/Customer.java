package restaurant.interfaces;

import restaurant.WaiterAgent;
import restaurant.Menu;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {
	
	// New
	public abstract void dispenseChange(double newMoney);
	public abstract void hereIsCheck(double amountDue);

}