package Controller;

import Models.CartItem;
import Models.ShoppingCart;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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

    /**
     * Updates the total price label including VAT text.
     */
    private void updateTotalLabel() {
        double total = ShoppingCart.getInstance().calculateTotal();
        // Assuming VAT is included or calculated here.
        // Project doc says "total cost including taxes (VAT)" [cite: 32]
        totalPriceLabel.setText(String.format("Total: %.2f TL", total));
    }

    @FXML
    private void handleCheckout() {
        System.out.println("Proceeding to checkout...");
        // TODO: Open Checkout Dialog (Date selection etc.)
    }

    @FXML
    private void handleBack() {
        // Close the cart window
        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }
}