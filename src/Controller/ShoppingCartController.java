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

/**
 * Controller class for the Shopping Cart interface.
 * Manages the display of items, removal of products, and the checkout process.
 * * @author Eren Çakır Bircan
 */
public class ShoppingCartController {

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> nameColumn;
    @FXML private TableColumn<CartItem, Double> amountColumn;
    @FXML private TableColumn<CartItem, String> priceColumn;
    @FXML private TableColumn<CartItem, String> totalColumn;
    @FXML private Label totalPriceLabel;

    private OrderService orderService = new OrderService();

    /**
     * Initializes the shopping cart view. Sets up table column bindings and
     * attaches a listener to the cart items to update the total price automatically.
     * * @author Eren Çakır Bircan
     */
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

        ShoppingCart.getInstance().getItems().addListener((javafx.collections.ListChangeListener.Change<? extends CartItem> c) -> {
            updateTotalLabel();
        });

        updateTotalLabel();
    }

    /**
     * Handles the removal of a selected item from the shopping cart.
     * If no item is selected, it displays an error alert.
     * * @author Eren Çakır Bircan
     */
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

    /**
     * Handles the checkout operation. Validates the cart status, creates a new order,
     * generates an invoice, and closes the cart window upon success.
     * * @author Eren Çakır Bircan
     */
    @FXML
    private void handleCheckout() {
        if (ShoppingCart.getInstance().getItems().isEmpty()) {
            showAlert("Cart Empty", "Your cart is empty.");
            return;
        }

        try {
            User currentUser = new User("customer", "pass", "John Doe", "555-1234", "john@example.com", "My Home Address", "CUSTOMER");
            currentUser.setId(1);

            Order order = orderService.placeOrder(currentUser, ShoppingCart.getInstance(), "Not Selected (Legacy Cart)");

            String invoice = orderService.generateInvoice(order);
            System.out.println(invoice);

            showAlert("Order Placed", "Your order has been placed successfully!\nCheck console for invoice.");

            closeWindow();
        } catch (Exception e) {
            showAlert("Error", "Checkout failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Closes the shopping cart window and returns to the previous screen.
     * * @author Eren Çakır Bircan
     */
    @FXML
    private void handleBack() {
        closeWindow();
    }

    /**
     * Updates the total price label with the current calculation from the ShoppingCart singleton.
     * * @author Eren Çakır Bircan
     */
    private void updateTotalLabel() {
        double total = ShoppingCart.getInstance().calculateTotal();
        totalPriceLabel.setText(String.format("Total: %.2f TL", total));
    }

    /**
     * Closes the current stage (window).
     * * @author Eren Çakır Bircan
     */
    private void closeWindow() {
        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }

    /**
     * Displays a graphical information alert to the user.
     * * @param title The title of the alert window.
     * @param content The text message to be shown.
     * @author Eren Çakır Bircan
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}