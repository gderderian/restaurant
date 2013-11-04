package restaurant.interfaces;

import restaurant.gui.WaiterGui;
import restaurant.CashierAgent;
import restaurant.CookAgent;
import restaurant.HostAgent;
import restaurant.Menu;
import restaurant.WaiterAgent.MyCustomer;

public interface Waiter {

	// Messages
	public abstract void doneEating(Customer c);
	public abstract void hereIsFood(int tableNum, String choice);
	public abstract void msgSeatCustomer(Customer c, int tableNum, HostAgent h, int customerX, int customerY);
	public abstract void readyToOrder(Customer c);
	public abstract void hereIsMyChoice(String choice, Customer c);
	public abstract void ImDone(Customer c);
	public abstract void needNewChoice(int tableNum, String choice);
	public abstract void breakApproved();
	public abstract void breakRejected();
	public abstract void requestBreak();
	public abstract void hereIsCheck(Customer c, double checkAmount);
	public abstract void readyForCheck(Customer c);
	public abstract void seatCustomer(MyCustomer c);
	public abstract void deliverOrder(MyCustomer c, String choice);
	public abstract void goodbyeCustomer(MyCustomer c);
	public abstract void releaseSemaphore();
	public abstract boolean hasCustomer(Customer c);
	public abstract void notifyHostReturnedFromBreak();

}