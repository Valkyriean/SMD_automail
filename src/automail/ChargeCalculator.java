package automail;


import com.unimelb.swen30006.wifimodem.WifiModem;

public class ChargeCalculator{
    private final double unitPrice;
    private final double markUpPercentage;
    private final WifiModem wModem;
    private int totalDelivered;
    private double billableActivity;
    private double totalActivityCost;
    private double totalServiceCost;
    private int totalSuccess;
    private int totalFailure;


    public ChargeCalculator(double unitPrice, double markUpPercentage,WifiModem wModem) {
        this.unitPrice = unitPrice;
        this.markUpPercentage = markUpPercentage/100;
        this.wModem = wModem;
        totalDelivered = 0;
        billableActivity = 0d;
        totalActivityCost = 0d;
        totalServiceCost = 0d;
        totalSuccess = 0;
        totalFailure = 0;
        
        
    }
    public double getServiceFee(int destination_floor){
        return wModem.forwardCallToAPI_LookupPrice(destination_floor);     
    }

    public double getActivityUnit(int destination_floor, int lookupCount){
        return (destination_floor - 1) * 2 * 5 + lookupCount * 0.1;
    }
    
    public double getActivityCost(double activityUnit){
        return unitPrice * activityUnit;
    }

    public double getCost(int destination_floor){
        int lookupCount = 0;
        double serviceFee;
        do{
            serviceFee = getServiceFee(destination_floor);
            lookupCount++;
        } while (serviceFee < 0);

        double activityUnit = getActivityUnit(destination_floor, 1);
        double activityCost = getActivityCost(activityUnit);
        
        return serviceFee + activityCost;
    }

    public double getCharge(int destination_floor){
        return getCost(destination_floor) * (1+markUpPercentage);
    }


    
    public String getChargeString(int destination_floor){
        
        int lookupCount = 0;
        double serviceFee;
        do{
            serviceFee = getServiceFee(destination_floor);
            lookupCount++;
        } while (serviceFee < 0);

        double activityUnit = getActivityUnit(destination_floor, lookupCount);
        double activityCost = getActivityCost(activityUnit);
        double charge = getCharge(destination_floor);
        
        totalDelivered++;
        billableActivity += activityUnit;
        totalActivityCost += activityCost;
        totalServiceCost += serviceFee;
        totalSuccess++;
        totalFailure += lookupCount-1;
        
        return String.format(" | Charge: %.2f | Cost: %.2f | Fee: %.2f | Activity: %.2f", charge, activityCost+serviceFee, serviceFee, activityUnit);
    }



    public void chargeStatistics(){
        
        System.out.printf("The total number of items delivered: %d\n", totalDelivered);
        System.out.printf("The total billable activity: %.2f\n",billableActivity);
        System.out.printf("The total activity cost: %.2f\n",totalActivityCost);
        System.out.printf("The total service cost: %.2f\n",totalServiceCost);
        System.out.printf("The total number of successful lookups: %d\n",totalSuccess);
        System.out.printf("The total number of failed lookups: %d\n",totalFailure);
        
    }
    
}