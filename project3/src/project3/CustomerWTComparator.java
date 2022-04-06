package project3;

import java.util.Comparator;

public class CustomerWTComparator implements Comparator<Customer> {
	@Override
	public int compare(Customer cst1, Customer cst2) {
		
		if (cst1.getWaitingTime() - cst2.getWaitingTime() >= 0)
			return -1;
		else 
			return 1;
		
	}
}
