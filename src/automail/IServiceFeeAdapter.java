package automail;

/**
 * Interface to set standard for ServiceFeeAdapter to ensure protected variation
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

// Class to encapsulate two return variables of different type
class ServiceData{
    double serviceFee;
    int lookupCount;
    ServiceData(double serviceFee, int lookupCount){
        this.serviceFee = serviceFee;
        this.lookupCount = lookupCount;
    }
}