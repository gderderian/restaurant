package restaurant.test.mock;


import restaurant.CustomerAgent;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

import java.util.*;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockWaiter extends Mock implements Waiter {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Waiter waiter;
	public ArrayList<LoggedEvent> log = new ArrayList<LoggedEvent>();

	public MockWaiter(String name) {
		super(name);

	}
	
	@Override
	public void hereIsCheck(CustomerAgent c, double checkAmount) {
		log.add(new LoggedEvent("Received message hereIsCheck in amount " + checkAmount));
	}
	
	@Override
	public void readyForCheck(CustomerAgent c){
		log.add(new LoggedEvent("Received message readyForCheck."));
	}

}
