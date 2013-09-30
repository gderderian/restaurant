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

    private RestaurantPanel restPanel;
    private String type;
    
    private JPanel addPersonPanel;
    private JButton addPersonButton = new JButton("Add");
    private JCheckBox hungryCheckbox = new JCheckBox();
	private JTextField personName = new JTextField();
	
	private static final int PERSON_GRID_ROWS = 2;
	private static final int PERSON_GRID_COLS = 1;
	
	private static final int PANE_SIZE_WIDTH_DIVIDE = 20;
	private static final int PANE_SIZE_HEIGHT_DIVIDE = 5;
	
    /**
     * Constructor for ListPanel.  Sets up the GUIs for displaying customers or waiters.
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(RestaurantPanel rp, String type) {
    	
        restPanel = rp;
        this.type = type;
        
        setLayout(new GridLayout(PERSON_GRID_ROWS, PERSON_GRID_COLS));
        this.setBorder(BorderFactory.createTitledBorder(type + " Management"));

        addPersonButton.addActionListener(this);
        personName.addKeyListener(this);
        
        hungryCheckbox.setText("Hungry?");
        
        addPersonPanel = new JPanel();
        addPersonPanel.setLayout(new GridLayout(PERSON_GRID_ROWS, PERSON_GRID_COLS));
 
        addPersonPanel.add(personName);
        
        if (type == "Customers"){
            addPersonPanel.add(hungryCheckbox);
        }

        addPersonPanel.add(addPersonButton);
        
        hungryCheckbox.setEnabled(false);
       
        add(addPersonPanel);
        
        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        
        pane.setViewportView(view);
        add(pane);
      
        
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
            Dimension buttonSize = new Dimension(paneSize.width - PANE_SIZE_WIDTH_DIVIDE, (int) (paneSize.height / PANE_SIZE_HEIGHT_DIVIDE));
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
