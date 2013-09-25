package restaurant;

import restaurant.Order.orderStatus;
import restaurant.gui.CustomerGui;
import restaurant.gui.RestaurantGui;
import agent.Agent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	
	static final int DEFAULT_HUNGER_LEVEL = 5;
	static final int DEFAULT_SIT_TIME = 5000;
	static final int DEFAULT_CHOOSE_TIME = 5000;
	
	private String name;
	private String choice;
	private int hungerLevel = DEFAULT_HUNGER_LEVEL;
	Timer eatingTimer;
	Timer choosingTimer;
	private CustomerGui customerGui;
	
	int destinationX = 0;
	int destinationY = 0;

	private WaiterAgent assignedWaiter;
	private Menu myMenu;
	private HostAgent host;

	public enum AgentState
	{DoingNothing, WaitingForSeat, BeingSeated, Seated, WaitingForFood, Eating, Leaving, Choosing};
	private AgentState state = AgentState.DoingNothing;

	public enum AgentEvent 
	{none, gotHungry, followHost, doneEating, doneLeaving, doneChoosing, seated};
	AgentEvent event = AgentEvent.none;

	public CustomerAgent(String name){
		super();
		this.name = name;
		this.choice = "";
		choosingTimer = new Timer(DEFAULT_CHOOSE_TIME,
				new ActionListener() { public void actionPerformed(ActionEvent evt) {
					choice = pickRandomItem();
					event = AgentEvent.doneChoosing;
		      }
		});
		eatingTimer = new Timer(DEFAULT_HUNGER_LEVEL,
				new ActionListener() { public void actionPerformed(ActionEvent evt) {
					event = AgentEvent.doneEating;
		      }
		});
	}

	// Messages
	public void gotHungry() {
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgSitAtTable(Menu m) {
		print("Received msgSitAtTable");
		myMenu = m;
		event = AgentEvent.followHost;
		state = AgentState.BeingSeated;
		stateChanged();
	}
	
	public void msgWhatDoYouWant() {
		assignedWaiter.hereIsMyChoice(choice);
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
			state = AgentState.Choosing;
			beginChoosing();
			return true;
		}
		if (state == AgentState.Seated && event == AgentEvent.doneChoosing){
			sendChoiceToWaiter(); 
			state = AgentState.WaitingForFood;
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.Leaving;
			leaveRestaurant();
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			return true;
		}
		return false;
	}

	// Actions
	private void sendChoiceToWaiter(){
		String itemChoice = pickRandomItem();
		assignedWaiter.hereIsMyChoice(itemChoice);
		stateChanged();
	}
	
	private void beginChoosing(){
		choosingTimer.restart();
		choosingTimer.start();
	}
	
	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);
	}

	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(1, destinationX, destinationY);
	}
	
	private void beginEating() {
		Do("Eating Food");
		eatingTimer.restart();
		eatingTimer.start();
	}

	private void leaveRestaurant() {
		Do("Leaving.");
		customerGui.DoExitRestaurant();
		assignedWaiter.ImDone(this);
		state = AgentState.Leaving;
		stateChanged();
	}

	// Accessors
	public String getName() {
		return name;
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

}