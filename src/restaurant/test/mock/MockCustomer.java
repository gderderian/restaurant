package restaurant.test.mock;


import java.util.ArrayList;

import restaurant.CashierAgent;
import restaurant.HostAgent;
import restaurant.Menu;
import restaurant.WaiterAgent;
import restaurant.gui.CustomerGui;
import restaurant.interfaces.Waiter;
import restaurant.interfaces.Customer;


public class MockCustomer extends Mock implements Customer {

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

	@Override
	public void gotHungry() {
		log.add(new LoggedEvent("Received message gotHungry."));
		
	}

	@Override
	public void msgSitAtTable(Menu m, Waiter w) {
		log.add(new LoggedEvent("Received message msgSitAtTable."));
		
	}

	@Override
	public void msgWhatDoYouWant() {
		log.add(new LoggedEvent("Received message msgWhatDoYouWant."));
	}

	@Override
	public void hereIsOrder(String choice) {
		log.add(new LoggedEvent("Received message hereIsOrder. choice=" + choice));
	}

	@Override
	public void repickFood(Menu newMenu) {
		log.add(new LoggedEvent("Received message repickFood along with a new menu."));
		
	}

	@Override
	public void goToCorner() {
		log.add(new LoggedEvent("Received message goToCorner"));
	}

	@Override
	public void restaurantFull() {
		log.add(new LoggedEvent("Received action restaurantFull."));
	}

	@Override
	public String pickRandomItem() {
		log.add(new LoggedEvent("Received action pickRandomItem."));
		return null;
	}

	@Override
	public String pickRandomItemWithinCost() {
		log.add(new LoggedEvent("Received action pickRandomItemWIthinCost."));
		return null;
	}

	@Override
	public void releaseSemaphore() {
		log.add(new LoggedEvent("Received action releaseSemaphore."));
	}

	@Override
	public String getCustomerName() {
		log.add(new LoggedEvent("Received call to getCustomerName"));
		return null;
	}

	@Override
	public CustomerGui getGui() {
		log.add(new LoggedEvent("Received call to getGui."));
		return null;
	}

	@Override
	public HostAgent getHost() {
		log.add(new LoggedEvent("Received call to getHost."));
		return null;
	}

}
