package restaurant;

import restaurant.gui.CustomerGui;

import restaurant.gui.RestaurantGui;
import agent.Agent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	
	static final int DEFAULT_HUNGER_LEVEL = 5;
	static final int DEFAULT_SIT_TIME = 5000;
	static final int DEFAULT_CHOOSE_TIME = 5000;
	
	private String name;
	private int hungerLevel = DEFAULT_HUNGER_LEVEL;
	Timer eatingTimer = new Timer();
	Timer choosingTimer = new Timer();
	private CustomerGui customerGui;
	
	int destinationX = 0;
	int destinationY = 0;

	private WaiterAgent assignedWaiter;
	private Menu myMenu;
	private HostAgent host;

	public enum AgentState
	{DoingNothing, WaitingForSeat, BeingSeated, Seated, WaitingForFood, Eating, DoneEating, Leaving, Choosing};
	private AgentState state = AgentState.DoingNothing;

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, doneEating, doneLeaving, doneChoosing};
	AgentEvent event = AgentEvent.none;

	public CustomerAgent(String name){
		super();
		this.name = name;
	}

	// Messages
	public void gotHungry() {
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgSitAtTable(Table t, Menu m, WaiterAgent w) {
		print("Received msgSitAtTable");
		event = AgentEvent.followHost;
		destinationX = t.tableX;
		destinationY = t.tableY;
		assignedWaiter = w;
		myMenu = m;
		state = AgentState.Choosing;
		stateChanged();
	}
	
	public void msgWhatDoYouWant() {
		decideOnFood();
		stateChanged();
	}
	
	public void hereIsOrder(Order o) {
		EatFood();
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		event = AgentEvent.seated;
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
			// state = AgentState.Eating;
			// EatFood();
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
		state = AgentState.WaitingForFood;
		stateChanged();
	}
	
	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);
	}

	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(1, destinationX, destinationY);
	}
	
	private void EatFood() {
		Do("Eating Food");
		state = AgentState.Eating;
		eatingTimer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.doneEating;
				stateChanged();
			}
		},
		DEFAULT_SIT_TIME);
	}

	private void leaveRestaurant() {
		Do("Leaving.");
		host.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
		assignedWaiter.ImDone(this);
		state = AgentState.Leaving;
		stateChanged();
	}
	
	private void decideOnFood(){
		Do("Making menu choice");
		state = AgentState.Choosing;
		choosingTimer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done choosing, cookie=" + cookie);
				sendChoiceToWaiter();
				state = AgentState.WaitingForFood;
				stateChanged();
			}
		},
		DEFAULT_CHOOSE_TIME);
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