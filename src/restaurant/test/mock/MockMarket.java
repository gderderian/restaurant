package restaurant.test.mock;


import restaurant.CashierAgent;
import restaurant.CookAgent;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.MarketAgent.MarketOrder;
import restaurant.Menu;
import restaurant.WaiterAgent.MyCustomer;
import restaurant.gui.WaiterGui;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.interfaces.Market;

import java.util.*;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockMarket extends Mock implements Market {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Waiter waiter;
	public ArrayList<LoggedEvent> log = new ArrayList<LoggedEvent>();

	public MockMarket(String name) {
		super(name);

	}

	@Override
	public void orderFood(CookAgent c, String foodToMarketOrder, int quantity) {
		log.add(new LoggedEvent("Recevied message orderFood."));
	}

	@Override
	public void acceptCashierPayment(CashierAgent c, double amountPaid) {
		log.add(new LoggedEvent("Received message acceptCashierPayment."));
	}
	
}