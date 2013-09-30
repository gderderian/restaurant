# Grant's CS201 Restaurant Design Doc

This document outlines the crucial parts of each main agent in the restaurant: HostAgent, WaiterAgent, CookAgent, and CustomerAgent.

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
		Do(cust.getName() + " is here and wants food.")
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
```

### Actions
```
	private void seatCustomer(CustomerAgent customer, Table table) {
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
			w_selected.msgSeatCustomer(customer, table, this)
			table.setOccupant(customer)
			waitingCustomers.remove(customer)
		}
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
				Do("Customer is ready to order!")
				takeOrder(c, c.table)
				return true
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.OrderedWaiting){
				Do("Sending " + c.customer.getName() + " order of " + c.order.getFoodName() + " to cook")
				sendToKitchen(c, c.order)
				return true
			}
		}
		for (MyCustomer c : myCustomers) {
			if (c.state == CustomerState.FoodReady){
				deliverOrder(c, c.order)
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
	public void hereIsFood(Order o) {
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(o.recipientCustomer)){
				cust.state = CustomerState.FoodReady
			}
		}
		stateChanged()
	}
```
```
	public void msgSeatCustomer(CustomerAgent c, Table t, HostAgent h) {
		myHost = h
		MyCustomer customer = new MyCustomer()
		customer.customer = c
		customer.table = t
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
		Order o = new Order(c, this, choice)
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				cust.order = o
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
	private void takeOrder(MyCustomer c, Table t){
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
	private void sendToKitchen(MyCustomer c, Order o){
		c.state = CustomerState.WaitingForFood
		Do("Order sent to cook, headed to kitchen!")
		waiterGui.setDestination(500, 230)
		waiterGui.beginAnimate()
		try {
			isAnimating.acquire()
		} catch (InterruptedException e) {
			e.printStackTrace()
		}
		myCook.hereIsOrder(o)
	}
```
```
	public boolean hasCustomer(CustomerAgent c){
		for (MyCustomer cust : myCustomers) {
			if (cust.customer.equals(c)){
				return true
			}
		}
		return false
	}
```
```
	public void setCook(CookAgent cook){
		myCook = cook
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
		
		Do("Going to fetch customer and then set their state")
		
		c.customer.msgSitAtTable(new Menu(), this)
		c.customer.getGui().setDestination(c.table.tableX, c.table.tableY)
		
		waiterGui.setDestination(c.table.tableX, c.table.tableY)
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
	public void deliverOrder(MyCustomer c, Order o){
		
		waiterGui.setDestination(500, 230)
		waiterGui.beginAnimate()
		
		try {
			isAnimating.acquire()
		} catch (InterruptedException e) {
			e.printStackTrace()
		}
		
		String carryText = ""
		
		switch(o.getFoodName()){
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
		waiterGui.setDestination(c.table.tableX, c.table.tableY)
		waiterGui.beginAnimate()
		
		try {
			isAnimating.acquire()
		} catch (InterruptedException e) {
			e.printStackTrace()
		}
		
		c.customer.hereIsOrder(o)
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
		Do("Picked: " + itemChoice + " - Sending to waiter " + assignedWaiter.getName())
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
		Do("Going to restaurant")
		host.msgIWantFood(this)
	}
```
```
	private void SitDown() {
		Do("Being seated. Going to table and beginning animation.")
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
		Do("Eating Food")
		eatingTimer.setRepeats(false)
		eatingTimer.restart()
		eatingTimer.start()
	}
```
```
	private void leaveRestaurant() {
		customerGui.setCarryText("")
		Do("Leaving.")
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
		Do("Picked: " + itemChoice + " - Sending to waiter " + assignedWaiter.getName())
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
		Do("Going to restaurant")
		host.msgIWantFood(this)
	}
```
```
	private void SitDown() {
		Do("Being seated. Going to table and beginning animation.")
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
		Do("Eating Food")
		eatingTimer.setRepeats(false)
		eatingTimer.restart()
		eatingTimer.start()
	}
```
```
	private void leaveRestaurant() {
		customerGui.setCarryText("")
		Do("Leaving.")
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
public void hereIsOrder(Order o) {
		currentOrders.add(o)
		Do("Order food name: " + o.getFoodName())
		stateChanged()
	}
```

### Actions
```
	private void prepareFood(Order o){ // Begins cooking the specified order and starts a timer based on the food item class' set cooking time
		o.status = orderStatus.preparing
		Do("Order Food Name: " + o.getFoodName())
		o.setCooking(timerList.get(o.getFoodName()))
	}
```
```
	private void orderDone(Order o){ // Tells the specific waiter that their customer's order is done and removes that order from the cook's list of orders
		o.getWaiter().hereIsFood(o)
		currentOrders.remove(o)
	}
```