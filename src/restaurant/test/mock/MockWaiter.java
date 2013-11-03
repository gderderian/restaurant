package restaurant.test.mock;


import restaurant.CashierAgent;
import restaurant.CookAgent;
import restaurant.HostAgent;
import restaurant.Menu;
import restaurant.WaiterAgent.MyCustomer;
import restaurant.gui.WaiterGui;
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
	public void hereIsCheck(Customer c, double checkAmount) {
		log.add(new LoggedEvent("Received message hereIsCheck in amount " + checkAmount));
	}
	
	@Override
	public void readyForCheck(Customer c){
		log.add(new LoggedEvent("Received message readyForCheck."));
	}

	@Override
	public void doneEating(Customer c) {
		log.add(new LoggedEvent("Received message doneEating."));
	}

	@Override
	public void hereIsFood(int tableNum, String choice) {
		log.add(new LoggedEvent("Received message hereIsFood."));
	}

	@Override
	public void msgSeatCustomer(Customer c, int tableNum, HostAgent h, int customerX, int customerY) {
		log.add(new LoggedEvent("Received message msgSeatCustomer."));
	}

	@Override
	public void readyToOrder(Customer c) {
		log.add(new LoggedEvent("Received message readyToOrder."));
	}

	@Override
	public void hereIsMyChoice(String choice, Customer c) {
		log.add(new LoggedEvent("Received message hereIsMyChoice."));
	}

	@Override
	public void ImDone(Customer c) {
		log.add(new LoggedEvent("Received message ImDone."));
	}

	@Override
	public void needNewChoice(int tableNum, String choice) {
		log.add(new LoggedEvent("Received message needNewChoice."));
	}

	@Override
	public void breakApproved() {
		log.add(new LoggedEvent("Received message breakApproved."));
	}

	@Override
	public void breakRejected() {
		log.add(new LoggedEvent("Received message breakRejected."));
	}

	@Override
	public void requestBreak() {
		log.add(new LoggedEvent("Received message requestBreak."));
	}

	@Override
	public void seatCustomer(MyCustomer c) {
		log.add(new LoggedEvent("Received message seatCustomer."));
	}

	@Override
	public void deliverOrder(MyCustomer c, String choice) {
		log.add(new LoggedEvent("Received message deliverOrder."));
	}

	@Override
	public void goodbyeCustomer(MyCustomer c) {
		log.add(new LoggedEvent("Received message goodbyeCustomer."));
	}

	@Override
	public void releaseSemaphore() {
		log.add(new LoggedEvent("Received message releaseSemaphore."));
	}

	@Override
	public boolean hasCustomer(Customer c) {
		log.add(new LoggedEvent("Received call to hasCustomer."));
		return false;
	}

	@Override
	public void notifyHostReturnedFromBreak() {
		log.add(new LoggedEvent("Received call to notifyHostReturnedFromBreak."));
	}

}
