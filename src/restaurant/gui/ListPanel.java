package restaurant.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and waiters
 */
public class ListPanel extends JPanel implements ActionListener, KeyListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private JButton addPersonB = new JButton("Add");

    private RestaurantPanel restPanel;
    private String type;
    
    private JPanel addPersonPanel;
    private JButton addPersonButton = new JButton("Add");
    private JCheckBox hungryCheckbox = new JCheckBox();
	private JLabel enterNamePrompt = new JLabel("Name:");
	private JLabel hungryPrompt = new JLabel("Hungry?");
	private JTextField personName = new JTextField();
	
	private static final int PERSON_GRID_ROWS = 1;
	private static final int PERSON_GRID_COLS = 5;

	private JPanel togglePanel;
    private JButton toggleTimerButton = new JButton("Pause");
    private boolean isPaused = false;
	
    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(RestaurantPanel rp, String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

        addPersonB.addActionListener(this);

        this.setBorder(BorderFactory.createTitledBorder("Animation Control"));
        
        addPersonPanel = new JPanel();
        addPersonPanel.setLayout(new GridLayout(PERSON_GRID_ROWS, PERSON_GRID_COLS));
        
        addPersonPanel.add(personName);
        addPersonPanel.add(enterNamePrompt);
        addPersonPanel.add(personName);
        
        if (type == "Customers"){
            addPersonPanel.add(hungryPrompt);
            addPersonPanel.add(hungryCheckbox);
        }

        
        addPersonPanel.add(addPersonButton);
        addPersonButton.addActionListener(this);
        
        hungryCheckbox.setEnabled(false);
        
        personName.addKeyListener(this);
       
        add(addPersonPanel);
        
        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        
        if (type == "Customers"){
        	pane.setViewportView(view);
        	add(pane);
        }

        togglePanel = new JPanel();
        togglePanel.setBorder(BorderFactory.createTitledBorder("Animation Control"));
        togglePanel.setLayout(new GridLayout(1, 1));
        
        toggleTimerButton.addActionListener(this);
        togglePanel.add(toggleTimerButton);
        
        Dimension toggleDim = new Dimension(50, 50);
        toggleTimerButton.setPreferredSize(toggleDim);
        toggleTimerButton.setMinimumSize(toggleDim);
        toggleTimerButton.setMaximumSize(toggleDim);
        
        add(togglePanel);
        
    }
    
    public void keyPressed(KeyEvent e) {
    	
    }
 
    public void keyReleased(KeyEvent e) {
    	
    	if (personName.getText().length() == 0) {
    		hungryCheckbox.setEnabled(false);
    	} else {
    		hungryCheckbox.setEnabled(true);
    	}
    	
    }
    
    public void keyTyped(KeyEvent e) { }
    
    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
        
    	if (e.getSource() == addPersonButton) {
        	
        	if (hungryCheckbox.isSelected() == true) {
        		addPerson(personName.getText(), true);
        	} else {
        		addPerson(personName.getText(), false);
        	}
        	
        	personName.setText("");
        	hungryCheckbox.setSelected(false);
        	
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
        	
        } else {
        	
        	for (JButton temp:list){
                if (e.getSource() == temp)
                    restPanel.showInfo(type, temp.getText());
            }
        	
        }
    }

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addPerson(String name, boolean isHungry) {
        if (name != null) {
            JButton button = new JButton(name);
            button.setBackground(Color.white);
            Dimension paneSize = pane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - 20, (int) (paneSize.height / 7));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            list.add(button);
            view.add(button);
            restPanel.addPerson(type, name, isHungry);
            restPanel.showInfo(type, name);
            validate();
        }
    }
    
    public void addWaiter(String name) {
        if (name != null) {
            restPanel.addPerson("Waiters", name, false);
            validate();
        }
    }
    
    
}
