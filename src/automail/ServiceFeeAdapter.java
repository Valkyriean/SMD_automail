package automail;

import com.unimelb.swen30006.wifimodem.WifiModem;

import simulation.Building;


public class ServiceFeeAdapter implements IServiceFeeAdapter{
    private WifiModem wModem;

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

    public void finish(){
        System.out.println(wModem.Turnoff());
    }

}
