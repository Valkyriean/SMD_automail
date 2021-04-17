package automail;

import com.unimelb.swen30006.wifimodem.WifiModem;

/** */
public class ChargeCalculator{
    /** Represents the activity unit of look up */
    private final double LOOK_UP_ACTIVITY_UNIT = 0.1d;
    /** Represents the activity unit of moven up/down one floor */
    private final int MOVEMENT_ACTIVITY_UNIT = 5;
    /** Represents factor to calculate the charge */
    private final int WHOLE_TRIP_FACTOR =2;
    
    private double unitPrice;
    private double markUpPercentage;
    private WifiModem wModem;
    private int groundFloor;

    private int totalDelivered;
    private double billableActivity;
    private double totalActivityCost;
    private double totalServiceCost;
    private int totalSuccess;
    private int totalFailure;
    private double threshold;

    private static ChargeCalculator instance = null;

    public static ChargeCalculator getInstance(){
        if (instance == null){
            instance = new ChargeCalculator();
        }
        return instance;
    }

    private ChargeCalculator(){
        totalDelivered = 0;
        billableActivity = 0d;
        totalActivityCost = 0d;
        totalServiceCost = 0d;
        totalSuccess = 0;
        totalFailure = 0;
    }

    public void initialize(double unitPrice, double markUpPercentage,WifiModem wModem,int groundFloor, double threshold){
        this.unitPrice = unitPrice;
        this.markUpPercentage = markUpPercentage;
        this.wModem = wModem;
        this.groundFloor = groundFloor;
        this.threshold = threshold;
    }


    private double getServiceFee(int destination_floor){
        return wModem.forwardCallToAPI_LookupPrice(destination_floor);     
    }


    private double getActivityUnit(int destination_floor, int lookupCount){
        return (destination_floor - groundFloor) * WHOLE_TRIP_FACTOR * MOVEMENT_ACTIVITY_UNIT
        + lookupCount * LOOK_UP_ACTIVITY_UNIT;
    }


    private double getTenantCharge(int destination_floor, double serviceFee){
        return (serviceFee + getActivityUnit(destination_floor, 1) * unitPrice) * (1 + markUpPercentage);
    }


    
    public String getChargeString(int destination_floor){  
        int lookupCount = 0;
        double serviceFee;
        do{
            serviceFee = getServiceFee(destination_floor);
            lookupCount++;
        } while (serviceFee < 0);
        double activityUnit = getActivityUnit(destination_floor, lookupCount);
        double activityCost = activityUnit * unitPrice;
        double tenantCharge = getTenantCharge(destination_floor, serviceFee);
        totalDelivered++;
        billableActivity += activityUnit;
        totalActivityCost += activityCost;
        totalServiceCost += serviceFee;
        totalSuccess++;
        totalFailure += lookupCount-1;
        return String.format(" | Charge: %.2f | Cost: %.2f | Fee: %.2f | Activity: %.2f", tenantCharge, activityCost + serviceFee, serviceFee, activityUnit);
    }

    public double getExpectedCost(int destination_floor){
        int lookupCount = 0;
        double serviceFee;
        do{
            serviceFee = getServiceFee(destination_floor);
            lookupCount++;
        } while (serviceFee < 0);
        double tenantCharge = getTenantCharge(destination_floor, serviceFee);
        billableActivity += lookupCount * LOOK_UP_ACTIVITY_UNIT;
        totalSuccess++;
        totalFailure += lookupCount-1;
        return tenantCharge;
    }


    public void chargeStatistics(){
        System.out.printf("The total number of items delivered: %d\n", totalDelivered);
        System.out.printf("The total billable activity: %.2f\n",billableActivity);
        System.out.printf("The total activity cost: %.2f\n",totalActivityCost);
        System.out.printf("The total service cost: %.2f\n",totalServiceCost);
        System.out.printf("The total number of successful lookups: %d\n",totalSuccess);
        System.out.printf("The total number of failed lookups: %d\n",totalFailure);
    }

    public int priority(int destination_floor){
        if(getExpectedCost(destination_floor) > threshold){
            return 1;
        }
        return 0;
    }
}