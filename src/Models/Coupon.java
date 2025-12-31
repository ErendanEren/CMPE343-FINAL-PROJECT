package Models;

import java.time.LocalDateTime;

public class Coupon {
    private int id;
    private String code;
    private double discountPercent;
    private double minTotal;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean isActive;
    private LocalDateTime createdAt;

    public Coupon() {}

    public Coupon(int id, String code, double discountPercent, double minTotal, LocalDateTime validFrom, LocalDateTime validTo, boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.discountPercent = discountPercent;
        this.minTotal = minTotal;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }

    public double getMinTotal() { return minTotal; }
    public void setMinTotal(double minTotal) { this.minTotal = minTotal; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
