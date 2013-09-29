package restaurant;

import java.util.Hashtable;
import java.util.Set;

public class Menu {
	
	public Hashtable<String, Double> itemList;
	
	public Menu(){
		itemList = new Hashtable<String, Double>();
		itemList.put("Lemonade", 1.75);
		itemList.put("Mac & Cheese", 7.95);
		itemList.put("French Fries", 4.25);
		itemList.put("Pizza", 7.95);
		itemList.put("Pasta", 5.75);
		itemList.put("Cobbler", 5.75);
	}
	
	public String getAt(int index){
		
		String returnName;
		switch(index){
			case 1: returnName = "Lemonade"; break;
			case 2: returnName = "Mac & Cheese"; break;
			case 3: returnName = "French Fries"; break;
			case 4: returnName = "Pizza"; break;
			case 5: returnName = "Pasta"; break;
			case 6: returnName = "Cobbler"; break;
			default: returnName = ""; break;
		}
		return returnName;
		
	}
	
	public String displayMenu(){
		
		String menuHTML = "";
		Set<String> itemSet = itemList.keySet();
		
		menuHTML = menuHTML + "<table>";
		for(String individItem : itemSet){
			menuHTML += "<tr><td>" + individItem + "</td><td>$" + itemList.get(individItem).toString() + "</td></tr>";
		}
		menuHTML = menuHTML + "</table>";
		
		return menuHTML;
		
	}
	
	
	
}