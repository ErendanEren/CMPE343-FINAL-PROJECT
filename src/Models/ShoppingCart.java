package Models;

import Dao.CouponDAO;
import Dao.DBCouponDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Manages the shopping cart operations using Singleton Pattern.
 * Handles adding items, merging duplicates, and calculating totals.
 *
 * @author Zafer Mert Serinken
 */
public class ShoppingCart {

    // The single instance of the cart (Singleton)
    private static ShoppingCart instance;

    private CouponDAO couponDAO;
    private Coupon appliedCoupon;

    // ObservableList allows the UI to update automatically when items are added/removed

    // ObservableList allows the UI to update automatically when items are added/removed
    private ObservableList<CartItem> items;

    // Private constructor so no one else can create a new cart manually
    private ShoppingCart() {
        this.items = FXCollections.observableArrayList();
        this.couponDAO = new DBCouponDAO();
    }

    /**
     * Returns the single instance of the ShoppingCart.
     * If it doesn't exist, creates it.
     *
     * @return The ShoppingCart instance.
     */
    public static ShoppingCart getInstance() {
        if (instance == null) {
            instance = new ShoppingCart();
        }
        return instance;
    }

    /**
     * Adds a product to the cart.
     * If the product already exists in the cart, it merges the quantity.
     *
     * @param product The product to add.
     * @param amount The amount in kg.
     */
    public void addItem(Product product, double amount) {
        // 1. Check if product already exists in cart (Merge Logic)
        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                // Product found! Update quantity instead of creating new line.
                item.addQuantity(amount);
                System.out.println("Merged item: " + product.getName() + " new total: " + item.getQuantity());
                return; // Exit method
            }
        }

        // 2. If not found, create new item
        CartItem newItem = new CartItem(product, amount);
        items.add(newItem);
        System.out.println("Added new item: " + product.getName());
    }

    /**
     * Removes an item from the cart.
     * @param item The CartItem to remove.
     */
    public void removeItem(CartItem item) {
        items.remove(item);
    }

    /**
     * Calculates the total cost of the cart.
     * @return Total price.
     */
    /**
     * Calculates the total cost of the cart.
     * @return Total price.
     */
    public double calculateTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getItemTotal();
        }

        if (appliedCoupon != null) {
            // Check minimum total requirement
            if (total >= appliedCoupon.getMinTotal()) {
                double discountAmount = total * (appliedCoupon.getDiscountPercent() / 100.0);
                total -= discountAmount;
            }
        }

        return total;
    }

    public boolean applyCoupon(String code) {
        Coupon coupon = couponDAO.getCouponByCode(code);
        if (coupon != null && coupon.isActive()) {
            // Check date validity
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            boolean isDateValid = true;

            if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
                isDateValid = false;
            }
            if (coupon.getValidTo() != null && now.isAfter(coupon.getValidTo())) {
                isDateValid = false;
            }

            if (isDateValid) {
                this.appliedCoupon = coupon;
                return true;
            }
        }
        this.appliedCoupon = null;
        return false;
    }

    public Coupon getAppliedCoupon() {
        return appliedCoupon;
    }

    public void removeCoupon() {
        this.appliedCoupon = null;
    }

    public ObservableList<CartItem> getItems() {
        return items;
    }

    public void clearCart() {
        items.clear();
    }
}