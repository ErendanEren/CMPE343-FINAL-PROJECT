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
 * Displays selected items, allows removal, handles coupon application,
 * and manages the final checkout process.
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
     * Initializes the controller class.
     * Sets up table column bindings, populates delivery time slots,
     * and displays available coupons from the database.
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

        // Fetch and display available coupons for the user
        Dao.CouponDAO couponDAO = new Dao.DBCouponDAO();
        java.util.List<Models.Coupon> coupons = couponDAO.getAllCoupons();
        if (!coupons.isEmpty()) {
            StringBuilder couponCodes = new StringBuilder("Available Coupons: ");
            for (Models.Coupon c : coupons) {
                couponCodes.append(c.getCode()).append(" ");
            }
            lblCouponMessage.setText(couponCodes.toString());
            lblCouponMessage.setStyle("-fx-text-fill: green;");
        }
    }

    /**
     * Removes the selected item from the shopping cart.
     * Refreshes the table view and the total price after removal.
     */
    @FXML
    private void handleRemoveItem() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Removing item from UI: " + selected.getProduct().getName());
            ShoppingCart.getInstance().removeItem(selected);
            updateTotalLabel();
        } else {
            System.out.println("No item selected to remove.");
            showAlert("No Selection", "Please select an item to remove.");
        }
    }

    /**
     * Validates and applies a coupon code entered by the user.
     * Updates the total price if the coupon is valid.
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
            updateTotalLabel();
        }
    }

    /**
     * Updates the total price label.
     * Incorporates VAT and reflects any applied discounts.
     */
    private void updateTotalLabel() {
        double total = ShoppingCart.getInstance().calculateTotal();

        if (ShoppingCart.getInstance().getAppliedCoupon() != null) {
            totalPriceLabel.setText(String.format("Total: %.2f TL (Coupon Applied)", total));
        } else {
            totalPriceLabel.setText(String.format("Total: %.2f TL", total));
        }
    }

    /**
     * Processes the final checkout.
     * Performs validation for user login, minimum cart value,
     * and delivery schedule before placing the order via OrderService.
     */
    @FXML
    private void handleCheckout() {
        User user = AuthService.getInstance().getCurrentUser();
        if (user == null) {
            showAlert("Login Required", "Please login to place an order.");
            return;
        }

        // Validate Minimum Cart Value
        if (ShoppingCart.getInstance().calculateTotal() < MIN_CART_VALUE) {
            showAlert("Minimum Order", "Minimum cart value must be " + MIN_CART_VALUE + " TL.");
            return;
        }

        // Validate Delivery Schedule Info
        if (datePickerDelivery.getValue() == null || comboDeliveryTime.getValue() == null) {
            showAlert("Delivery Info Missing", "Please select a delivery date and time slot.");
            return;
        }

        String deliveryInfo = datePickerDelivery.getValue().toString() + " " + comboDeliveryTime.getValue();

        try {
            // Place the order using the OrderService
            Order order = orderService.placeOrder(user, ShoppingCart.getInstance(), deliveryInfo);

            showAlert("Order Successful", "Your order has been placed!\nOrder ID: " + order.getId() + "\nInvoice generated.");

            // Refresh UI and return to main dashboard
            cartTable.refresh();
            updateTotalLabel();
            handleBack();
        } catch (IllegalStateException e) {
            showAlert("Cart Empty", "Your cart is empty.");
        } catch (Exception e) {
            showAlert("Error", "Could not place order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Utility method to display an alert dialog.
     * * @param title   The title of the alert.
     * @param content The message content of the alert.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Closes the current shopping cart window.
     */
    @FXML
    private void handleBack() {
        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }
}