package restaurant;

import agent.Agent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.Timer;
import restaurant.interfaces.Market;
import restaurant.test.mock.EventLog;

/**
 * Restaurant Market Agent
 */
public class MarketAgent extends Agent implements Market {
	
	// Variable Declarations
	private String name;
	public List<MarketOrder> currentMarketOrders;
	public Hashtable<String, Integer> inventoryCount;
	private static final int DEFAULT_ORDER_FULFILL_TIME = 25000; // All market orders take a default of twenty-five seconds to fulfill
	private double myMoney;
	private CashierAgent cashier;
	private Menu myMenu;

	public EventLog log;
	
	public MarketAgent(String name, CashierAgent c) {

		super();
		this.name = name;
		currentMarketOrders = new ArrayList<MarketOrder>();
		myMoney = 5;
		cashier = c;
		myMenu = new Menu();
		
		log = new EventLog();
		
		// Initial inventory declarations for each menuItem
		inventoryCount = new Hashtable<String, Integer>();
		inventoryCount.put("Chicken", 5);
		inventoryCount.put("Mac & Cheese", 5);
		inventoryCount.put("French Fries", 2);
		inventoryCount.put("Pizza", 5);
		inventoryCount.put("Pasta", 5);
		inventoryCount.put("Cobbler", 5);
		
	}
	
	// Messages
	public void orderFood(CookAgent c, String foodToMarketOrder, int quantity) {
		// Create and add order into queue to be fulfilled by market
		Do("Received order from cook for " + quantity + " " + foodToMarketOrder + "(s).");
		MarketOrder o = new MarketOrder();
		o.foodItem = foodToMarketOrder;
		o.requestingCook = c;
		o.quantityRequested = quantity;
		currentMarketOrders.add(o);
		stateChanged();
	}
	
	public void acceptCashierPayment(CashierAgent c, double amountPaid) {
		// Add what the cashier paid to my money
		myMoney = myMoney + amountPaid;
		stateChanged();
	}

	// Scheduler
	public boolean pickAndExecuteAnAction() {
		if (!currentMarketOrders.isEmpty()) {
			synchronized(currentMarketOrders) {
				for (MarketOrder order : currentMarketOrders) {
					if (order.getStatus() == orderStatus.ready) {
						orderDone(order); // Notify cook that their MarketOrder is done and deliver order back to them
						return true;
					} else if (order.getStatus() == orderStatus.waiting){
						prepareOrder(order); // Prepare waiting MarketOrder
						return true;
					}
				}
			}
		}
		return false;
	}

	// Actions
	private void prepareOrder(MarketOrder o){
		Do("Preparing marketOrder of " + o.foodItem + ".");
		o.status = orderStatus.preparing;
		int foodInventoryCount = inventoryCount.get(o.foodItem);
		if (o.quantityRequested > foodInventoryCount){ // Check and see if we (the market) actually have enough stock to fulfill
			o.deliverableQuantity = foodInventoryCount; // Give them everything we have as we cannot fulfill their entire order
		} else {
			o.deliverableQuantity = o.quantityRequested; // Give them everything we requested because we still have some in stock
		}
		inventoryCount.put(o.foodItem, foodInventoryCount - o.deliverableQuantity); // Adjust inventory to reflect completed order
		o.setFulfilling(DEFAULT_ORDER_FULFILL_TIME); // Begin fulfilling order by starting this order's timer
	}

	private void orderDone(MarketOrder o){
		Do("marketOrder of " + o.foodItem + " is now done.");
		o.getCook().deliverFood(o.foodItem, o.deliverableQuantity); // Notify cook that their order is now done
		double singleItemPrice = myMenu.getPriceofItem(o.foodItem);
		double totalCostToBill = singleItemPrice * o.deliverableQuantity; // Calculate total cost of order
		Do("Billing cashier $" + totalCostToBill + " for the " + o.deliverableQuantity + " " + o.foodItem + "(s) that they ordered.");
		cashier.acceptMarketBill(this, totalCostToBill); // Bill the cashier because an order was just delivered
		currentMarketOrders.remove(o);
	}
	
	// Accessors
	public String getName() {
		return name;
	}

	public List<MarketOrder> getMarketOrders() {
		return currentMarketOrders;
	}
	
	// Misc. utilities
	public enum orderStatus {waiting, preparing, ready, bounceBack}; // Ties in with MarketOrder class below
	
	public class MarketOrder {
		
		public String foodItem;
		public int quantityRequested;
		CookAgent requestingCook;
		Timer foodTimer;
		public orderStatus status;
		int deliverableQuantity;
		
		public MarketOrder(CookAgent c){
			requestingCook = c;
			status = orderStatus.waiting;
			deliverableQuantity = 0;
		}
		
		public MarketOrder(){
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
		
		public CookAgent getCook(){
			return requestingCook;
		}
		
		public void setFulfilling(int fulfillTime){ // This function is what delays marketOrders from being instantly available
			foodTimer = new Timer(fulfillTime,
					new ActionListener() { public void actionPerformed(ActionEvent event) {
			          status = orderStatus.ready; // Mark as ready only after set amount of time to fulfill order has compelted
			          foodTimer.stop();
			          stateChanged();
			      }
			});
			foodTimer.start();
		}
			
	}

}