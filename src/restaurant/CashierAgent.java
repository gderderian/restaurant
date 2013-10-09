package restaurant;

import agent.Agent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.Timer;

/**
 * Restaurant Cashier Agent
 */
public class CashierAgent extends Agent {
	
	// Variable Declarations
	private String name;
	private List<Check> myChecks;
	private Menu myMenu;

	// Accessors
	public CashierAgent(String name) {

		super();
		this.name = name;
		myChecks = new ArrayList<Check>();
		Menu myMenu = new Menu();
		
	}

	public String getName() {
		return name;
	}
	
	// Messages
	public void hereIsOrder(String choice, WaiterAgent waiter, int tableNum) {
		
		Do("Here is order to cook.");
		
		// Determine if there is enough inventory of this item to fulfill this order
		if (allFood.get(choice).quantity >= 1) { // Able to fulfill order, dock one from that item's inventory
			Order o = new Order();
			o.foodItem = choice;
			o.requestingWaiter = waiter;
			o.recipTable = tableNum;
			currentOrders.add(o);
			stateChanged();
		} else { // Unable to fulfill order, create it and have it marked as bounce back
			Order o = new Order();
			o.foodItem = choice;
			o.requestingWaiter = waiter;
			o.recipTable = tableNum;
			o.status = orderStatus.bounceBack;
			currentOrders.add(o);
			stateChanged();
		}
		
	}
	
	public void deliverFood(String incomingFood, int quantity) {
		Do("Accepting order of " + quantity + " " + incomingFood + "s from market.");
		FoodItem f = allFood.get(incomingFood);
		if (quantity < f.requestedQuantity && f.searchMarket != MARKETS_NUM){
			f.searchMarket++;
		}
		int currentFoodQuantity = f.quantity;
		int newFoodQuantity  = currentFoodQuantity + quantity;
		f.quantity = newFoodQuantity;
		f.reorderSent = false;
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		if (!currentOrders.isEmpty()) {
			for (Order order : currentOrders) {
				if (order.getStatus() == orderStatus.ready) {
					orderDone(order);
					return true;
				} else if (order.getStatus() == orderStatus.waiting){
					prepareFood(order);
					return true;
				} else if (order.getStatus() == orderStatus.bounceBack) { // Item is out, send choice back to waiter
					orderOut(order);
				} else {
					return true;
				}
			}
		}
		return false;
	}

	// Actions
	private void prepareFood(Order o){ // Begins cooking the specified order and starts a timer based on the food item class' set cooking time
		o.status = orderStatus.preparing;
		o.setCooking(allFood.get(o.getFoodName()).cookingTime);
		allFood.get(o.foodItem).decrementQuantity(); // After preparing this order, there is one less of this item available
		if (allFood.get(o.foodItem).quantity <= REORDER_THRESHOLD && allFood.get(o.foodItem).reorderSent == false){
			int orderQuantity = allFood.get(o.foodItem).maxCapacity - allFood.get(o.foodItem).quantity;
			Do("Ordering " + orderQuantity + " " + o.foodItem + "s from market.");
			myMarkets.get(allFood.get(o.foodItem).searchMarket).orderFood(this, o.foodItem, orderQuantity);
		}
		Do("Beginning to prepare food " + o.getFoodName() + ".");
	}

	private void orderDone(Order o){ // Tells the specific waiter that their customer's order is done and removes that order from the cook's list of orders
		o.getWaiter().hereIsFood(o.recipTable, o.foodItem);
		currentOrders.remove(o);
		Do("Notifying waiter that " + o.getFoodName() + " is done.");
	}
	
	private void orderOut(Order o){ // Tells the specific waiter that their customer's order cannot be fulfilled
		o.getWaiter().needNewChoice(o.recipTable, o.foodItem);
		currentOrders.remove(o);
		Do("Notifying waiter that " + o.getFoodName() + " is out an the customer who ordered it needs to rechoose.");
	}
	
	public void addMarket(MarketAgent m){
		myMarkets.add(m);
	}
	
	public class Check {
		
		CustomerAgent customer;
		WaiterAgent waiter;
		float amount;
		
		public Check(float checkAmount){
			amount = checkAmount;
		}
		
		public void setCustomer(CustomerAgent c){
			customer = c;
		}
		
		public void setWaiter(WaiterAgent w){
			waiter = w;
		}
		
	}

}