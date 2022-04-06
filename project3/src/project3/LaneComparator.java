package project3;

import java.util.Comparator;

/*********LaneComparator**********
 *LaneComparator is used to sort the lanes in the lane list by the lane size 
 */
public class LaneComparator implements Comparator<CheckoutLane> {
	@Override
	public int compare(CheckoutLane lane1, CheckoutLane lane2) {
		return lane1.getWaitingQueue().size() - lane2.getWaitingQueue().size();
	}

}
