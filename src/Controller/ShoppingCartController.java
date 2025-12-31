package Controller;

import Models.CartItem;
import Models.Order;
import Models.ShoppingCart;
import Models.User;
import Service.OrderService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ShoppingCartController {

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> nameColumn;
    @FXML private TableColumn<CartItem, Double> amountColumn;
    @FXML private TableColumn<CartItem, String> priceColumn;
    @FXML private TableColumn<CartItem, String> totalColumn;
    @FXML private Label totalPriceLabel;

    private OrderService orderService = new OrderService();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getName()));

        amountColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        priceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f TL", cellData.getValue().getProduct().getEffectivePrice())));

        totalColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f TL", cellData.getValue().getItemTotal())));

        cartTable.setItems(ShoppingCart.getInstance().getItems());

        // Listener to update total when items change
        ShoppingCart.getInstance().getItems().addListener((javafx.collections.ListChangeListener.Change<? extends CartItem> c) -> {
            updateTotalLabel();
        });

        updateTotalLabel();
    }

    @FXML
    private void handleRemoveItem() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ShoppingCart.getInstance().removeItem(selected);
            updateTotalLabel();
        } else {
            showAlert("No Selection", "Please select an item to remove.");
        }
    }

    @FXML
    private void handleCheckout() {
        if (ShoppingCart.getInstance().getItems().isEmpty()) {
            showAlert("Cart Empty", "Your cart is empty.");
            return;
        }

        try {
            // Mocking a loaded user
            User currentUser = new User("customer", "pass", "John Doe", "555-1234", "john@example.com", "My Home Address", "CUSTOMER");
            currentUser.setId(1);

            Order order = orderService.placeOrder(currentUser, ShoppingCart.getInstance(), "Not Selected (Legacy Cart)");

            String invoice = orderService.generateInvoice(order);
            System.out.println(invoice); // Print invoice to console for now

            showAlert("Order Placed", "Your order has been placed successfully!\nCheck console for invoice.");

            // Close the cart window
            closeWindow();
        } catch (Exception e) {
            showAlert("Error", "Checkout failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        closeWindow();
    }

    private void updateTotalLabel() {
        double total = ShoppingCart.getInstance().calculateTotal();
        totalPriceLabel.setText(String.format("Total: %.2f TL", total));
    }

    private void closeWindow() {
        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
