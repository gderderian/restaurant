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
import restaurant.MarketAgent;
import java.util.*;


public class MockMarket extends Mock implements Market {

	public MockMarket(String name) {
		super(name);
	}

	@Override
	public void orderFood(CookAgent c, String foodToMarketOrder, int quantity) {
		log.add(new LoggedEvent("Received message orderFood with quantity " + quantity + " of " + foodToMarketOrder + " from cook " + c.getName()));
	}

	@Override
	public void acceptCashierPayment(CashierAgent c, double amountPaid) {
		log.add(new LoggedEvent("Received message acceptCashierPayment of " + amountPaid));
	}
	
}