package restaurant.test;

import junit.framework.TestCase;
import restaurant.*;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockWaiter;
import restaurant.CashierAgent.Check;
import restaurant.CashierAgent.checkStatus;
import restaurant.CookAgent;
import restaurant.MarketAgent.orderStatus;
import restaurant.test.mock.MockMarket;
import restaurant.CashierAgent.checkType;


public class CashierTest extends TestCase
{
	
	//these are instantiated for each test separately via the setUp() method.
	CashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	CookAgent cook;
	MockMarket market;
	
	
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier");
		customer = new MockCustomer("mockcustomer");		
		waiter = new MockWaiter("mockwaiter");
		cook = new CookAgent("mockcook");
		market = new MockMarket("mockmarket");
	}	
	
	public void testOneNormalScenario(){
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.myChecks.size(), 0);	
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "+ cashier.log.toString(), 0, cashier.log.size());
				
		cashier.acceptMarketBill(market, 10.00);
		
		// check postconditions of the check
		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.myChecks.size(), 1);	
		assertEquals("Cashier's first bill to pay should be for chicken.", cashier.myChecks.get(0).amount, 10.00);
		assertEquals("Cashier's first check should be for our market.", cashier.myChecks.get(0).market, market);
		assertEquals("Cashier's first check should be set to status pending.", cashier.myChecks.get(0).status, checkStatus.pending);
		assertEquals("Cashier's first check should be a marketCheck.", cashier.myChecks.get(0).type, checkType.marketCheck);
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		// check to see if market received payment message
		assertTrue("Cashier should have logged \"Received message acceptCashierPayment of 10.0\" but didn't. The log instead reads: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received message acceptCashierPayment of 10.0"));
				
	}
	

	
	
}
