package automail;

/** This class calculates the charge, cost, activity units, activity cost
 *  and records the service fee and statistics information 
 *  by singleton design model  
 */
public class ChargeCalculator{
    /** Represents the activity unit of look up */
    private final double LOOK_UP_ACTIVITY_UNIT = 0.1d;
    /** Represents the activity unit of move up/down one floor */
    private final int MOVEMENT_ACTIVITY_UNIT = 5;
    /** Represents factor to calculate the charge */
    private final int WHOLE_TRIP_FACTOR =2;
    private IServiceFeeAdapter serviceFeeAdapter = null;
    private double unitPrice;
    private double markUpPercentage;
    private int groundFloor;
    private double threshold;
    // used to record statistics information
    private int totalDelivered;
    private double billableActivity;
    private double totalActivityCost;
    private double totalServiceCost;
    private int totalSuccess;
    private int totalFailure;
    
    // single instance of ChargeCalculator
    private static ChargeCalculator instance = null;

    
    /**
     * Singleton get instance function
     * @return  instance of this class
     */
    public static ChargeCalculator getInstance(){
        if (instance == null){
            instance = new ChargeCalculator();
        }
        return instance;
    }

    
    // Constructor
    private ChargeCalculator(){
        // initialize statistics information
        totalDelivered = 0;
        billableActivity = 0d;
        totalActivityCost = 0d;
        totalServiceCost = 0d;
        totalSuccess = 0;
        totalFailure = 0;
        serviceFeeAdapter = new ServiceFeeAdapter();
    }

    
    /**
     * config the calculator with properties
     * @param  unitPrice  unit price of each activity unit
     * @param  markUpPercentage  percent of mark up
     * @param  wModem  WifiModem instance
     * @param  groundfloor position of mail room
     * @param  threshold threshold to judge the mail is priority or not
     */
    public void config(double unitPrice, double markUpPercentage,int groundFloor, double threshold){
        this.unitPrice = unitPrice;
        this.markUpPercentage = markUpPercentage;
        this.groundFloor = groundFloor;
        this.threshold = threshold;
    }

    
    // calculate the activity units
    private double getActivityUnit(int destination_floor, int lookupCount){
        return (destination_floor - groundFloor) * WHOLE_TRIP_FACTOR * MOVEMENT_ACTIVITY_UNIT
        + lookupCount * LOOK_UP_ACTIVITY_UNIT;
    }


    // calculate the charge paid by the tenant
    private double getTenantCharge(int destination_floor, double serviceFee){
        return (serviceFee + getActivityUnit(destination_floor, 1) * unitPrice) * (1 + markUpPercentage);
    }
    

	/**
     * Return the charge information upon successful delivery
     * @param destination_floor  destination of the mail
     * @return a string contains the charge cost fee and activity unit in two decimal places
     */
    public String getChargeString(int destination_floor){  
        ServiceData serviceData = serviceFeeAdapter.getServiceFee(destination_floor);
        int lookupCount = serviceData.lookupCount;
        double serviceFee = serviceData.serviceFee;
        double activityUnit = getActivityUnit(destination_floor, lookupCount);
        double activityCost = activityUnit * unitPrice;
        double tenantCharge = getTenantCharge(destination_floor, serviceFee);
        // record statistical information
        totalDelivered++;
        billableActivity += activityUnit;
        totalActivityCost += activityCost;
        totalServiceCost += serviceFee;
        totalSuccess++;
        totalFailure += lookupCount-1;
        return String.format(" | Charge: %.2f | Cost: %.2f | Fee: %.2f | Activity: %.2f", tenantCharge, activityCost + serviceFee, serviceFee, activityUnit);
    }

    
    // get the expected cost while only add lookup cost to statistical data
    private double getExpectedCost(int destination_floor){
        ServiceData serviceData = serviceFeeAdapter.getServiceFee(destination_floor);
        int lookupCount = serviceData.lookupCount;
        double serviceFee = serviceData.serviceFee;
        double tenantCharge = getTenantCharge(destination_floor, serviceFee);
        // record statistical information
        billableActivity += lookupCount * LOOK_UP_ACTIVITY_UNIT;
        totalSuccess++;
        totalFailure += lookupCount-1;
        return tenantCharge;
    }


    //print the statistics information
    public void chargeStatistics(){
        System.out.printf("The total number of items delivered: %d\n", totalDelivered);
        System.out.printf("The total billable activity: %.2f\n",billableActivity);
        System.out.printf("The total activity cost: %.2f\n",totalActivityCost);
        System.out.printf("The total service cost: %.2f\n",totalServiceCost);
        System.out.printf("The total number of lookups: %d\n",totalSuccess + totalFailure);
        System.out.printf("The total number of successful lookups: %d\n",totalSuccess);
        System.out.printf("The total number of failed lookups: %d\n",totalFailure);
    }

    
    // Send finish signal to adapter
    public void finish(){
        serviceFeeAdapter.finish();
    }


    /**
     * Return the priority of the Mail item
     * @param destination_floor destination of mail
     * @return priority of mail 
    */
    public int priority(int destination_floor){
        if(getExpectedCost(destination_floor) > threshold){
            return 1;
        }
        return 0;
    }
}