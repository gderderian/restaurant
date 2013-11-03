package restaurant;

import agent.Agent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.Timer;

/**
 * Restaurant Cook Agent
 */
public class CookAgent extends Agent {
	
	// Variable Declarations
	private String name;
	private List<Order> currentOrders;
	private List<MarketAgent> myMarkets;
	Hashtable<String, FoodItem> allFood;
	private static final int REORDER_THRESHOLD = 2; // Once a food item has this many of itself left, a reorder request will automatically be placed
	private static final int MARKETS_NUM = 2;

	public CookAgent(String name) {

		super();
		this.name = name;
		
		currentOrders = Collections.synchronizedList(new ArrayList<Order>());
		myMarkets = Collections.synchronizedList(new ArrayList<MarketAgent>());
		
		allFood = new Hashtable<String, FoodItem>();
		allFood.put("Chicken", new FoodItem("Chicken", 3000, 1));
		allFood.put("Mac & Cheese", new FoodItem("Mac & Cheese", 3000, 3));
		allFood.put("French Fries", new FoodItem("French Fries", 4000, 3));
		allFood.put("Pizza", new FoodItem("Pizza", 7000, 3));
		allFood.put("Pasta", new FoodItem("Pasta", 6000, 3));
		allFood.put("Cobbler", new FoodItem("Cobbler", 5000, 3));
		
	}
	
	// Messages
	public void hereIsOrder(String choice, WaiterAgent waiter, int tableNum) {
		Do("Cook has received an order of " + choice + " for table #" + tableNum + " via waiter " + waiter.getName() + ".");
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
		Do("Accepting order of " + quantity + " " + incomingFood + "(s) from market.");
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
			try {
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
			} catch (ConcurrentModificationException schedulerComod) {
				return true;
			}
		}
		return false;
	}

	// Actions
	private void prepareFood(Order o){ // Begins cooking the specified order and starts a timer based on the food item class' set cooking time
		Do("Beginning to prepare food " + o.getFoodName() + ".");
		o.status = orderStatus.preparing;
		o.setCooking(allFood.get(o.getFoodName()).cookingTime);
		allFood.get(o.foodItem).decrementQuantity(); // After preparing this order, there is one less of this item available
		if (allFood.get(o.foodItem).quantity <= REORDER_THRESHOLD && allFood.get(o.foodItem).reorderSent == false){
			int orderQuantity = allFood.get(o.foodItem).maxCapacity - allFood.get(o.foodItem).quantity;
			myMarkets.get(allFood.get(o.foodItem).searchMarket).orderFood(this, o.foodItem, orderQuantity);
			allFood.get(o.foodItem).requestedQuantity = orderQuantity;
		}
	}

	private void orderDone(Order o){ // Tells the specific waiter that their customer's order is done and removes that order from the cook's list of orders
		Do("Notifying waiter that " + o.getFoodName() + " is done.");
		o.getWaiter().hereIsFood(o.recipTable, o.foodItem);
		currentOrders.remove(o);
	}
	
	private void orderOut(Order o){ // Tells the specific waiter that their customer's order cannot be fulfilled
		Do("Notifying waiter that " + o.getFoodName() + " is out of stock and the customer who ordered it needs to rechoose.");
		o.getWaiter().needNewChoice(o.recipTable, o.foodItem);
		currentOrders.remove(o);
	}
	
	public void addMarket(MarketAgent m){
		myMarkets.add(m);
	}
	
	public class FoodItem {
		
		String foodItem;
		int cookingTime;
		int quantity;
		boolean reorderSent;
		int searchMarket;
		int requestedQuantity;
		int maxCapacity;
		
		public FoodItem(String foodName, int cookTime, int initialInventory){
			foodItem = foodName;
			cookingTime = cookTime;
			quantity = initialInventory;
			reorderSent = false;
			searchMarket = 0;
			requestedQuantity = 0;
			maxCapacity = initialInventory;
		}
		
		public void decrementQuantity(){
			quantity--;
		}
		
	}
	
	public enum orderStatus {waiting, preparing, ready, bounceBack};
	
	public class Order {
		
		String foodItem;
		int recipTable;
		WaiterAgent requestingWaiter;
		Timer foodTimer;
		orderStatus status;
		
		public Order(WaiterAgent w){
			requestingWaiter = w;
			status = orderStatus.waiting;
		}
		
		public Order(){
			status = orderStatus.waiting;
		}
		
		public Order(CustomerAgent c, WaiterAgent w, String foodChoice){
			requestingWaiter = w;
			foodItem = foodChoice;
			status = orderStatus.waiting;
		}
		
		public void setPreparing(){
			status = orderStatus.preparing;
		}
		
		public orderStatus getStatus(){
			return status;
		}
		
		public String getFoodName(){
			return foodItem;
		}
		
		public WaiterAgent getWaiter(){
			return requestingWaiter;
		}
		
		public void setCooking(int cookTime){
			foodTimer = new Timer(cookTime,
					new ActionListener() { public void actionPerformed(ActionEvent event) {
			          status = orderStatus.ready;
			          foodTimer.stop();
			      }
			});
			foodTimer.start();
		}

	}
	
	// Accessors
	public String getName() {
		return name;
	}

	public List<Order> getOrders() {
		return currentOrders;
	}

}