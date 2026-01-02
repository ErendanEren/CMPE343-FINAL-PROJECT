package Models;

import java.io.ByteArrayInputStream;
import javafx.scene.image.Image;

/**
 * Represents a product (Vegetable or Fruit) in the inventory.
 * Contains business logic for dynamic pricing based on stock thresholds.
 * Handles both file paths (for testing) and BLOB data (from DB).
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
    private byte[] imageContent;
    private String mimeType;


    private boolean isActive;

    public Product() {}

    /**
     * Constructor including BLOB image data (For Database operations)
     */
    public Product(int id, String name, String type, double pricePerKg, double stockKg, double thresholdKg, byte[] imageContent, String mimeType) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.pricePerKg = pricePerKg;
        this.stockKg = stockKg;
        this.thresholdKg = thresholdKg;
        this.imageContent = imageContent;
        this.mimeType = mimeType;
        this.isActive = true;
    }

    /**
     * Overloaded Constructor for Dummy Data (Uses image path)
     */
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
     * Implements the "Greedy Owner" rule[cite: 41].
     */
    public double getEffectivePrice() {
        if (stockKg <= thresholdKg && stockKg > 0) {
            return pricePerKg * 2;
        }
        return pricePerKg;
    }


    /**
     * Converts the stored BLOB (byte[]) to a JavaFX Image object.
     * Useful for ProductCardController.
     */
    public Image getJavaFXImage() {
        if (imageContent != null && imageContent.length > 0) {
            return new Image(new ByteArrayInputStream(imageContent));
        } else if (imagePath != null && !imagePath.isEmpty()) {
            try {
                return new Image(getClass().getResourceAsStream("/Images/" + imagePath));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    // --- Getters and Setters ---

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

    public byte[] getImageContent() { return imageContent; }
    public void setImageContent(byte[] imageContent) { this.imageContent = imageContent; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }



    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}