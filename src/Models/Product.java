package Models;

public class Product {
    private int id;
    private String name;
    private String type; // VEGETABLE, FRUIT
    private double pricePerKg;
    private double stockKg;
    private double thresholdKg;
    private String imagePath;
    private boolean isActive;

    public Product() {}

    public Product(String name, String type, double pricePerKg, double stockKg, double thresholdKg, String imagePath) {
        this.name = name;
        this.type = type;
        this.pricePerKg = pricePerKg;
        this.stockKg = stockKg;
        this.thresholdKg = thresholdKg;
        this.imagePath = imagePath;
        this.isActive = true;
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

    // Logic helper: Dynamic price based on logic can be here or in Service
    // But basic data holding is here.
}
