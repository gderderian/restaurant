package restaurant;

import agent.Agent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.Timer;

/**
 * Restaurant Market Agent
 */
public class MarketAgent extends Agent {
	
	// Variable Declarations
	private String name;
	private List<MarketOrder> currentMarketOrders;
	Hashtable<String, Integer> inventoryCount;

	// Accessors
	public MarketAgent(String name) {

		super();
		this.name = name;
		currentMarketOrders = new ArrayList<MarketOrder>();
		
		// Initial inventory
		inventoryCount = new Hashtable<String, Integer>();
		inventoryCount.put("Chicken", 5);
		inventoryCount.put("Mac & Cheese", 5);
		inventoryCount.put("French Fries", 5);
		inventoryCount.put("Pizza", 5);
		inventoryCount.put("Pasta", 5);
		inventoryCount.put("Cobbler", 5);
		
	}

	public String getName() {
		return name;
	}

	public List<MarketOrder> getMarketOrders() {
		return currentMarketOrders;
	}
	
	// Messages
	public void orderFood(CookAgent c, String foodToMarketOrder, int quantity) {
		// Determine if there is enough inventory of this item to fulfill this order
		if (inventoryCount.get(foodToMarketOrder) >= 1) { // Able to fulfill order, dock one from that item's inventory
			MarketOrder o = new MarketOrder();
			o.foodItem = foodToMarketOrder;
			o.requestingCook = c;
			currentMarketOrders.add(o);
			stateChanged();
		} else { // Unable to fulfill order, create it and have it marked as bounceback
			MarketOrder o = new MarketOrder();
			o.foodItem = foodToMarketOrder;
			o.requestingCook = c;
			o.status = orderStatus.bounceBack;
			currentMarketOrders.add(o);
			stateChanged();
		}
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		if (!currentMarketOrders.isEmpty()) {
			for (MarketOrder order : currentMarketOrders) {
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
	private void prepareFood(MarketOrder o){
		o.status = orderStatus.preparing;
		o.setFulfilling(5000);
		int foodInventoryCount = inventoryCount.get(o.foodItem);
		inventoryCount.put(o.foodItem, foodInventoryCount - 1);
		Do("Beginning to prepare food " + o.getFoodName() + ".");
	}

	private void orderDone(MarketOrder o){
		o.getCook().deliverFood(o.foodItem, o.quantity);
		currentMarketOrders.remove(o);
		Do("Notifying waiter that " + o.getFoodName() + " is done.");
	}
	
	private void orderOut(MarketOrder o){
		o.getCook().deliverFood(o.foodItem, o.quantity);
		currentMarketOrders.remove(o);
		Do("Notifying waiter that " + o.getFoodName() + " is out an the customer who ordered it needs to rechoose.");
	}
	
	public enum orderStatus {waiting, preparing, ready, bounceBack};
	
	public class MarketOrder {
		
		String foodItem;
		int quantity;
		CookAgent requestingCook;
		Timer foodTimer;
		orderStatus status;
		
		public MarketOrder(CookAgent c){
			requestingCook = c;
			status = orderStatus.waiting;
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
		
		public void setFulfilling(int cookTime){
			foodTimer = new Timer(cookTime,
					new ActionListener() { public void actionPerformed(ActionEvent event) {
			          status = orderStatus.ready;
			          foodTimer.stop();
			      }
			});
			foodTimer.start();
		}
			
	}

}