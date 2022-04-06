package project3;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/*********** class Customer **********
 * Customer is for storing customer state at every stage.
 * 	num: number of customer.
 * 	status: customer status at current time stamp (Arrival, EndShopping, EndCheckout)
 * 	timeStamp: the current time stamp of the customer 
 * 	itemCount: the number of items customer pick up
 * 	itemTime: the average picking up time.
 * 	waitingTime: waiting time in checkout lane.
 * 	finishShoppingtimeStamp: time stamp when customer finish shopping
 * 	laneNum: the lane number where customer got checked out.
 * 	cstNumLaneBefore: the number of customers in the lane just after the customer finish shopping
 * 	cstNumLaneAfter: the number of customers in the lane when the customer leaves the lane.
 */
@SuppressWarnings("serial")
public class Customer  implements Comparable<Customer>, Serializable {
	private int num;
	private CustomerStatus status;
	private double timeStamp;
	private int itemCount;
	private double itemTime;
	private double waitingTime;
	private double finishShoppingTimeStamp;
	private int laneNum;
	private int cstNumLaneBefore;
	private int cstNumLaneAfter;
	
	public Customer() {
		
	}
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public CustomerStatus getStatus() {
		return status;
	}
	public void setStatus(CustomerStatus status) {
		this.status = status;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	public double getItemTime() {
		return itemTime;
	}
	public void setItemTime(double itemTime) {
		this.itemTime = itemTime;
	}
	public double getWaitingTime() {
		return waitingTime;
	}
	public void setWaitingTime(double waitingTime) {
		this.waitingTime = waitingTime;
	}
	public int getLaneNum() {
		return laneNum;
	}
	public void setLaneNum(int laneNum) {
		this.laneNum = laneNum;
	}
	public double getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(double timeStamp) {
		this.timeStamp = timeStamp;
	}
	public double getFinishShoppingTimeStamp() {
		return finishShoppingTimeStamp;
	}

	public void setFinishShoppingTimeStamp(double finishShoppingTimeStamp) {
		this.finishShoppingTimeStamp = finishShoppingTimeStamp;
	}
	public int getCstNumLaneBefore() {
		return cstNumLaneBefore;
	}

	public void setCstNumLaneBefore(int cstNumLaneBefore) {
		this.cstNumLaneBefore = cstNumLaneBefore;
	}

	public int getCstNumLaneAfter() {
		return cstNumLaneAfter;
	}

	public void setCstNumLaneAfter(int cstNumLaneAfter) {
		this.cstNumLaneAfter = cstNumLaneAfter;
	}
	
	public Customer deepCopyUsingSerialization()
	{
		try
		{
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(bo);
			o.writeObject(this);
			
			ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
			ObjectInputStream i = new ObjectInputStream(bi);
			
			return (Customer)i.readObject();
		}
		catch(Exception e)
		{
			return null;
		}	
	}
	
	// Compare two customer objects by their timeStamp
    @Override
    public int compareTo(Customer customer) {
        if(this.getTimeStamp() > customer.getTimeStamp()) {
            return 1;
        } else if (this.getTimeStamp() < customer.getTimeStamp()) {
            return -1;
        } else {
            return 0;
        }
    }
}


