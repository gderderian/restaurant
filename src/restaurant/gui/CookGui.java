package restaurant.gui;

import restaurant.CookAgent;

import java.awt.*;
import java.util.ArrayList;

public class CookGui implements Gui {

    private CookAgent agent = null;

	RestaurantGui gui;
    
    private int xPos = 230, yPos = 445;
    public int xDestination = 230, yDestination = 445, host_tableX, host_tableY;
    
    private static final int COOK_SIZE_X = 20;
    private static final int COOK_SIZE_Y = 20;

	boolean isAnimating = false;
	boolean hasDestination = false;
	
	String carryingOrderText = "";
	
	public ArrayList<String> platingFood;
	public ArrayList<String> cookingFood;
	
    public CookGui(CookAgent a) {
    	agent = a;
    	carryingOrderText = "";
		platingFood = new ArrayList<String>();
		cookingFood = new ArrayList<String>();
    }
    
    public CookGui(CookAgent a, RestaurantGui g) {
    	agent = a;
    	gui = g;
    	carryingOrderText = "";
		platingFood = new ArrayList<String>();
		cookingFood = new ArrayList<String>();
    }
    
    public CookGui(CookAgent a, RestaurantGui g, int startX, int startY, int indexNum) {
    	agent = a;
    	gui = g;
    	xPos = startX;
    	yPos = startY;
    	xDestination = startX;
    	yDestination = startY;
    	carryingOrderText = "";
    	hasDestination = false;
		platingFood = new ArrayList<String>();
		cookingFood = new ArrayList<String>();
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
        } else if (xPos == xDestination && yPos == yDestination){
        	if (isAnimating){
        		doneAnimating();
        	}	
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillRect(xPos, yPos, COOK_SIZE_X, COOK_SIZE_Y);
		if (!carryingOrderText.equals("")){
			g.drawString(carryingOrderText, xPos, yPos);
		}
		
		for (int i = 0; i < platingFood.size(); i++){
			g.drawString(platingFood.get(i), 225, 390 + (i * 10));
		}
		
		for (int j = 0; j < cookingFood.size(); j++){
			g.drawString(cookingFood.get(j), 225, 495 + (j * 10));
		}
		
    }

    public boolean isPresent() {
        return true;
    }
    
	public void beginAnimate(){
		isAnimating = true;
	}

	public void doneAnimating(){
		hasDestination = false;
		isAnimating = false;
		agent.releaseSemaphore();
	}

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    public void setCarryText(String carryText){
    	carryingOrderText = carryText;
    }
   
}
