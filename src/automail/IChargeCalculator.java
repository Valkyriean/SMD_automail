package automail;
/**
 * a ChargeCalculatot to calculate the charge, priority, 
 * record the statistics information and print out it
 */
public interface IChargeCalculator{

    /**
     * Calculate the real charge and each part of charge
     * @param destination_floor  destination of the mail
     * @return a string contains the charge cost fee and activity unit in two decimal places
     */
    public String getChargeString(int destination_floor);
    
    /**
     * print the statistics information
     */
    public void chargeStatistics();
    
    /**
     * @param destination_floor destination of mail
     * @return priority of mail 
    */
    public int priority(int destination_floor);

}