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

    private OrderService orderService = new OrderService();

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
    }

    /**
     * Removes the selected item from the cart and refreshes the total.
     */
    @FXML
    private void handleRemoveItem() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ShoppingCart.getInstance().removeItem(selected);
            updateTotalLabel();
        }
    }

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
     * Updates the total price label including VAT text.
     */
    private void updateTotalLabel() {
        double total = ShoppingCart.getInstance().calculateTotal();
        // Assuming VAT is included or calculated here.

        // Project doc says "total cost including taxes (VAT)" [cite: 32]
        if (ShoppingCart.getInstance().getAppliedCoupon() != null) {
            totalPriceLabel.setText(String.format("Total: %.2f TL (Coupon Applied)", total));
        } else {
            totalPriceLabel.setText(String.format("Total: %.2f TL", total));
        }
    }

    @FXML
    private void handleCheckout() {
        User user = AuthService.getInstance().getCurrentUser();
        if (user == null) {
            showAlert("Login Required", "Please login to place an order.");
            return;
        }

        try {
            Order order = orderService.placeOrder(user, ShoppingCart.getInstance());
            showAlert("Order Successful", "Your order has been placed!\nOrder ID: " + order.getId());
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        // Close the cart window
        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }
}