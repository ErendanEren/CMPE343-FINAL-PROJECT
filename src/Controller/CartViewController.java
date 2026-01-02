package Controller;

import Models.CartItem;
import Models.ShoppingCart;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Service.AuthService;
import Service.OrderService;
import Models.User;
import Models.Order;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Controls the Shopping Cart interface.
 * Displays selected items, allows removal, and shows total cost.
 *
 * @author Zafer Mert Serinken
 */
public class CartViewController {

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> nameColumn;
    @FXML private TableColumn<CartItem, Double> amountColumn;
    @FXML private TableColumn<CartItem, String> priceColumn;
    @FXML private TableColumn<CartItem, String> totalColumn;
    @FXML private Label totalPriceLabel;

    @FXML private MFXTextField txtCouponCode;
    @FXML private MFXButton btnApplyCoupon;
    @FXML private Label lblCouponMessage;

    @FXML private javafx.scene.control.DatePicker datePickerDelivery;
    @FXML private javafx.scene.control.ComboBox<String> comboDeliveryTime;

    private OrderService orderService = new OrderService();
    private static final double MIN_CART_VALUE = 50.0;

    /**
     * Initializes the cart view by binding table columns to data properties,
     * loading items from the ShoppingCart singleton, and setting up delivery time slots.
     * * @author Eren Çakır Bircan
     */
    @FXML
    public void initialize() {
        // Bind columns to CartItem properties
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getName()));

        amountColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Custom formatting for price columns (adding "TL")
        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f TL", cellData.getValue().getProduct().getEffectivePrice())));

        totalColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f TL", cellData.getValue().getItemTotal())));

        // Load data from Singleton ShoppingCart
        cartTable.setItems(ShoppingCart.getInstance().getItems());

        // Update total price label initially
        updateTotalLabel();

        // Populate Delivery Time Slots
        comboDeliveryTime.getItems().addAll(
                "09:00 - 11:00",
                "11:00 - 13:00",
                "13:00 - 15:00",
                "15:00 - 17:00",
                "17:00 - 19:00"
        );
    }

    /**
     * Removes the selected item from the shopping cart and refreshes the total price.
     * * @author Zafer Mert Serinken
     */
    @FXML
    private void handleRemoveItem() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ShoppingCart.getInstance().removeItem(selected);
            updateTotalLabel();
        }
    }

    /**
     * Validates and applies the coupon code entered in the text field to the current cart session.
     * * @author Eren Çakır Bircan
     */
    @FXML
    private void handleApplyCoupon() {
        String code = txtCouponCode.getText();
        if (code == null || code.trim().isEmpty()) {
            lblCouponMessage.setText("Please enter a code.");
            lblCouponMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        boolean success = ShoppingCart.getInstance().applyCoupon(code.trim());
        if (success) {
            lblCouponMessage.setText("Coupon applied!");
            lblCouponMessage.setStyle("-fx-text-fill: green;");
            updateTotalLabel();
        } else {
            lblCouponMessage.setText("Invalid or expired coupon.");
            lblCouponMessage.setStyle("-fx-text-fill: red;");
            updateTotalLabel(); // In case a previous coupon was removed/invalidated internally (though logic keeps it null if fail)
        }
    }

    /**
     * Updates the total price label displayed in the UI, considering VAT and any applied coupons.
     * * @author Zafer Mert Serinken
     */
    private void updateTotalLabel() {
        double total = ShoppingCart.getInstance().calculateTotal();
        // Assuming VAT is included or calculated here.

        // Project doc says "total cost including taxes (VAT)"
        if (ShoppingCart.getInstance().getAppliedCoupon() != null) {
            totalPriceLabel.setText(String.format("Total: %.2f TL (Coupon Applied)", total));
        } else {
            totalPriceLabel.setText(String.format("Total: %.2f TL", total));
        }
    }

    /**
     * Validates user authentication, minimum order requirements, and delivery information
     * before finalizing the order via OrderService.
     * * @author Eren Çakır Bircan
     */
    @FXML
    private void handleCheckout() {
        User user = AuthService.getInstance().getCurrentUser();
        if (user == null) {
            showAlert("Login Required", "Please login to place an order.");
            return;
        }

        // 1. Min Cart Value Check
        if (ShoppingCart.getInstance().calculateTotal() < MIN_CART_VALUE) {
            showAlert("Minimum Order", "Minimum cart value must be " + MIN_CART_VALUE + " TL.");
            return;
        }

        // 2. Delivery Time Validation
        if (datePickerDelivery.getValue() == null || comboDeliveryTime.getValue() == null) {
            showAlert("Delivery Info Missing", "Please select a delivery date and time slot.");
            return;
        }

        // Combine date and time (simplified for now as String or LocalDateTime parsing)
        String deliveryInfo = datePickerDelivery.getValue().toString() + " " + comboDeliveryTime.getValue();

        try {
            // Pass delivery info to placeOrder (Requires updating OrderService signature)
            // For now, setting it via a transient method or updating the service first?
            // I'll update the Service call here assuming I'll update Service next.
            // Actually, let's keep the call signature same if I can't change it atomically,
            // OR change OrderService signature first.
            // Let's pass it as a separate argument.

            Order order = orderService.placeOrder(user, ShoppingCart.getInstance(), deliveryInfo);

            showAlert("Order Successful", "Your order has been placed!\nOrder ID: " + order.getId() + "\nInvoice generated.");

            // Close cart or refresh UI
            cartTable.refresh();
            updateTotalLabel();
            // Possibly close window?
            handleBack();
        } catch (IllegalStateException e) {
            showAlert("Cart Empty", "Your cart is empty.");
        } catch (Exception e) {
            showAlert("Error", "Could not place order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays a graphical information alert with a specific title and message.
     * * @param title the title of the alert window
     * @param content the message content to be shown
     * @author Zafer Mert Serinken
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Closes the current shopping cart window and returns to the previous scene.
     * * @author Eren Çakır Bircan
     */
    @FXML
    private void handleBack() {
        // Close the cart window
        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }
}