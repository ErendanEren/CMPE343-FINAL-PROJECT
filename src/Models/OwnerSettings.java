package Models;

public class OwnerSettings {
    private int id;
    private double minCartValue;
    private int loyaltyMinCompleted;
    private double loyaltyDiscountPercent;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getMinCartValue() { return minCartValue; }
    public void setMinCartValue(double minCartValue) { this.minCartValue = minCartValue; }

    public int getLoyaltyMinCompleted() { return loyaltyMinCompleted; }
    public void setLoyaltyMinCompleted(int loyaltyMinCompleted) { this.loyaltyMinCompleted = loyaltyMinCompleted; }

    public double getLoyaltyDiscountPercent() { return loyaltyDiscountPercent; }
    public void setLoyaltyDiscountPercent(double loyaltyDiscountPercent) { this.loyaltyDiscountPercent = loyaltyDiscountPercent; }
}
