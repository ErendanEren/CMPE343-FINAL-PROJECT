package Models;

/**
 * Represents a single line item in the shopping cart.
 * Contains the product and the quantity selected by the customer.
 *
 * @author Zafer Mert Serinken
 */
public class CartItem {
    private Product product;
    private double quantity; // in kg

    public CartItem(Product product, double quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Calculates the total price for this item line.
     * Logic: Effective Price (considering threshold) * Quantity
     *
     * @return Total price for this amount of product.
     */
    public double getItemTotal() {
        return product.getEffectivePrice() * quantity;
    }

    // --- Getters and Setters ---

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    // Increases the quantity (used when merging items)
    public void addQuantity(double additionalAmount) {
        this.quantity += additionalAmount;
    }

    @Override
    public String toString() {
        return String.format("%s - %.2f kg - Total: %.2f TL",
                product.getName(), quantity, getItemTotal());
    }
}