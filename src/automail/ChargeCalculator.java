package automail;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ChargeCalculator{
    private final BigDecimal unitPrice;
    private final BigDecimal markUpPercentage;
    public ChargeCalculator(double unitPrice, double markUpPercentage){
        this.unitPrice = new BigDecimal(unitPrice);
        this.markUpPercentage = new BigDecimal(markUpPercentage);
    }

    public BigDecimal calculate(BigDecimal activityUnits, BigDecimal serviceFee){
        BigDecimal activityCost = unitPrice.multiply(activityUnits);
        BigDecimal cost = activityCost.add(serviceFee);
        BigDecimal result = cost.multiply(markUpPercentage.add(new BigDecimal(1)));
        return result.setScale(2,RoundingMode.HALF_UP);
    }

    public static void main(String[] args) {
        BigDecimal result = null;
        ChargeCalculator c = new ChargeCalculator(0.224, 0.059);
        result = c.calculate(new BigDecimal(20.1), new BigDecimal(12.3));
        System.out.println(result.setScale(2,RoundingMode.HALF_UP));
    }
}