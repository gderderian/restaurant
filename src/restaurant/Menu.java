package restaurant;

import java.util.Hashtable;

public class Menu {
	
	public Hashtable<String, Double> itemList;
	
	public Menu(){
		itemList = new Hashtable<String, Double>();
		itemList.put("Lemonade", 1.50);
		itemList.put("Water", 1.00);
		itemList.put("French Fries", 4.50);
		itemList.put("Pizza", 7.95);
		itemList.put("Pasta", 6.75);
		itemList.put("Cobbler", 5.50);
	}
	
	public String getAt(int index){
		
		String returnName;
		switch(index){
			case 1: returnName = "Lemonade"; break;
			case 2: returnName = "Water"; break;
			case 3: returnName = "French Fries"; break;
			case 4: returnName = "Pizza"; break;
			case 5: returnName = "Pasta"; break;
			case 6: returnName = "Cobbler"; break;
			default: returnName = ""; break;
		}
		return returnName;
		
	}
	
}