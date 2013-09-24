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
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, Eating, DoneEating, Leaving, Following, Choosing};
	private AgentState state = AgentState.DoingNothing;

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, doneEating, doneLeaving, doneChoosing};
	AgentEvent event = AgentEvent.none;

	public CustomerAgent(String name){
		super();
		this.name = name;
	}

	public void setHost(HostAgent host) {
		this.host = host;
	}

	public String getCustomerName() {
		return name;
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

	public void msgAnimationFinishedGoToSeat() {
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	
	public void msgWhatDoYouWant() {
		sendChoiceToWaiter();
		stateChanged();
	}
	
	public void hereIsOrder(Order o) {
		EatFood();
		stateChanged();
	}
	

	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost){
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
			leaveTable();
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

	private void leaveTable() {
		Do("Leaving.");
		host.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
		assignedWaiter.ImDone(this);
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

	public String pickRandomItem() {
		Random randNum = new Random();
		int itemPickNum = randNum.nextInt(myMenu.itemList.size()) + 1;
		return myMenu.getAt(itemPickNum);
	}

}


