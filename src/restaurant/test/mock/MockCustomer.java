package restaurant.test.mock;


import java.util.ArrayList;

import restaurant.CashierAgent;
import restaurant.Menu;
import restaurant.WaiterAgent;
import restaurant.interfaces.Waiter;
import restaurant.interfaces.Customer;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Customer customer;
	public ArrayList<LoggedEvent> log = new ArrayList<LoggedEvent>();
	public CashierAgent cashier;

	public MockCustomer(String name) {
		super(name);

	}

	@Override
	public void dispenseChange(double newMoney) {
		log.add(new LoggedEvent("Received message dispenseChange. Change=" + newMoney));
	}
	
	@Override
	public void hereIsCheck(double amountDue) {
		log.add(new LoggedEvent("Received message hereIsCheck. AmountDue=" + amountDue));
	}

}
