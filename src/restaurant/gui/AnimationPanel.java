package restaurant.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 500;
    private final int WINDOWY = 550;
    private Image bufferImage;
    private Dimension bufferSize;
    
    private final int MAIN_TIMER = 20;
    private final int TABLE_X = 50;
    private final int TABLE_Y = 50;
    private final int SCREEN_RECT_X_COORD = 0;
    private final int SCREEN_RECT_Y_COORD = 0;

    private List<Gui> guis = new ArrayList<Gui>();
    
	Timer timer = new Timer(MAIN_TIMER, this);
	private boolean timerIsRunning = false;
    
    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
        
    	timer.start();
    	timerIsRunning = true;
    }

    public void toggleTimer() {
    	System.out.println(timerIsRunning);
    	if (timerIsRunning == true){
    		timer.stop();
    		timerIsRunning = false;
    	} else {
        	timer.start();
        	timerIsRunning = true;
    	}	
    }
    
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

    public void paintComponent(Graphics g) {
    	
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(SCREEN_RECT_X_COORD, SCREEN_RECT_Y_COORD, WINDOWX, WINDOWY);

        // Table 1
        g2.setColor(Color.ORANGE);
        g2.fillRect(150, 150, TABLE_X, TABLE_Y);

        // Table 2
        g2.setColor(Color.ORANGE);
        g2.fillRect(150, 275, TABLE_X, TABLE_Y);
        
        // Table 3
        g2.setColor(Color.ORANGE);
        g2.fillRect(275, 150, TABLE_X, TABLE_Y);
        
        // Table 4
        g2.setColor(Color.ORANGE);
        g2.fillRect(275, 275, TABLE_X, TABLE_Y);
        
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(WaiterGui gui) {
        guis.add(gui);
    }
}
