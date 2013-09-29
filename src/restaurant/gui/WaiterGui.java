package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.WaiterAgent;
import restaurant.CustomerAgent.AgentState;

import java.awt.*;

public class WaiterGui implements Gui {

    private WaiterAgent agent = null;

    private int xPos = 230, yPos = 230;
    public int xDestination = 230, yDestination = 230, host_tableX, host_tableY;

    public static final int xTable = 200;
    public static final int yTable = 250;
    
    private static final int HOST_SIZE_X = 20;
    private static final int HOST_SIZE_Y = 20;

	boolean isAnimating = false;
	boolean hasDestination = false;
    
    public WaiterGui(WaiterAgent a) {
    	agent = a;
    }

	public void setDestination(int newX, int newY){
		xDestination = newX;
		yDestination = newY;
		hasDestination = true;
	}
    
    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;
        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination){
            yPos--;
        } else if (xPos == xDestination && yPos == yDestination
        		& (xDestination == host_tableX + 20) & (yDestination == host_tableY - 20)) {
        	
        }
 
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, HOST_SIZE_X, HOST_SIZE_Y);
		//Image hostImg = Toolkit.getDefaultToolkit().getImage("stickfig.png");
	    //g.drawImage(hostImg, xPos, yPos, HOST_SIZE_X, HOST_SIZE_Y, null);
    }

    public boolean isPresent() {
        return true;
    }

	public void doneAnimating(){
		//System.out.println("Done animating in waiter gui, about to release semaphore");
		agent.releaseSemaphore();
		hasDestination = false;
		isAnimating = false;
	}
    
    public void DoBringToTable(CustomerAgent customer, int tableX, int tableY) {
        xDestination = tableX + 20;
        yDestination = tableY - 20;
        host_tableX = tableX;
        host_tableY = tableY;
    }

    public void DoLeaveCustomer() {
        xDestination = -20;
        yDestination = -20;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
