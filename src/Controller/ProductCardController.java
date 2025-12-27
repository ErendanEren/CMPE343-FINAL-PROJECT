package Controller;

import Models.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.InputStream;

/**
 * Controller for a single product card UI component.
 * Handles displaying product details and adding items to the cart.
 *
 * @author Zafer Mert Serinken
 */
public class ProductCardController {

    @FXML private ImageView productImage;
    @FXML private Label productName;
    @FXML private Label productPrice;
    @FXML private Label stockLabel;
    @FXML private Spinner<Double> amountSpinner; // For selecting kg (0.5, 1.0, etc.)

    private Product product;

    /**
     * Initializes the controller. Sets up the spinner for kilogram selection.
     */
    @FXML
    public void initialize() {
        // Configure Spinner for 0.5 kg increments, starting at 1.0 kg
        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 100.0, 1.0, 0.5);
        amountSpinner.setValueFactory(valueFactory);
    }

    /**
     * Injects product data into the UI card.
     * This method is called from CustomerController.
     *
     * @param product The product object to display.
     */
    public void setProductData(Product product) {
        this.product = product;

        productName.setText(product.getName());

        // Use the business logic for price (Checks threshold/Greedy rule)
        double price = product.getEffectivePrice();
        productPrice.setText(String.format("%.2f TL/kg", price));

        // Show stock warning if needed
        stockLabel.setText("Stock: " + product.getStockKg() + " kg");
        if (product.getStockKg() <= product.getThresholdKg()) {
            stockLabel.setTextFill(Color.RED); // Visual cue for low stock
            productPrice.setTextFill(Color.RED); // Visual cue for high price
        } else {
            stockLabel.setTextFill(Color.BLACK);
            productPrice.setTextFill(Color.BLACK);
        }

        // Load Image (Handles local resources or potential DB streams)
        try {
            // For now, loading from resources folder (e.g., /Images/tomato.jpg)
            // In the future, this will handle BLOBs from DB.
            String path = "/Images/" + product.getImagePath();
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                productImage.setImage(new Image(is));
            }
        } catch (Exception e) {
            System.err.println("Could not load image for " + product.getName());
        }
    }

    /**
     * Event handler for "Add to Cart" button.
     */
    @FXML
    private void handleAddToCart() {
        double amount = amountSpinner.getValue();

        if (amount > product.getStockKg()) {
            // TODO: Show alert dialog to user (JavaFX Alert)
            System.out.println("Error: Not enough stock!");
            return;
        }

        System.out.println("Added to cart: " + product.getName() + " - " + amount + " kg");

        // Call the Singleton ShoppingCart to add the item
        Models.ShoppingCart.getInstance().addItem(product, amount);
    }
}