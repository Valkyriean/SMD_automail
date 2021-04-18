package automail;

/**
 * a ChargeCalculatot to calculate the charge, priority, 
 * record the statistics information and print out it
 */
public interface IServiceFeeAdapter{

    /**
     * get the service fee and the times of look up service fee
     * @param destination_floor  destination of the mail
     * @return an object contains service fee and times of look up service fee
     */
    public ServiceData getServiceFee(int destination_floor);
    public void finish();
}

class ServiceData{
    double serviceFee;
    int lookupCount;
    ServiceData(double serviceFee, int lookupCount){
        this.serviceFee = serviceFee;
        this.lookupCount = lookupCount;
    }
}