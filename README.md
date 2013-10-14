##Restaurant Project Repository

###Student Information
  + Name: Grant Derderian
  + USC Email: gderderi@usc.edu
  + USC ID: 9679170673
  + Lecture: Prof. Wilczynski - MW 4:00-5:20p
  + Lab: Tues. 6:00-7:50p

### Compile Instructions
To compile, clone this GitHub repo into a new directory on your local computer. Then, enter these commands into a new terminal/command line:
```
ant compile
ant run.gui
```
The program will then begin to run. To exit, close the terminal/command line window you opened.

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