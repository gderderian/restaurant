package restaurant.interfaces;

import java.util.List;
import restaurant.CashierAgent;
import restaurant.CookAgent;
import restaurant.MarketAgent.MarketOrder;

public interface Market {

	// Messages
	public abstract void orderFood(CookAgent c, String foodToMarketOrder, int quantity);
	public abstract void acceptCashierPayment(CashierAgent c, double amountPaid);

}