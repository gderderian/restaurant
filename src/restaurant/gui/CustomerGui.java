package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.HostAgent;

import java.awt.*;

import javax.swing.ImageIcon;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	RestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 200;
	public static final int yTable = 250;
	
	// Lab 2
	private static final int HIDDEN_X = -40;
	private static final int HIDDEN_Y = -40;
	private static final int CUST_SIZE_X = 20;
	private static final int CUST_SIZE_Y = 20;
	private Image custImg;

	public CustomerGui(CustomerAgent c, RestaurantGui gui){ //HostAgent m) {
		agent = c;
		xPos = HIDDEN_X;
		yPos = HIDDEN_Y;
		xDestination = HIDDEN_X;
		yDestination = HIDDEN_Y;
		//maitreD = m;
		this.gui = gui;
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
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, CUST_SIZE_X, CUST_SIZE_Y);
		Image custImg = Toolkit.getDefaultToolkit().getImage("happy.jpg");
	    g.drawImage(custImg, xPos, yPos, CUST_SIZE_X, CUST_SIZE_Y, null);
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

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int seatnumber, int tableX, int tableY) { //later you will map seatnumber to table coordinates.
		xDestination = tableX;
		yDestination = tableY;
		command = Command.GoToSeat;
	}

	public void DoExitRestaurant() {
		xDestination = HIDDEN_X;
		yDestination = HIDDEN_Y;
		command = Command.LeaveRestaurant;
	}
}
