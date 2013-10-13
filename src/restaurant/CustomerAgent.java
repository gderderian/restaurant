package restaurant;

import restaurant.gui.CustomerGui;
import agent.Agent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.util.concurrent.Semaphore;
import java.util.Random;


/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	
	static final int DEFAULT_HUNGER_LEVEL = 3500;
	static final int DEFAULT_SIT_TIME = 5000;
	static final int DEFAULT_CHOOSE_TIME = 5000;
	static final int ORDER_ATTEMPT_THRESHOLD = 3;
	
	private String name;
	private String choice;
	private int hungerLevel = DEFAULT_HUNGER_LEVEL;
	Timer eatingTimer;
	Timer choosingTimer;
	private CustomerGui customerGui;
	private double money;
	private double needToPay;
	private int orderAttempts;
	
	private WaiterAgent assignedWaiter;
	private Menu myMenu;
	private HostAgent host;
	private CashierAgent cashier;

	public enum AgentState
	{DoingNothing, WaitingForSeat, BeingSeated, Seated, Ordering, WaitingForFood, Eating, Leaving, Choosing, CalledWaiter, RequestedCheck, Paying, restaurantFull, CantPay};
	private AgentState state = AgentState.DoingNothing;

	public enum AgentEvent 
	{none, gotHungry, followHost, begunEating, doneEating, doneLeaving, doneChoosing, seated, wantWaiter, receivedCheck, notPaid};
	AgentEvent event = AgentEvent.none;
	
	private Semaphore isAnimating = new Semaphore(0,true);

	public CustomerAgent(String name){
		
		super();
		this.name = name;
		choice = "";
		money = 15.00;
		needToPay = 0;
		orderAttempts = 0;
		
		// Hack to set amount of money based on customer's name
		if (name == "reallycheap"){
			money = 1.00; // Can't afford anything
		} else if (name == "cheap") {
			money = 2.50;
		} else if (name == "somemoney") {
			money = 7.00;
		} else if (name == "lotsofmoney") {
			money = 15.00;
		} else if (name == "tonsofmoney") {
			money = 25.00;
		}
		
		choosingTimer = new Timer(DEFAULT_CHOOSE_TIME,
				new ActionListener() { public void actionPerformed(ActionEvent evt) {
					choice = pickRandomItemWithinCost();
					event = AgentEvent.wantWaiter;
					stateChanged();
		      }
		});
		eatingTimer = new Timer(DEFAULT_HUNGER_LEVEL,
				new ActionListener() { public void actionPerformed(ActionEvent evt) {
					state = AgentState.DoingNothing;
					event = AgentEvent.doneEating;
					stateChanged();
		      }
		});
	}

	// Messages
	public void gotHungry() {
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgSitAtTable(Menu m, WaiterAgent w) {
		print("Received msgSitAtTable");
		myMenu = m;
		assignedWaiter = w;
		event = AgentEvent.followHost;
		stateChanged();
	}
	
	public void msgWhatDoYouWant() {
		print("Received msgWhatWant");
		event = AgentEvent.doneChoosing;
		stateChanged();
	}
	
	public void hereIsOrder(String choice) {
		state = AgentState.Eating;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	
	public void repickFood(Menu newMenu) {
		myMenu = newMenu;
		state = AgentState.BeingSeated;
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void dispenseChange(double newMoney) {
		money = newMoney;
		Do("New money amount is $" + newMoney);
		stateChanged();
	}
	
	public void hereIsCheck(double amountDue) {
		Do("Customer needs to pay $" + amountDue);
		needToPay = amountDue;
		event = AgentEvent.receivedCheck;
		stateChanged();
	}
	
	public void goToCorner() {
		state = AgentState.CantPay;
		event = AgentEvent.notPaid;
		stateChanged();
	}
	
	public void restaurantFull(){
		state = AgentState.restaurantFull;
		stateChanged();
	}
	
	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry){
			state = AgentState.WaitingForSeat;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingForSeat && event == AgentEvent.followHost){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			print("Beginning to choose");
			state = AgentState.Choosing;
			beginChoosing();
			return true;
		}
		if (state == AgentState.Choosing && event == AgentEvent.wantWaiter){
			tellWaiterReady();
			return true;
		}
		if (state == AgentState.CalledWaiter && event == AgentEvent.doneChoosing){
			sendChoiceToWaiter();
			Do("Calling suspicious message!");
			state = AgentState.WaitingForFood;
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneChoosing){
			beginEating();
			event = AgentEvent.begunEating;
			return true;
		}		
		if (state == AgentState.DoingNothing && event == AgentEvent.doneEating){
			state = AgentState.Leaving;
			sendReadyForCheck();
			return true;
		}
		if (state == AgentState.RequestedCheck && event == AgentEvent.receivedCheck){
			leaveRestaurant();
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			event = AgentEvent.none;
			return true;
		}
		if (state == AgentState.Paying && event == AgentEvent.none){
			sendPayment();
			state = AgentState.Leaving;
			event = AgentEvent.doneLeaving;
			return true;
		}
		if (state == AgentState.WaitingForFood && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			event = AgentEvent.none;
			return true;
		}
		if (state == AgentState.CantPay && event == AgentEvent.notPaid){
			shame();
			state = AgentState.DoingNothing;
			event = AgentEvent.none;
			return true;
		}
		if (state == AgentState.restaurantFull && event == AgentEvent.gotHungry){
			determineIfStay();
			event = AgentEvent.none;
			return true;
		}
		return false;
	}

	// Actions
	private void tellWaiterReady(){
		assignedWaiter.readyToOrder(this);
		state = AgentState.CalledWaiter;
		Do("Telling waiter that I'm ready.");
	}
	
	private void sendReadyForCheck(){
		assignedWaiter.readyForCheck(this);
		state = AgentState.RequestedCheck;
		Do("Telling waiter I want my check because I'm ready to leave." + event + " - " + state);
	}
	
	private void sendChoiceToWaiter(){
		
		// Customer can't afford anything on menu within their price range because choice was set to blank
		if (choice.equals("")) { // Customer cannot afford any items on the menu. Make them leave.
			leaveAbruptly();
			return;
		}
		
		// Customer leaves if there is nothing for them to order
		if (orderAttempts > ORDER_ATTEMPT_THRESHOLD){ // Nothing is left on the menu for the customer to order. Make them leave.
			Do("Customer is leaving because there is not enough food for them to oder and the threshold has been exceeded!!");
			leaveAbruptly();
			return;
		}
		
		assignedWaiter.hereIsMyChoice(choice, this);
		orderAttempts++;
		
		String carryText = "";
		switch(choice){
		case "Chicken":
			carryText = "CHK";
			break;
		case "Mac & Cheese":
			carryText = "M&C";
			break;
		case "French Fries":
			carryText = "FRF";
			break;
		case "Pizza":
			carryText = "PZA";
			break;
		case "Pasta":
			carryText = "PST";
			break;
		case "Cobbler":
			carryText = "CBL";
			break;
		}
		customerGui.setCarryText(carryText + "?");
		
		Do("Sending food choice " + choice + " to waiter.");
	}
	
	private void beginChoosing(){
		choosingTimer.setRepeats(false);
		choosingTimer.restart();
		choosingTimer.start();
		Do("Beginning to decide what food item to pick.");
	}
	
	private void goToRestaurant() {
		host.msgIWantFood(this);
		Do("Going to restaurant and telling host that I'm hungry.");
		Do("I have $" + money);
	}

	private void SitDown() {
		Do("Going to sit down.");
		customerGui.beginAnimate();
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		event = AgentEvent.seated;
	}
	
	private void beginEating() {
		Do("Beginning to eat food.");
		String carryText = "";
		switch(choice){
		case "Chicken":
			carryText = "CHK";
			break;
		case "Mac & Cheese":
			carryText = "M&C";
			break;
		case "French Fries":
			carryText = "FRF";
			break;
		case "Pizza":
			carryText = "PZA";
			break;
		case "Pasta":
			carryText = "PST";
			break;
		case "Cobbler":
			carryText = "CBL";
			break;
		}
		customerGui.setCarryText(carryText);
		eatingTimer.setRepeats(false);
		eatingTimer.restart();
		eatingTimer.start();
	}

	private void leaveRestaurant() {
		Do("Leaving restaurant.");
		customerGui.setCarryText("");
		customerGui.DoExitRestaurant();
		assignedWaiter.ImDone(this);
		orderAttempts = 0;
		state = AgentState.Paying;
		event = AgentEvent.none;
	}
	
	private void leaveAbruptly() {
		Do("Leaving restaurant.");
		customerGui.setCarryText("");
		customerGui.DoExitRestaurant();
		assignedWaiter.ImDone(this);
		orderAttempts = 0;
		event = AgentEvent.doneLeaving;
	}
	
	private void sendPayment(){
		Do("Sending money to cashier: $" + money);
		cashier.acceptPayment(this, money);
	}
	
	private void determineIfStay(){
		Random random = new Random();
	    boolean willStay = random.nextBoolean();
		
	    if (willStay == true){
	    	state = AgentState.DoingNothing;
	    	event = AgentEvent.gotHungry;
	    	host.msgIWantFood(this);
	    	Do("I'LL CONTINUE TO WAIT!!!!!!!!!!!!!!!!");
	    } else {
	    	Do("TOO MANY PEOPLE ARE WAITING, I'M LEAVING!!!!!");
	    	state = AgentState.DoingNothing;
	    	event = AgentEvent.none;
	    	customerGui.resetNotHungry();
	    }
		
	}

	// Accessors
	public String getName() {
		return name;
	}
	
	public HostAgent getHost() {
		return host;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	
	public void setHost(HostAgent host) {
		this.host = host;
	}

	public String getCustomerName() {
		return name;
	}
	
	public void assignWaiter(WaiterAgent w) {
		assignedWaiter = w;
	}
	
	public void setCashier(CashierAgent c) {
		cashier = c;
	}
	
	public double getNeedToPay() {
		return needToPay;
	}

	public void shame() {
		customerGui.goInCorner();
	}
	
	// Misc. Utilities
	public String pickRandomItem() {
		return myMenu.pickRandomItem();
	}
	
	public String pickRandomItemWithinCost() {
		return myMenu.pickRandomItemWithinCost(money);
	}
	
	public void releaseSemaphore(){
		isAnimating.release();
	}
	
}