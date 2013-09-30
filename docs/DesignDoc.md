# Grant's CS201 Restaurant Design Doc

This document outlines the crucial parts of each main agent in the restaurant: HostAgent, WaiterAgent, CookAgent, and CustomerAgent.

![Design Doc](designdocimg.png "Design Doc")

## Host Agent

### Data
```
	static final int NTABLES = 4
	public List<CustomerAgent> waitingCustomers
	public List<WaiterAgent> myWaiters
	public Collection<Table> tables
	private String name
	public WaiterGui hostGui = null
	String carryingOrderText = ""
```
### Scheduler
```
for (Table table : tables) {
			if (!table.isOccupied()) {
				if (!waitingCustomers.isEmpty()) {
					seatCustomer(waitingCustomers.get(0), table)
					return true
				}
			}
		}
return false
```

### Messages
```
public void msgIWantFood(CustomerAgent cust) {
		waitingCustomers.add(cust)
		stateChanged()
	}
```
```
	public void msgLeavingTable(CustomerAgent cust) {
		
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table + " - setting as unoccupied")
				table.setUnoccupied()
				stateChanged()
			}
		}
		
	}
```
### Actions
```
		// Find waiter and notify them
		if (myWaiters.size() != 0) {
			int init_cust = myWaiters.get(0).getNumCustomers()
			WaiterAgent w_selected = null
			for (WaiterAgent w : myWaiters){
				if (w.getNumCustomers() <= init_cust){
					init_cust = w.getNumCustomers()
					w_selected = w
				}
			}
			w_selected.msgSeatCustomer(customer, table.tableNumber, this)
			table.setOccupant(customer)
			waitingCustomers.remove(customer)
		}
```

## Waiter Agent

### Data
```
	public List<MyCustomer> myCustomers
	public HostAgent myHost
	public CookAgent myCook
	private String name
	private WaiterGui waiterGui
	private Semaphore isAnimating = new Semaphore(0,true)
```

### Scheduler
```
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.Waiting){
				seatCustomer(c)
				return true
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.ReadyToOrder){
				takeOrder(c, c.tableNum)
				return true
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.OrderedWaiting){
				sendToKitchen(c, c.choice)
				return true
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.FoodReady){
				deliverOrder(c, c.choice)
				return true
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.Done){
				goodbyeCustomer(c)
				return true
			}
		}
		goHome()
		return false
```

### Messages
```
public void doneEating(CustomerAgent c) {
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.state = CustomerState.Done
			}
		}
		stateChanged()
	}
```
```
	public void hereIsFood(int tableNum, String choice) {
		for (MyCustomer cust : myCustomers) {
			if (cust.tableNum == tableNum){
				cust.state = CustomerState.FoodReady
			}
		}
		stateChanged()
	}
```
```
	public void msgSeatCustomer(CustomerAgent c, int tableNum, HostAgent h) {
		myHost = h
		MyCustomer customer = new MyCustomer()
		customer.customer = c
		customer.tableNum = tableNum
		myCustomers.add(customer)
		stateChanged()
	}
```
```
	public void readyToOrder(CustomerAgent c) {
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.state = CustomerState.ReadyToOrder
			}
		}
		stateChanged()
		
	}
```
```
	public void hereIsMyChoice(String choice, CustomerAgent c) {
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.choice = choice
				cust.state = CustomerState.OrderedWaiting
			}
		}
		stateChanged()
	}
```
```
	public void ImDone(CustomerAgent c) {
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.state = CustomerState.Done
			}
		}
		stateChanged()
	}

```

### Actions
```
private void takeOrder(MyCustomer c, int tableNum){
		waiterGui.setDestination(c.customer.getGui().getX(), c.customer.getGui().getY())
		waiterGui.beginAnimate()
		try {
			isAnimating.acquire()
		} catch (InterruptedException e) {
			e.printStackTrace()
		}
		c.customer.msgWhatDoYouWant()
		c.state = CustomerState.Ordering
	}
```
```
	private void sendToKitchen(MyCustomer c, String choice){
		c.state = CustomerState.WaitingForFood
		waiterGui.setDestination(500, 230)
		waiterGui.beginAnimate()
		try {
			isAnimating.acquire()
		} catch (InterruptedException e) {
			e.printStackTrace()
		}
		myCook.hereIsOrder(choice, this, c.tableNum)
	}
```
```
	public void seatCustomer(MyCustomer c){
		waiterGui.setDestination(-20, -20)
		waiterGui.beginAnimate()
		try {
			isAnimating.acquire()
		} catch (InterruptedException e) {
			e.printStackTrace()
		}
		
		c.customer.msgSitAtTable(new Menu(), this)
		
		int destX = 0, destY = 0
		
		for (Table t : myHost.getTables()) {
			if (c.tableNum == t.tableNumber){
				destX = t.tableX
				destY = t.tableY
			}
		}
		
		c.customer.getGui().setDestination(destX, destY)
		
		waiterGui.setDestination(destX, destY)
		waiterGui.beginAnimate()
		
		try {
			isAnimating.acquire()
		} catch (InterruptedException e) {
			e.printStackTrace()
		}
		
		c.state = CustomerState.Seated
		
	}
```
```
	public void deliverOrder(MyCustomer c, String choice){
		waiterGui.setDestination(500, 230)
		waiterGui.beginAnimate()
		
		try {
			isAnimating.acquire()
		} catch (InterruptedException e) {
			e.printStackTrace()
		}
		
		String carryText = ""
		
		switch(choice){
		case "Chicken":
			carryText = "CHK"
			break
		case "Mac & Cheese":
			carryText = "M&C"
			break
		case "French Fries":
			carryText = "FRF"
			break
		case "Pizza":
			carryText = "PZA"
			break
		case "Pasta":
			carryText = "PST"
			break
		case "Cobbler":
			carryText = "CBL"
			break
		}
		
		waiterGui.setCarryText(carryText)
		waiterGui.setDestination(c.customer.getGui().getX(), c.customer.getGui().getY())
		waiterGui.beginAnimate()
		
		try {
			isAnimating.acquire()
		} catch (InterruptedException e) {
			e.printStackTrace()
		}
		
		c.customer.hereIsOrder(choice)
		c.state = CustomerState.Eating
		waiterGui.setCarryText("")
		
	}
```
```
	public void goodbyeCustomer(MyCustomer c){
		myCustomers.remove(c)
		c.customer.getHost().msgLeavingTable(c.customer)
	}
```
```
	private void goHome(){
		waiterGui.setDestination(230, 230)
	}
```

## Customer Agent

### Data
```
	static final int DEFAULT_HUNGER_LEVEL = 3500
	static final int DEFAULT_SIT_TIME = 5000
	static final int DEFAULT_CHOOSE_TIME = 5000
	
	private String name
	private String choice
	private int hungerLevel = DEFAULT_HUNGER_LEVEL
	Timer eatingTimer
	Timer choosingTimer
	private CustomerGui customerGui
	
	private WaiterAgent assignedWaiter
	private Menu myMenu
	private HostAgent host

	public enum AgentState
	{DoingNothing, WaitingForSeat, BeingSeated, Seated, Ordering, WaitingForFood, Eating, Leaving, Choosing, CalledWaiter}
	private AgentState state = AgentState.DoingNothing

	public enum AgentEvent 
	{none, gotHungry, followHost, doneEating, doneLeaving, doneChoosing, seated, wantWaiter}
	AgentEvent event = AgentEvent.none
	
	private Semaphore isAnimating = new Semaphore(0,true)
```
### Scheduler
```
if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry){
			state = AgentState.WaitingForSeat
			goToRestaurant()
			return true
		}
		if (state == AgentState.WaitingForSeat && event == AgentEvent.followHost){
			state = AgentState.BeingSeated
			SitDown()
			return true
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			print("Beginning to choose")
			state = AgentState.Choosing
			beginChoosing()
			return true
		}
		if (state == AgentState.Choosing && event == AgentEvent.wantWaiter){
			tellWaiterReady()
			return true
		}
		if (state == AgentState.CalledWaiter && event == AgentEvent.doneChoosing){
			sendChoiceToWaiter()
			state = AgentState.WaitingForFood
			return true
		}
		if (state == AgentState.DoingNothing && event == AgentEvent.doneEating){
			state = AgentState.Leaving
			leaveRestaurant()
			return true
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			refreshAfterLeaving()
			state = AgentState.DoingNothing
			event = AgentEvent.none
			return true
		}
		return false
```

### Messages
```
	public void gotHungry() {
		print("I'm hungry")
		event = AgentEvent.gotHungry
		stateChanged()
	}
```
```
	public void msgSitAtTable(Menu m, WaiterAgent w) {
		print("Received msgSitAtTable")
		myMenu = m
		assignedWaiter = w
		event = AgentEvent.followHost
		stateChanged()
	}
```
```
	public void msgWhatDoYouWant() {
		print("Received msgWhatWant")
		event = AgentEvent.doneChoosing
		stateChanged()
	}
```
```
	public void hereIsOrder(String choice) {
		state = AgentState.Eating
		beginEating()
		stateChanged()
	}
```
```
	public void msgAnimationFinishedGoToSeat() {
		stateChanged()
	}
```
```
	public void msgAnimationFinishedLeaveRestaurant() {
		event = AgentEvent.doneLeaving
		stateChanged()
	}
	
```

### Actions
```
	private void tellWaiterReady(){
		assignedWaiter.readyToOrder(this)
		state = AgentState.CalledWaiter
	}
```
```
	private void sendChoiceToWaiter(){
		String itemChoice = pickRandomItem()
		choice = itemChoice
		assignedWaiter.hereIsMyChoice(itemChoice, this)
		
		String carryText = ""
		switch(choice){
		case "Chicken":
			carryText = "CHK"
			break
		case "Mac & Cheese":
			carryText = "M&C"
			break
		case "French Fries":
			carryText = "FRF"
			break
		case "Pizza":
			carryText = "PZA"
			break
		case "Pasta":
			carryText = "PST"
			break
		case "Cobbler":
			carryText = "CBL"
			break
		}
		customerGui.setCarryText(carryText + "?")
		
	}
```
```
	private void beginChoosing(){
		choosingTimer.setRepeats(false)
		choosingTimer.restart()
		choosingTimer.start()
	}
```
```
	private void goToRestaurant() {
		host.msgIWantFood(this)
	}
```
```
	private void SitDown() {
		customerGui.beginAnimate()
		try {
			isAnimating.acquire()
		} catch (InterruptedException e) {
			e.printStackTrace()
		}
		event = AgentEvent.seated
	}
```
```
	private void beginEating() {
		String carryText = ""
		switch(choice){
		case "Chicken":
			carryText = "CHK"
			break
		case "Mac & Cheese":
			carryText = "M&C"
			break
		case "French Fries":
			carryText = "FRF"
			break
		case "Pizza":
			carryText = "PZA"
			break
		case "Pasta":
			carryText = "PST"
			break
		case "Cobbler":
			carryText = "CBL"
			break
		}
		customerGui.setCarryText(carryText)
		eatingTimer.setRepeats(false)
		eatingTimer.restart()
		eatingTimer.start()
	}
```
```
	private void leaveRestaurant() {
		customerGui.setCarryText("")
		customerGui.DoExitRestaurant()
		assignedWaiter.ImDone(this)
		state = AgentState.DoingNothing
		event = AgentEvent.none
	}
```
```
	private void refreshAfterLeaving(){
		assignedWaiter = null
		myMenu = null
		host = null
		choice = ""
	}
```

## Cook Agent

### Data
```
	private String name
	private List<Order> currentOrders
	Hashtable<String, Integer> timerList
```
### Scheduler
```
		if (!currentOrders.isEmpty()) {
			for (Order order : currentOrders) {
				if (order.getStatus() == orderStatus.ready) {
					orderDone(order)
					return true
				} else if (order.getStatus() == orderStatus.waiting){
					prepareFood(order)
					return true
				} else {
					return true
				}
			}
		}
		return false
```

### Messages
```
	public void hereIsOrder(String choice, WaiterAgent waiter, int tableNum) {
		Order o = new Order()
		o.foodItem = choice
		o.requestingWaiter = waiter
		o.recipTable = tableNum
		currentOrders.add(o)
		stateChanged()
	}
```

### Actions
```
	private void prepareFood(Order o){
		o.status = orderStatus.preparing
		o.setCooking(timerList.get(o.getFoodName()))
	}
```
```
	private void orderDone(Order o){
		o.getWaiter().hereIsFood(o.recipTable, o.foodItem)
		currentOrders.remove(o)
	}
```