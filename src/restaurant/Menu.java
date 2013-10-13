package restaurant;

import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import java.util.*;

public class Menu {
	
	public Hashtable<String, Double> itemList;
	
	public Menu(){
		itemList = new Hashtable<String, Double>();
		itemList.put("Chicken", 7.95);
		itemList.put("Mac & Cheese", 5.95);
		itemList.put("French Fries", 2.50);
		itemList.put("Pizza", 5.95);
		itemList.put("Pasta", 6.75);
		itemList.put("Cobbler", 6.50);
	}

	// Utilities
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
	
	public double getPriceofItem(String item){
		System.out.println("Getting price of item: " + item);
		double priceOfItem = 0;
		priceOfItem = itemList.get(item);
		System.out.println("Price of item is: " + priceOfItem);
		return priceOfItem;
		
	}
	
	public String pickRandomItem() {
		Random randNum = new Random();
		int itemPickNum = randNum.nextInt(itemList.size());
		ArrayList<String> menuItems = new ArrayList<String>(itemList.keySet());
		String randItem = menuItems.get(itemPickNum);
		return randItem;
	}
	
	public String pickRandomItemWithinCost(double maxCost) {
		int counter = 0;
		while (true){
			Random randNum = new Random();
			int itemPickNum = randNum.nextInt(itemList.size());
			ArrayList<String> menuItems = new ArrayList<String>(itemList.keySet());
			String randItem = menuItems.get(itemPickNum);
			counter++;
			if (itemList.get(randItem) <= maxCost) {
				return randItem;
			}
			if (counter > itemList.size()){
				return "";
			}
		}
	}
	
}