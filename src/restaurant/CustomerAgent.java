package restaurant;


import restaurant.gui.CustomerGui;
import agent.Agent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Timer;

import java.util.concurrent.Semaphore;


/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	
	static final int DEFAULT_HUNGER_LEVEL = 3500;
	static final int DEFAULT_SIT_TIME = 5000;
	static final int DEFAULT_CHOOSE_TIME = 5000;
	
	private String name;
	private String choice;
	private int hungerLevel = DEFAULT_HUNGER_LEVEL;
	Timer eatingTimer;
	Timer choosingTimer;
	private CustomerGui customerGui;
	
	private WaiterAgent assignedWaiter;
	private Menu myMenu;
	private HostAgent host;

	public enum AgentState
	{DoingNothing, WaitingForSeat, BeingSeated, Seated, Ordering, WaitingForFood, Eating, Leaving, Choosing};
	private AgentState state = AgentState.DoingNothing;

	public enum AgentEvent 
	{none, gotHungry, followHost, doneEating, doneLeaving, doneChoosing, seated, wantWaiter};
	AgentEvent event = AgentEvent.none;
	
	private Semaphore isAnimating = new Semaphore(0,true);

	public CustomerAgent(String name){
		
		super();
		this.name = name;
		choice = "";
		
		choosingTimer = new Timer(DEFAULT_CHOOSE_TIME,
				new ActionListener() { public void actionPerformed(ActionEvent evt) {
					choice = pickRandomItem();
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
	
	public void hereIsOrder(Order o) {
		state = AgentState.Eating;
		beginEating();
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	
	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		
		// print("State: " + state + " - Event: " + event);
		
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
		if (state == AgentState.Choosing && event == AgentEvent.doneChoosing){
			sendChoiceToWaiter();
			state = AgentState.WaitingForFood;
			return true;
		}
		if (state == AgentState.DoingNothing && event == AgentEvent.doneEating){
			state = AgentState.Leaving;
			leaveRestaurant();
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			refreshAfterLeaving();
			state = AgentState.DoingNothing;
			event = AgentEvent.none;
			return true;
		}
		return false;
	}

	// Actions
	private void tellWaiterReady(){
		assignedWaiter.readyToOrder(this);
	}
	
	private void sendChoiceToWaiter(){
		String itemChoice = pickRandomItem();
		choice = itemChoice;
		assignedWaiter.hereIsMyChoice(itemChoice, this);
		Do("Picked: " + itemChoice + " - Sending to waiter " + assignedWaiter.getName());
	}
	
	private void beginChoosing(){
		choosingTimer.setRepeats(false);
		choosingTimer.restart();
		choosingTimer.start();
	}
	
	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);
	}

	private void SitDown() {
		Do("Being seated. Going to table and beginning animation.");
		customerGui.beginAnimate();
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		event = AgentEvent.seated;
	}
	
	private void beginEating() {
		
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
		Do("Eating Food");
		eatingTimer.setRepeats(false);
		eatingTimer.restart();
		eatingTimer.start();
	}

	private void leaveRestaurant() {
		customerGui.setCarryText("");
		Do("Leaving.");
		customerGui.DoExitRestaurant();
		assignedWaiter.ImDone(this);
		state = AgentState.DoingNothing;
		event = AgentEvent.none;
	}
	
	private void refreshAfterLeaving(){
		assignedWaiter = null;
		myMenu = null;
		host = null;
		choice = "";
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

	// Misc. Utilities
	public String pickRandomItem() {
		Random randNum = new Random();
		int itemPickNum = randNum.nextInt(myMenu.itemList.size()) + 1;
		return myMenu.getAt(itemPickNum);
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
	
	public void releaseSemaphore(){
		//System.out.println("Releasing customer semaphore");
		isAnimating.release();
	}
	
}