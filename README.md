##Restaurant Project Repository

###Student Information
  + Name: Grant Derderian
  + USC Email: gderderi@usc.edu
  + USC ID: 9679170673
  + Lecture: Prof. Wilczynski - MW 4:00-5:20p
  + Lab: Tues. 6:00-7:50p

### Compile Instructions (v2.2)
To compile and run my restaurant program, clone this GitHub repo into a new directory on your local computer. Then, follow these instructios:
+ Open the Eclipse IDE on your computer.
+ Choose File -> New -> Other. Select the Java Project from Exisitng Ant Buildfile in the list of options
+ Click on Browse, navigate to the directory of the repo you cloned from my assignment on GitHub, and then select my build.xml file.
+ Click the Link to the buildfile in the file system checkbox and then click on finish.

__To run the program__, navigate to the `RestaurantGui.java` file located within `agents/src/restaurant.gui/`. Then, click the small arrow next to the green play button in the Eclipse toolbar and select `RestaurantGui` from the dropdown list. The program will now run. To close it, click the red stop sign in Eclipse.

__To test the program__, navigate to the `CashierTest.java` file located within `agents/src/restaurant.test/`. If you receive a message about JUnit not being in your build path, accept Eclipse's warning/suggestion to add it. If you don't see this, go to Project -> Properties in Eclipse's menu. Click on Java Build Path in the left menu, then on Libraries in the upper menu, and then on Add Library. Click JUnit, then select JUnit 3, and then click Finish. Click OK to exit the Project Settings dialog. With the `CashierText.java` file still open, click the small arrow next to the green play button in the Eclipse toolbar and select `CashierTest` from the dropdown list. The test will now run, showing you the results on the left.

### Resources
  + [Design Doc and Interaction Diagrams](https://github.com/usc-csci201-fall2013/restaurant_gderderi/blob/master/docs/DesignDoc.md)

### Testing Information
This program has several coded hacks which allow you to force conditions to occurr in order to expedite scenario testing. Customers normally order a random item off of the available menu. However, to force a specific customer to order a specific food, name them (case-sensitive) after that food. These are the available food options for which you can also name customers:

```
Chicken
Mac & Cheese
French Fries
Pizza
Pasta
Cobbler
```

By default, all customers also start with $15.00 when they get created and enter the restaurant. Everything they order deducts from that amount. However, to force a customer to have a more specific amount of money, name them the following (case-sensitive) string to have the indicated amount of money:

+ `reallycheap` - $1.00
+ `cheap` - $2.55
+ `somemoney` - $7.00
+ `lotsofmoney` - $17.00
+ `tonsofmoney` - $25.00