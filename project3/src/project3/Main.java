package project3;
import java.util.PriorityQueue;
import java.io.IOException;


public class Main {
	
	/********* Program Flow**********
	 * 1.read arrival.txt and construct arrivalQue from it
	 * 	arrivalQue restores the customer state when they arrive at the store. 
	 * 2.construct endShoppingQue from arrivalQue
	 * 	endShoppingQue restores the customer state when they finish shopping.
	 * 3.construct endCheckoutQue from endShoppingQue
	 * 	endCheckoutQue restores the customer state when they finish checkout.
	 * 4.merge three queues into totalQue
	 * 	totalQue stores all the states of the customer states from arrival to checkout.
	 * 5.print totalQue to output.txt
	 */
	
	private static PriorityQueue<Customer> totalQue = new PriorityQueue<>();
	private static PriorityQueue<Customer> arrivalQue = new PriorityQueue<>();
	private static PriorityQueue<Customer> endShoppingQue = new PriorityQueue<>();
	private static PriorityQueue<Customer> endCheckoutQue = new PriorityQueue<>();
	
	
	private static String customerInfo = "arrival.txt";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			arrivalQue = Utility.constructArrivalQue(customerInfo); //construct arrivalQue
			endShoppingQue = Utility.constructEndShoppingQue(arrivalQue); //construct endShoppingQue
			endCheckoutQue = Utility.constructEndCheckoutQue(endShoppingQue);//construct endCheckoutQue
			
			endShoppingQue = Utility.refreshEndShoppingQue();
			//construct totalQue
			totalQue = arrivalQue;
			for(Customer cst: endShoppingQue)
				totalQue.add(cst);
			for(Customer cst: endCheckoutQue)
				totalQue.add(cst);
			
			//print simulationResult
			Utility.printSimulationResult(totalQue);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
