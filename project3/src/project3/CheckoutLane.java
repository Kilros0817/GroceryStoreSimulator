package project3;
import java.util.Queue;
import java.util.LinkedList;

/**************CheckoutLane************
 * CheckoutLane is for storing lane state.
 * 	waitingQueue: the queue of customers on the line
 * 	laneNum: the lane number (0 - 11)
 * 	lType: the type of lane (Regular, Express)
 */

public class CheckoutLane {
	private Queue<Customer> waitingQueue = new LinkedList<>();
	private int laneNum;
	private LaneType lType;
	public CheckoutLane() {
		
	}
	public CheckoutLane(int p_laneNum, LaneType p_type) {
		this.setLaneNum(p_laneNum);
		this.setlType(p_type);
	}
	/**
	 * @return the waitingQueue
	 */
	public Queue<Customer> getWaitingQueue() {
		return waitingQueue;
	}
	/**
	 * @param waitingQueue the waitingQueue to set
	 */
	public void setWaitingQueue(Queue<Customer> waitingQueue) {
		this.waitingQueue = waitingQueue;
	}
	/**
	 * @return the laneNum
	 */
	public int getLaneNum() {
		return laneNum;
	}
	/**
	 * @param laneNum the laneNum to set
	 */
	public void setLaneNum(int laneNum) {
		this.laneNum = laneNum;
	}
	/**
	 * @return the lType
	 */
	public LaneType getlType() {
		return lType;
	}
	/**
	 * @param lType the lType to set
	 */
	public void setlType(LaneType lType) {
		this.lType = lType;
	}
}

