package Models;

/**
 * Represents a product (Vegetable or Fruit) in the inventory.
 * Contains business logic for dynamic pricing based on stock thresholds.
 *
 * @author Zafer Mert Serinken
 */
public class Product {
    private int id;
    private String name;
    private String type; // "VEGETABLE" or "FRUIT"
    private double pricePerKg;
    private double stockKg;
    private double thresholdKg;
    private String imagePath;
    private boolean isActive;

    public Product() {}

    public Product(int id, String name, String type, double pricePerKg, double stockKg, double thresholdKg, String imagePath) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.pricePerKg = pricePerKg;
        this.stockKg = stockKg;
        this.thresholdKg = thresholdKg;
        this.imagePath = imagePath;
        this.isActive = true;
    }

    /**
     * Calculates the current price of the product based on the stock level.
     * <p>
     * Implements the "Greedy Owner" rule: If the current stock is less than or equal to
     * the threshold, the price is doubled.
     * </p>
     *
     * @return The effective selling price per kg.
     */
    public double getEffectivePrice() {
        if (stockKg <= thresholdKg && stockKg > 0) {
            return pricePerKg * 2;
        }
        return pricePerKg;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(double pricePerKg) { this.pricePerKg = pricePerKg; }

    public double getStockKg() { return stockKg; }
    public void setStockKg(double stockKg) { this.stockKg = stockKg; }

    public double getThresholdKg() { return thresholdKg; }
    public void setThresholdKg(double thresholdKg) { this.thresholdKg = thresholdKg; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}