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
 * Part of the Member 1 Customer Module focusing on the core shopping experience.
 * Manages the display of individual product details and handles quantity selection.
 *
 * @author Zafer Mert Serinken
 */
public class ProductCardController {

    @FXML private ImageView productImage;
    @FXML private Label productName;
    @FXML private Label productPrice;
    @FXML private Label stockLabel;
    @FXML private Spinner<Double> amountSpinner;

    private Product product;

    /**
     * Initializes the controller class.
     * Configures the weight spinner to allow selection in 0.5 kg increments.
     */
    @FXML
    public void initialize() {
        // Set up spinner for 0.5 kg steps, default to 1.0 kg
        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 100.0, 1.0, 0.5);
        amountSpinner.setValueFactory(valueFactory);
    }

    /**
     * Populates the UI card with specific product data.
     * Applies dynamic pricing logic and provides visual warnings for low stock levels.
     *
     * @param product The product model to be displayed on this card.
     */
    public void setProductData(Product product) {
        this.product = product;

        productName.setText(product.getName());

        // Apply pricing business logic (checks stock threshold/Greedy rule)
        double price = product.getEffectivePrice();
        productPrice.setText(String.format("%.2f TL/kg", price));

        // Display current stock and apply visual cues if stock is below threshold
        stockLabel.setText("Stock: " + product.getStockKg() + " kg");
        if (product.getStockKg() <= product.getThresholdKg()) {
            stockLabel.setTextFill(Color.RED);
            productPrice.setTextFill(Color.RED);
        } else {
            stockLabel.setTextFill(Color.BLACK);
            productPrice.setTextFill(Color.BLACK);
        }

        // Load the product image if it exists
        if (product.getJavaFXImage() != null) {
            productImage.setImage(product.getJavaFXImage());
        }
    }

    /**
     * Handles the 'Add to Cart' button action.
     * Validates requested quantity against available stock and updates the ShoppingCart.
     */
    @FXML
    private void handleAddToCart() {
        double amount = amountSpinner.getValue();

        // Perform stock validation check
        if (amount > product.getStockKg()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Insufficient Stock");
            alert.setHeaderText("Not Enough Stock");
            alert.setContentText("You requested " + amount + " kg, but only " + product.getStockKg() + " kg is available.");
            alert.showAndWait();
            return;
        }

        // Add the validated item to the singleton ShoppingCart instance
        Models.ShoppingCart.getInstance().addItem(product, amount);
    }
}