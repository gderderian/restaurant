package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;




//import javax.swing.Image;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener {

	AnimationPanel animationPanel = new AnimationPanel();
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one */
    private JPanel infoPanel;
    private JLabel infoLabel;
    private JCheckBox stateCB;
    
    private JPanel leftPanel;
    
    private static final int WINDOW_BOUND = 50;
    private static final int REST_GRID_COLS = 1;
    private static final int REST_GRID_ROWS = 2;
    private static final int INFO_PANEL_ROWS = 1;
    private static final int INFO_PANEL_COLS = 2;
    private static final int INFO_PANEL_X_PADDING = 30;
    private static final int INFO_PANEL_Y_PADDING = 0;
    private static final double HALF = .5;
    private static final double TWO_THIRDS = .6725;
    private static final double ONE_THIRD = .125;

    private Object currentPerson;
    
	private JPanel controlPanel;
    private JButton toggleTimerButton = new JButton("Pause");
    private boolean isPaused = false;

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
    	
        int WINDOWX = 1000;
        int WINDOWY = 550;
    	
    	setBounds(WINDOW_BOUND, WINDOW_BOUND, WINDOWX, WINDOWY);

        setLayout(new BoxLayout((Container) getContentPane(), BoxLayout.X_AXIS));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        
        Dimension leftDim = new Dimension((int) (WINDOWX * HALF), WINDOWY);
        leftPanel.setPreferredSize(leftDim);
        leftPanel.setMinimumSize(leftDim);
        leftPanel.setMaximumSize(leftDim);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Settings & Controls"));
        
        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * TWO_THIRDS));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        leftPanel.add(restPanel);
        
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * ONE_THIRD)); // Takes up .125 of the height of the window in pixels
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);

        infoPanel.setLayout(new GridLayout(INFO_PANEL_ROWS, INFO_PANEL_COLS, INFO_PANEL_X_PADDING, INFO_PANEL_Y_PADDING));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><p>Enter a customer or waiter name above and click add to begin!</p></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        leftPanel.add(infoPanel);
        
        add(leftPanel);
        animationPanel.setBorder(BorderFactory.createTitledBorder("Restaurant Animation"));
        add(animationPanel);
        
        controlPanel = new JPanel();
        Dimension controlPanelDim = new Dimension(WINDOWX, (int) (WINDOWY * ONE_THIRD));
        controlPanel.setPreferredSize(controlPanelDim);
        controlPanel.setMinimumSize(controlPanelDim);
        controlPanel.setMaximumSize(controlPanelDim);
        controlPanel.setBorder(BorderFactory.createTitledBorder("Agent Controls"));
        toggleTimerButton.addActionListener(this);
        controlPanel.add(toggleTimerButton);
        leftPanel.add(controlPanel);
        
    }
    
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        stateCB.setVisible(true);
        currentPerson = person;

        if(person instanceof WaiterAgent){
        	stateCB.setVisible(false);
        	WaiterAgent waiter = (WaiterAgent) person;
        	infoLabel.setText("<html>Waiter Name: " + waiter.getName() + "</html>");
        }
        
        if (person instanceof CustomerAgent) {
            CustomerAgent customer = (CustomerAgent) person;
            stateCB.setText("Hungry?");
            stateCB.setSelected(customer.getGui().isHungry());
            stateCB.setEnabled(!customer.getGui().isHungry());
            infoLabel.setText("<html>Customer Name: " + customer.getName() + "</html>");
        }
        infoPanel.validate();
    }
    
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerAgent) {
                CustomerAgent c = (CustomerAgent) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
        } else if (e.getSource() == toggleTimerButton) {
        	if (isPaused == true) {
        		toggleTimerButton.setText("Pause");
            	restPanel.toggleTimer();
            	isPaused = false;
        	} else {
        		toggleTimerButton.setText("Resume");
            	restPanel.toggleTimer();
            	isPaused = true;
        	}
        	
        }
    }
    
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(CustomerAgent c) {
        if (currentPerson instanceof CustomerAgent) {
            CustomerAgent cust = (CustomerAgent) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("Grant's csci201 Restaurant - v2.0");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
