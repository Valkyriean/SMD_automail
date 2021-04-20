package automail;

import com.unimelb.swen30006.wifimodem.WifiModem;
import simulation.Building;

/**
 * This class lookup the service fee from modem and return
 */
public class ServiceFeeAdapter implements IServiceFeeAdapter{
    private WifiModem wModem;

    // Constructor
    public ServiceFeeAdapter(){
        // Install the modem & turn on the modem
        try {
        	System.out.println("Setting up Wifi Modem");
        	wModem = WifiModem.getInstance(Building.MAILROOM_LOCATION);
			System.out.println(wModem.Turnon());
		} catch (Exception mException) {
			mException.printStackTrace();
		}
    }

    
    /**
     * get the service fee and the times of look up service fee
     * @param destination_floor  destination of the mail
     * @return an object contains service fee and times of look up service fee
     */
    public ServiceData getServiceFee(int destination_floor){
        int lookupCount = 0;
        // look up the serviceFee until succeed
        double serviceFee;
        do{
            serviceFee = wModem.forwardCallToAPI_LookupPrice(destination_floor); 
            lookupCount++;
        } while (serviceFee < 0);
        return new ServiceData(serviceFee, lookupCount);
    }

    // Turn off the modem upon finish
    public void finish(){
        System.out.println(wModem.Turnoff());
    }

}
