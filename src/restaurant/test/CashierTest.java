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
	MockMarket market2;
	
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier");
		customer = new MockCustomer("mockcustomer");		
		waiter = new MockWaiter("mockwaiter");
		cook = new CookAgent("mockcook");
		market = new MockMarket("mockmarket");
		market2 = new MockMarket("mockmarket2");
	}	
	
	public void testOneNormalScenario(){ // Tests scenario: One order, fulfilled by the market, bill paid in full.
		
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
	
	public void testTwoNormalScenario(){ // Tests scenario: One order, fulfilled by TWO markets, 2 bills paid in full.
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.myChecks.size(), 0);	
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "+ cashier.log.toString(), 0, cashier.log.size());
						
		cashier.acceptMarketBill(market, 5.00);
				
		// check postconditions of the first check
		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.myChecks.size(), 1);	
		assertEquals("Cashier's first bill to pay should be for chicken.", cashier.myChecks.get(0).amount, 5.00);
		assertEquals("Cashier's first check should be for our market.", cashier.myChecks.get(0).market, market);
		assertEquals("Cashier's first check should be set to status pending.", cashier.myChecks.get(0).status, checkStatus.pending);
		assertEquals("Cashier's first check should be a marketCheck.", cashier.myChecks.get(0).type, checkType.marketCheck);
		
		// check to see if market received payment message
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
				cashier.pickAndExecuteAnAction());
		assertTrue("Cashier should have logged \"Received message acceptCashierPayment of 5.0\" but didn't. The log instead reads: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received message acceptCashierPayment of 5.0"));
		
		cashier.acceptMarketBill(market2, 15.00);
		
		// Check postconditions of the second check
		assertEquals("Cashier should have 2 bills in it. It doesn't.",cashier.myChecks.size(), 2);	
		assertEquals("Cashier's first bill to pay should be for chicken.", cashier.myChecks.get(1).amount, 15.00);
		assertEquals("Cashier's first check should be for our market.", cashier.myChecks.get(1).market, market2);
		assertEquals("Cashier's first check should be set to status pending.", cashier.myChecks.get(1).status, checkStatus.pending);
		assertEquals("Cashier's first check should be a marketCheck.", cashier.myChecks.get(1).type, checkType.marketCheck);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
			cashier.pickAndExecuteAnAction());

		// check to see if market received payment message
		assertTrue("Cashier should have logged \"Received message acceptCashierPayment of 5.0\" but didn't. The log instead reads: " 
			+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received message acceptCashierPayment of 5.0"));				
		
	}
	
	public void testThreeNormalScenario(){ // Tests scenario: Cashier told to calculate check and waiter receives notification that check is ready.
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.myChecks.size(), 0);	
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "+ cashier.log.toString(), 0, cashier.log.size());
		
		cashier.calculateCheck(waiter, customer, "Chicken");
		
		// check postconditions of the check
		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.myChecks.size(), 1);	
		assertEquals("Cashier's first check should be for chicken.", cashier.myChecks.get(0).choice, "Chicken");
		assertEquals("Cashier's first check should be for our waiter.", cashier.myChecks.get(0).waiter, waiter);
		assertEquals("Cashier's first check should be set to status pending.", cashier.myChecks.get(0).status, checkStatus.pending);
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		// check to see if waiter received message
		assertTrue("Waiter should have logged \"Received message hereIsCheck in amount 7.95\" but didn't. The log instead reads: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received message hereIsCheck in amount 7.95"));
		
	}
	
	public void testFourNormalScenario(){ // Tests scenario: Check gets created, waiter delivers to customer, customer pays exact amount.
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.myChecks.size(), 0);	
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "+ cashier.log.toString(), 0, cashier.log.size());
		
		cashier.calculateCheck(waiter, customer, "Mac & Cheese");
		
		// check postconditions of the check
		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.myChecks.size(), 1);	
		assertEquals("Cashier's first check should be for mac and cheese.", cashier.myChecks.get(0).choice, "Mac & Cheese");
		assertEquals("Cashier's first check should be for our waiter.", cashier.myChecks.get(0).waiter, waiter);
		assertEquals("Cashier's first check should be set to status pending.", cashier.myChecks.get(0).status, checkStatus.pending);
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		// check to see if waiter received message
				assertTrue("Waiter should have logged \"Received message hereIsCheck in amount 5.95\" but didn't. The log instead reads: " 
						+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received message hereIsCheck in amount 5.95"));
				
		// Move on to accept payment
		cashier.acceptPayment(customer, 5.95);
		assertEquals("Cashier's first check should be set to status paid.", cashier.myChecks.get(0).status, checkStatus.paid);
		assertEquals("Cashier's money should be what they had plus $5.95.", cashier.myMoney, 10000 + 5.95);
		
	}
	
	public void testFiveNormalScenario(){ // Tests scenario: Check gets created, waiter delivers to customer, customer pays more than their order so change is dispensed.
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.myChecks.size(), 0);	
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "+ cashier.log.toString(), 0, cashier.log.size());
		
		cashier.calculateCheck(waiter, customer, "Mac & Cheese");
		
		// check postconditions of the check
		assertEquals("Cashier should have 1 bill in it. It doesn't.",cashier.myChecks.size(), 1);	
		assertEquals("Cashier's first check should be for mac and cheese.", cashier.myChecks.get(0).choice, "Mac & Cheese");
		assertEquals("Cashier's first check should be for our waiter.", cashier.myChecks.get(0).waiter, waiter);
		assertEquals("Cashier's first check should be set to status pending.", cashier.myChecks.get(0).status, checkStatus.pending);
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		// check to see if waiter received message
		assertTrue("Waiter should have logged \"Received message hereIsCheck in amount 5.95\" but didn't. The log instead reads: " 
						+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received message hereIsCheck in amount 5.95"));
				
		// Move on to accept payment
		cashier.acceptPayment(customer, 6.95);
		assertEquals("Cashier's first check should be set to status paid.", cashier.myChecks.get(0).status, checkStatus.paid);
		assertEquals("Cashier's money should be what they had plus $5.95.", cashier.myMoney, 10000 + 5.95);
		assertTrue("Customer should have logged \"Received message dispenseChange. Change=1.0\" but didn't. The log instead reads: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received message dispenseChange. Change=1.0"));
		
	}
	
}
