package restaurant;

import java.util.Hashtable;

public class Menu {
	
	Hashtable<String, Double> itemList = new Hashtable<String, Double>();
	
	public Menu(){
		
		itemList.put("Lemonade", 1.50);
		itemList.put("Water", 1.00);
		itemList.put("French Fries", 4.50);
		itemList.put("Pizza", 7.95);
		itemList.put("Pasta", 6.75);
		itemList.put("Cobbler", 5.50);
		
	}
	
}