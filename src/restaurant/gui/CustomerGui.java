package restaurant.gui;

import restaurant.CustomerAgent;
import java.awt.*;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	RestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 200;
	public static final int yTable = 250;
	
	private static final int HIDDEN_X = -40;
	private static final int HIDDEN_Y = -40;
	private static final int BADCUSTOMERPILE_X = 400;
	private static final int BADCUSTOMERPILE_Y = 400;
	private static final int CUST_SIZE_X = 20;
	private static final int CUST_SIZE_Y = 20;
	
	boolean isAnimating = false;
	boolean hasDestination = false;
	String carryingOrderText = "";
	
	Color color = Color.GREEN;
	
	public CustomerGui(CustomerAgent c, RestaurantGui gui){
		agent = c;
		xPos = HIDDEN_X;
		yPos = HIDDEN_Y;
		xDestination = HIDDEN_X;
		yDestination = HIDDEN_Y;
		this.gui = gui;
	}
	
	public void setDestination(int newX, int newY){
		xDestination = newX;
		yDestination = newY;
		hasDestination = true;
	}
	
	public void beginAnimate(){
		isAnimating = true;
	}
	
	public void doneAnimating(){
		agent.releaseSemaphore();
		hasDestination = false;
		isAnimating = false;
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;
		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;
		if (xPos == xDestination && yPos == yDestination) {
			if (isAnimating){
				doneAnimating();
			}
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(color);
		g.fillRect(xPos, yPos, CUST_SIZE_X, CUST_SIZE_Y);
		if (!carryingOrderText.equals("")){
			g.drawString(carryingOrderText, xPos, yPos);
		}
	}

	public boolean isPresent() {
		return isPresent;
	}
	
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	
	public boolean isHungry() {
		return isHungry;
	}

	public int getX() {
		return xPos;
	}
	
	public int getY() {
		return yPos;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int seatnumber, int tableX, int tableY) {
		xDestination = tableX;
		yDestination = tableY;
		command = Command.GoToSeat;
	}

	public void DoExitRestaurant() {
		xDestination = HIDDEN_X;
		yDestination = HIDDEN_Y;
		command = Command.LeaveRestaurant;
	}
	
	public void goInCorner() {
		color = Color.black;
		carryingOrderText = "BAD!";
		xDestination = BADCUSTOMERPILE_X;
		yDestination = BADCUSTOMERPILE_Y;
		command = Command.noCommand;
	}
	
    public void setCarryText(String carryText){
    	carryingOrderText = carryText;
    }
    
    public void resetNotHungry() {
		gui.setCustomerEnabled(agent);
    	isHungry = false;
    }
    
}
