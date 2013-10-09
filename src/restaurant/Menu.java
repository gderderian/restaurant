package restaurant;

import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import java.util.*;

public class Menu {
	
	public Hashtable<String, Double> itemList;
	
	public Menu(){
		itemList = new Hashtable<String, Double>();
		itemList.put("Chicken", 1.75);
		itemList.put("Mac & Cheese", 7.95);
		//itemList.put("French Fries", 4.25);
		//itemList.put("Pizza", 7.95);
		//itemList.put("Pasta", 5.75);
		//itemList.put("Cobbler", 5.75);
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
	
	public void removeItem(String item){
		itemList.remove(item);
	}
	
	public String pickRandomItem() {
		Random randNum = new Random();
		int itemPickNum = randNum.nextInt(itemList.size());
		ArrayList<String> menuItems = new ArrayList<String>(itemList.keySet());
		String randItem = menuItems.get(itemPickNum);
		return randItem;
	}
	
}