package restaurant.gui;

import restaurant.CustomerAgent;

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
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	JFrame animationFrame = new JFrame("Restaurant Animation");
	AnimationPanel animationPanel = new AnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    
    // Added as part of lab
    private JPanel demoPanel;
    private JLabel demoLabel;
    
    // Lab 2
    private static final int DEMO_IMAGE_X = 64;
    private static final int DEMO_IMAGE_Y = 64;
    private static final int WINDOW_BOUND = 50;
    private static final int REST_GRID_COLS = 1;
    private static final int REST_GRID_ROWS = 2;
    private static final int INFO_PANEL_ROWS = 1;
    private static final int INFO_PANEL_COLS = 2;
    private static final int INFO_PANEL_X_PADDING = 30;
    private static final int INFO_PANEL_Y_PADDING = 0;
    private static final int DEMO_PANEL_ROWS = 1;
    private static final int DEMO_PANEL_COLS = 2;
    private static final int DEMO_PANEL_X_PADDING = 30;
    private static final int DEMO_PANEL_Y_PADDING = 0;

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        int WINDOWX = 550;
        int WINDOWY = 550;

        animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        animationFrame.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
        animationFrame.setVisible(true);
    	animationFrame.add(animationPanel); 
    	
    	setBounds(WINDOW_BOUND, WINDOW_BOUND, WINDOWX, WINDOWY);

        //setLayout(new BoxLayout((Container) getContentPane(), 
        //		BoxLayout.Y_AXIS));
    	setLayout(new GridLayout(REST_GRID_ROWS, REST_GRID_COLS));

        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .6));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        add(restPanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .25));
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
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        add(infoPanel);
        
        
        // Add new demo panel for lab
        Dimension demoDim = new Dimension(WINDOWX, (int) (WINDOWY * .10));
        demoPanel = new JPanel();
        demoPanel.setPreferredSize(demoDim);
        demoPanel.setMinimumSize(demoDim);
        demoPanel.setMaximumSize(demoDim);
        demoPanel.setBorder(BorderFactory.createTitledBorder("Welcome Message from Grant"));
        demoPanel.setLayout(new GridLayout(DEMO_PANEL_ROWS, DEMO_PANEL_COLS, DEMO_PANEL_X_PADDING, DEMO_PANEL_Y_PADDING));
        demoLabel = new JLabel(); 
        demoLabel.setText("<html><pre><i>Hello world, my name is Grant!</i></pre></html>");
        
        // Add image too (as JButton)
        JButton shieldButton = new JButton();
        Image uscShield = null;
		try {
			uscShield = ImageIO.read(getClass().getResource("small_shield.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        shieldButton.setIcon(new ImageIcon(uscShield));
        shieldButton.setPreferredSize(new Dimension(DEMO_IMAGE_X, DEMO_IMAGE_Y));
        shieldButton.setMinimumSize(new Dimension(DEMO_IMAGE_X, DEMO_IMAGE_Y));
        shieldButton.setMaximumSize(new Dimension(DEMO_IMAGE_X, DEMO_IMAGE_Y));
        
        demoPanel.add(shieldButton);
       
        demoPanel.add(demoLabel);
        
        // add(demoPanel);
        
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

        if (person instanceof CustomerAgent) {
            CustomerAgent customer = (CustomerAgent) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there?
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
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
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
