/**
 * 
 */
package com.resto.image;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * @author kkanaparthi
 *
 */
public class CheckDates {

	public static String CLASS_NAME="CheckDates";
	public static final int INVALID_MIRACLE_DATE = Integer.MAX_VALUE;
	public static final int INVALID_OFFSET_DATE = Integer.MAX_VALUE;
	public static final int INVALID_AVAILABILITY_STATUS = Integer.MAX_VALUE;
	public static final int INVALID_BACKORDER_DAYS = Integer.MAX_VALUE;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Get Out OF Stock Date "+getOutOfStockDate());
		
		long daysBetween = getDaysBetweenDates(Calendar.getInstance(), getOutOfStockDate());
		
		System.out.println("Days Between Is  "+ daysBetween);
		
		System.out.println("Checking OOS Equality "+ getOutOfStockDate().compareTo(getOutOfStockDate()) );

		
	     // create two calendar at the different dates
	      Calendar cal1 = new GregorianCalendar(2015, 8, 15);
	      Calendar cal2 = new GregorianCalendar(2008, 1, 02);

	      Calendar cal3 = new GregorianCalendar(0, 0, 0);

	      
	      Calendar calendar = Calendar.getInstance();
			//Not to Include Today in MiracleDate Calculation
	      calendar.set(1980, 1, 1);
			
	      
		Calendar calendarZero = Calendar.getInstance();
				//Not to Include Today in MiracleDate Calculation
			  calendarZero.set(0, 0, 0);
				
	      // compare the time values represented by two calendar objects.
	      int i = cal1.compareTo(cal2);

	      // return positive value if equals else return negative value
	      System.out.println("The result is :"+i);

	      // compare again but with the two calendars swapped
	      int j = cal2.compareTo(cal1);

	      // return positive value if equals else return negative value
	      System.out.println("The result is :" + j);
	      int k = calendar.compareTo(calendarZero);
	      // return positive value if equals else return negative value
	      System.out.println("The result is KKK :" + k);
	      
	      SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
	     formatter.format(calendar.getTime());
	      
	      System.out.println("The result is Regular Calendar  :" + formatter.format(calendar.getTime()));

	      
			Calendar gregCalendar = new GregorianCalendar(1970,1,1);
			//Not to Include Today in MiracleDate Calculation
		      System.out.println("The result is gregCalendar :" + formatter.format(gregCalendar.getTime()));

	}

	
	
	public static Calendar getOutOfStockDate() {
		Calendar calendar = Calendar.getInstance();
		//Not to Include Today in MiracleDate Calculation
		calendar.set(0, 0, 0);
		return calendar;
	}
	
	
	  /**
     * This method finds the days between the Given Dates/TimeStamps
     * @return
     */
    public static long getDaysBetweenDates(Calendar cal1,Calendar cal2) {
    
        String methodName = "getDaysBetweenDates";
        long noOfDays= INVALID_BACKORDER_DAYS;
            System.out.println("Started "+CLASS_NAME+"."+methodName);
            System.out.println("Started the Service with TimeStamp Difference: startDate: "+cal1+" endDate:  "+cal2);
            
            Calendar calLocal = Calendar.getInstance();
//            calLocal.add(Calendar.DAY_OF_MONTH,0);
//            calLocal.set(Calendar.YEAR, 2014);
            Date tmpDte = new Date(calLocal.getTimeInMillis());
            boolean isBefore =  tmpDte.before(Calendar.getInstance().getTime());
            System.out.println(" tmpDte "+calLocal+" currDate "+Calendar.getInstance().getTime());

            System.out.println(" isBefore IS  "+isBefore+" EXITING ");
            System.exit(1);
            
        try {
            if(isValidObject(cal1)&& isValidObject(cal2)) {
                SimpleDateFormat onlyDateSdf = new SimpleDateFormat("MM-dd-yyyy");
          
                String startDateStr = onlyDateSdf.format(cal1.getTime());
                String endDateStr = onlyDateSdf.format(cal2.getTime());
                
                cal1.setTime(onlyDateSdf.parse(startDateStr));
                cal2.setTime(onlyDateSdf.parse(endDateStr));
 
              if(cal1.before(cal2)) {
                noOfDays = 0;
                    while(cal1.before(cal2) ) {
                        cal1.add(Calendar.DAY_OF_MONTH,1);
                        noOfDays++;
                    }

                        System.out.println("The Total NUmber fo Days came to  "+noOfDays);
 
                } else if(cal1.equals(cal2)) {
                	noOfDays = INVALID_OFFSET_DATE;
                } else {
                    noOfDays = -1;
                        System.out.println("The backOrder date is Past Date so returning noofDays as  "+noOfDays);
           
                }
            }
        } catch(Exception ex) {
            System.err.println("The Application Exception while Comparing TimeStamps is "+ ex.getMessage());
//            throw new ApplicationException("The Application Exception while Comparing TimeStamps is "+ ex.getMessage());
            }
   
            System.out.println("Exiting "+CLASS_NAME+"."+methodName);
            System.out.println("The Difference in Days is  the Service with TimeStamp Difference"+noOfDays);

            return noOfDays;
    }
	
    
	/**
	 * This method checks if the given String  is Valid/Not
	 * @param inArray
	 * @return
	 */
	public static boolean isValidObject(Object inObject) {
		if(inObject!= null) {
			return true;
		} else {
			return false;
		}
	}
}
