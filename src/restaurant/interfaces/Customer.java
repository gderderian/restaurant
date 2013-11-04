package restaurant.interfaces;

import restaurant.HostAgent;
import restaurant.Menu;
import restaurant.gui.CustomerGui;

public interface Customer {

	// Messages
	public abstract void gotHungry();
	public abstract void msgSitAtTable(Menu m, Waiter w);
	public abstract void msgWhatDoYouWant();
	public abstract void hereIsOrder(String choice);
	public abstract void repickFood(Menu newMenu);
	public abstract void dispenseChange(double newMoney);
	public abstract void hereIsCheck(double amountDue);
	public abstract void goToCorner();
	public abstract void restaurantFull();

	// Misc. Utilities
	public abstract String pickRandomItem();
	public abstract String pickRandomItemWithinCost();
	public abstract void releaseSemaphore();
	public abstract String getCustomerName();
	public abstract String getName();
	public abstract CustomerGui getGui();
	public abstract HostAgent getHost();

}