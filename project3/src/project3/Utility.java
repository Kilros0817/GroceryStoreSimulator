package project3;

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;

public class Utility {
	
	//total number of lanes
	public static int totalCnt = 12;
	//number of express lanes
	public static int expressCnt = 6;
	//laneList is for managing lanes in the store.
	public static ArrayList<CheckoutLane> laneList = new ArrayList<>(totalCnt);
	
	/********constructArrivalQue**********
	 * function: construct arrivalQue from the arrival.txt
	 *  read line -> split by tab -> construct Customer -> add to que
	 */
	public static PriorityQueue<Customer> constructArrivalQue(String path) throws FileNotFoundException, IOException { 
    	try (BufferedReader file = new BufferedReader(new FileReader(path))) {
    	    String line;
    	    PriorityQueue<Customer> que = new PriorityQueue<>(); 
    	    int num = 0;
    	    line = file.readLine();
    	    while (line != null && line.length() > 0) {
    	        Customer w_customer = new Customer();
    	    	String[] values = line.split("\t");
    	    	w_customer.setNum(num++);
    	    	w_customer.setTimeStamp(Double.valueOf(values[0]));
    	    	w_customer.setItemCount(Integer.valueOf(values[1]));
    	    	w_customer.setItemTime(Double.valueOf(values[2]));
    	    	w_customer.setStatus(CustomerStatus.Arrival);
	    		que.add(w_customer);
	    		
	    		line = file.readLine();
    	    }
    	    return que;
    	}
    }
	
	/********initializeQueList**********
	 * function: initialize lane list (set express and regular lanes) 
	 *  param: numExp -- number of express lane in the store.
	 */
	public static void initializeQueList() {
		for(int i = 0; i < 12; i++ ) {
			if (i < expressCnt)
				laneList.add(new CheckoutLane(i, LaneType.Express));
			else
				laneList.add(new CheckoutLane(i, LaneType.Regular));
		}
	}
	
	/********constructEndShoppingQue**********
	 * function: construct endShoppingQue from the arrivalQue
	 *  create clone of customer state and set/update status. 
	 */
	public static PriorityQueue<Customer> constructEndShoppingQue(PriorityQueue<Customer> arrivalQue) throws IOException{
		PriorityQueue<Customer> pq = new PriorityQueue<Customer>();
		for (Customer cst : arrivalQue) {
			Customer clone = cst.deepCopyUsingSerialization();
			clone.setTimeStamp(cst.getTimeStamp() + cst.getItemTime() * cst.getItemCount());
			clone.setStatus(CustomerStatus.EndShopping);
			clone.setFinishShoppingTimeStamp(clone.getTimeStamp());
			pq.add(clone);
		}
		return pq;
	}
	
	//checkoutQue is the temp to construct endCheckoutQue
	private static PriorityQueue<Customer> checkoutQue = new PriorityQueue<>();
	//endShoppingQuetem is the temp to reconstruct endShoppingQue
	private static PriorityQueue<Customer> endShoppingQueTemp = new PriorityQueue<>();
	
	/********refreshLaneList**********
	 * function: when a customer finish shopping, pop customers who are ahead of the customer in time.
	 *  pop customer -> add it to checkoutQue
	 *  param: p_timeStam -- represent the time stamp of the customer just after shopping
	 */
	private static void refreshLaneList(double p_timeStamp) {
		for (CheckoutLane lane : laneList) {
			while(!lane.getWaitingQueue().isEmpty()) {
				if (lane.getWaitingQueue().peek().getTimeStamp() <= p_timeStamp )
				{
					Customer cst = lane.getWaitingQueue().remove();
					cst.setCstNumLaneAfter(lane.getWaitingQueue().size());
					checkoutQue.add(cst);
				}
				else
					break;
			}
		}
	}

	//get the list of express lines in laneList
	private static ArrayList<CheckoutLane> getExpressList()
	{
		ArrayList<CheckoutLane> exLaneList = new ArrayList<>();
		for (CheckoutLane lane : laneList) {
			if (lane.getlType() == LaneType.Express)
				exLaneList.add(lane);
		}
		
		return exLaneList;
	}
	
	/********getShortestLaneNum**********
	 * function: get the number of shortest lane for the customer
	 * 	cst -> check its item count -> get the shortest lane num according the count
	 * param: cst -- customer just finished shopping
	 */
	private static int getShortestLaneNum(Customer cst)
	{
		if (cst.getItemCount() > 12) {
			ArrayList<CheckoutLane> tempList = getExpressList();
			Collections.sort(tempList, new LaneComparator());
			return tempList.get(0).getLaneNum();
		} else {
			Collections.sort(laneList, new LaneComparator());
			return laneList.get(0).getLaneNum();
		}
	}
	
	//get lane using its number
	private static CheckoutLane getLane(int laneNum) {
		CheckoutLane clane = new CheckoutLane();
		for (CheckoutLane lane : laneList) {
			if (lane.getLaneNum() == laneNum)
				clane = lane;
		}
		return clane;
	}
	
	
	/********setCheckoutTime**********
	 * function: set the state of customer after checkout 
	 * 	line -> get checkout time of the last customer in the line
	 * 	cst	 -> set the check out time of cst -> set status as EndCheckout	
	 * 
	 * param: laneNum -- lane number that the customer get checked out
	 * 		  cst -- customer
	 */
	private static void setCheckoutTime(Customer cst) {
		double timeStamp = 0.0;
		CheckoutLane lane = getLane(cst.getLaneNum());
		Queue<Customer> queCst = lane.getWaitingQueue();
		
		double lastCheckoutTime = cst.getTimeStamp();
		if (queCst.size() != 0) {
			Object[] cArray = queCst.toArray();
			lastCheckoutTime = ((Customer)cArray[cArray.length - 1]).getTimeStamp();
		}
		
		cst.setWaitingTime(lastCheckoutTime - cst.getTimeStamp());
		
		if (lane.getlType() == LaneType.Regular) 
			timeStamp = lastCheckoutTime + cst.getItemCount() * 0.05 + 2;
		else
			timeStamp = lastCheckoutTime + cst.getItemCount() * 0.1 + 1;
		
		cst.setStatus(CustomerStatus.EndCheckout);
		cst.setTimeStamp(timeStamp);
	}
	
	
	/********constructCheckoutQue**********
	 * function: add customer into a checkout lane and determine his checkout state
	 * 	cst -> refresh lanes using cst's time stamp
	 * 		-> get the shortest lane he can use	and set his lane number property
	 * 		-> set cst's cstNumLaneBefore as the current size of the lane
	 * 		-> create clone of cst and put it into endShoppingQueTemp
	 * 		-> set cst's checkout time stamp, status, wasting time
	 * 		-> put cst into the lane
	 * param: customer just finished shopping
	 */
	public static void constructCheckoutQue(Customer cst) {
		refreshLaneList(cst.getTimeStamp());
		int laneNum = getShortestLaneNum(cst);
		cst.setLaneNum(laneNum);
		cst.setCstNumLaneBefore(getLane(laneNum).getWaitingQueue().size());
	
		Customer clone = cst.deepCopyUsingSerialization();
		endShoppingQueTemp.add(clone);
		
		setCheckoutTime(cst);
		getLane(laneNum).getWaitingQueue().add(cst);
		
	}
	
	/********constructEndCheckoutQue**********
	 * function: construct endCheckoutQue from endShoppingQue
	 * 	endShoppingQue -> pop one by one -> add customer into check out lanes
	 * 		-> pop all customers in lanes and add them to checkoutQue
	 * param: endShoppingQue -- priority queue of all customers end shopping
	 */
	public static PriorityQueue<Customer> constructEndCheckoutQue(PriorityQueue<Customer> endShoppingQue){
		initializeQueList();
		while(!endShoppingQue.isEmpty()) {
			Customer cst = endShoppingQue.poll().deepCopyUsingSerialization();
			constructCheckoutQue(cst);
		}
		
		for (CheckoutLane lane : laneList) {
			while(!lane.getWaitingQueue().isEmpty()) 
				checkoutQue.add(lane.getWaitingQueue().remove());
		}
		
		return checkoutQue;
	}
	
	//return recreated endShoppingQue
	public static PriorityQueue<Customer> refreshEndShoppingQue(){
		return endShoppingQueTemp;
	}
	
	
	/* Analysis Info
	 * 	avgWTime: average waiting time
	 * 	maxWTime: maximum waiting time
	 * 	getSimulationAnlInfo -- function to calculate avgWTime, maxWTime.
	 */
	public static double avgWTime = 0.0;
	public static double maxWTime = 0.0;
	public static void getSimulationAnlInfo(){
		int size = checkoutQue.size();
		while(!checkoutQue.isEmpty()) {
			Customer cst = checkoutQue.poll();
			maxWTime = cst.getWaitingTime() > maxWTime ? cst.getWaitingTime() : maxWTime;
			avgWTime += cst.getWaitingTime();
		}
		avgWTime /= size;
	}
	
	/********printSimulationResult**********
	 * function: print simulation result into output.txt file
	 */
	public static void printSimulationResult(PriorityQueue<Customer> totalQue) throws FileNotFoundException, IOException{
		FileOutputStream file = new FileOutputStream("output.txt");
		getSimulationAnlInfo();
		try (OutputStreamWriter osw = new OutputStreamWriter(file, StandardCharsets.UTF_8)){
			osw.write(String.format("=========== Grocery Store Simulation ============\n"
					+ "Total Lanes Count: %d\n"
					+ "Express Lanes Count: %d\n"
					+ "=====Simulation Analysis====\n"
					+ "Average Waiting Time: %.2f\n"
					+ "Max Waiting Time: %.2f\n"
					+ "=========== Simulation Start ==========\n", totalCnt, expressCnt, avgWTime, maxWTime));
			while(!totalQue.isEmpty()) {
				Customer cst = totalQue.poll();
				String line;
				line = String.format("%.2f: ", cst.getTimeStamp()); 
				if (cst.getStatus() == CustomerStatus.Arrival) 
					line += String.format("Arrival Customer %d\n", cst.getNum());
				else if(cst.getStatus() == CustomerStatus.EndShopping) {
					line += String.format("Finished Shopping Customer %d\n", cst.getNum());
					if (cst.getItemCount() > 12)
						line += String.format("More than 12, chose Lane %d (%d)\n", cst.getLaneNum(), cst.getCstNumLaneBefore());
					else
						line += String.format("12 or fewer, chose Lane %d (%d)\n", cst.getLaneNum(), cst.getCstNumLaneBefore());
				}
				else
					line += String.format("Finished Checkout Customer %d on Lane %d (%d) (%.2f minute wait, "
							+ "%d people in line -- finished shopping at %.2f, got to front of the line at %.2f)\n"
							, cst.getNum(), cst.getLaneNum(), cst.getCstNumLaneAfter(), cst.getWaitingTime()
							, cst.getCstNumLaneBefore(), cst.getFinishShoppingTimeStamp(), cst.getFinishShoppingTimeStamp() + cst.getWaitingTime());
					
				osw.append(line);
			}
			osw.append("===========Simulation Start============");
			osw.flush();
			osw.close();
		}
		System.out.println("simulation end!");
	}

}
