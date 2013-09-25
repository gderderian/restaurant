package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.CustomerAgent.AgentState;

import java.awt.*;

public class HostGui implements Gui {

    private HostAgent agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    public int xDestination = -20, yDestination = -20, host_tableX, host_tableY;//default start position

    public static final int xTable = 200;
    public static final int yTable = 250;
    
    // Lab 2
    private static final int HOST_SIZE_X = 20;
    private static final int HOST_SIZE_Y = 20;

    private boolean atDesk = false;
    
    public HostGui(HostAgent agent) {
        this.agent = agent;
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
        else if (xPos == -20 && yPos == -20 && atDesk == false) {
        	// agent.msgAtDesk();
        	atDesk = true;
        } else if (xPos == xDestination && yPos == yDestination
        		& (xDestination == host_tableX + 20) & (yDestination == host_tableY - 20)) {
           // agent.msgAtTable();
        }
        
        if (xPos != -20 && yPos != -20) {
        	atDesk = false;
    	}
    }

    public void draw(Graphics2D g) {
        //g.setColor(Color.MAGENTA);
        //g.fillRect(xPos, yPos, HOST_SIZE_X, HOST_SIZE_Y);
		Image hostImg = Toolkit.getDefaultToolkit().getImage("stickfig.png");
	    g.drawImage(hostImg, xPos, yPos, HOST_SIZE_X, HOST_SIZE_Y, null);
    }

    public boolean isPresent() {
        return true;
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
