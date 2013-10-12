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

	public MarketAgent(String name) {

		super();
		this.name = name;
		currentMarketOrders = new ArrayList<MarketOrder>();
		
		// Initial inventory
		inventoryCount = new Hashtable<String, Integer>();
		inventoryCount.put("Chicken", 1);
		inventoryCount.put("Mac & Cheese", 1);
		inventoryCount.put("French Fries", 5);
		inventoryCount.put("Pizza", 5);
		inventoryCount.put("Pasta", 5);
		inventoryCount.put("Cobbler", 5);
		
	}
	
	// Messages
	public void orderFood(CookAgent c, String foodToMarketOrder, int quantity) {
		// Determine if there is enough inventory of this item to fulfill this order
		Do("Received order from cook for " + quantity + " " + foodToMarketOrder);
		MarketOrder o = new MarketOrder();
		o.foodItem = foodToMarketOrder;
		o.requestingCook = c;
		o.quantityRequested = quantity;
		currentMarketOrders.add(o);
		stateChanged();
	}

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		if (!currentMarketOrders.isEmpty()) {
			for (MarketOrder order : currentMarketOrders) {
				if (order.getStatus() == orderStatus.ready) {
					orderDone(order);
					return true;
				} else if (order.getStatus() == orderStatus.waiting){
					prepareOrder(order);
					return true;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	// Actions
	private void prepareOrder(MarketOrder o){
		o.status = orderStatus.preparing;
		// Can we fulfill this order?
		int foodInventoryCount = inventoryCount.get(o.foodItem);
		if (o.quantityRequested > foodInventoryCount){
			o.deliverableQuantity = foodInventoryCount;
		} else {
			o.deliverableQuantity = o.quantityRequested;
		}
		inventoryCount.put(o.foodItem, foodInventoryCount - o.deliverableQuantity);
		o.setFulfilling(5000);
		Do("Beginning to prepare market order of " + o.getFoodName() + ".");
	}

	private void orderDone(MarketOrder o){
		o.getCook().deliverFood(o.foodItem, o.deliverableQuantity);
		currentMarketOrders.remove(o);
		Do("Notifying waiter that " + o.getFoodName() + " is done.");
	}
	
	public enum orderStatus {waiting, preparing, ready, bounceBack};
	
	public class MarketOrder {
		
		String foodItem;
		int quantityRequested;
		CookAgent requestingCook;
		Timer foodTimer;
		orderStatus status;
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
	
	// Accessors
	public String getName() {
		return name;
	}

	public List<MarketOrder> getMarketOrders() {
		return currentMarketOrders;
	}

}