package restaurant.test;

import junit.framework.TestCase;
import restaurant.*;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockWaiter;
import restaurant.CashierAgent.Check;
import restaurant.CashierAgent.checkStatus;;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class CashierTest extends TestCase
{
	
	//these are instantiated for each test separately via the setUp() method.
	CashierAgent cashier;
	WaiterAgent waiter;
	CustomerAgent customer;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier");		
		customer = new CustomerAgent("mockcustomer");		
		waiter = new WaiterAgent("mockwaiter", 1, 2);
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier; //You can do almost anything in a unit test.			
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.myChecks.size(), 0);	
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//step 1 of the test
		//public Bill(Cashier, Customer, int tableNum, double price) {
		//Check bill = new Check(waiter, customer, "Chicken");
		cashier.calculateCheck(waiter, customer, "Chicken");
		
		
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.myChecks.size(), 1);
		
		// assertFalse("Cashier's scheduler should have returned false (no actions to do on a bill from a waiter), but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		//step 2 of the test
		cashier.acceptPayment(customer, 10.00);
		
		//check postconditions for step 2 / preconditions for step 3
		//assertTrue("CashierBill should contain a bill with state == customerApproached. It doesn't.",
		//		cashier.myChecks.get(0).status == checkStatus.pending);
		
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
					cashier.myChecks.get(0).customer == customer);
		
		assertTrue("CashierBill should contain changeDue == 0.0. It contains something else instead: $" 
				+ cashier.myChecks.get(0).amount, cashier.myChecks.get(0).amount == 0);
		
		
		
		//step 4
		assertTrue("CashierBill should contain a bill with state == done. It doesn't.",
				cashier.myChecks.get(0).status == checkStatus.paid);
		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
	
	}//end one normal customer scenario
	
	
}
