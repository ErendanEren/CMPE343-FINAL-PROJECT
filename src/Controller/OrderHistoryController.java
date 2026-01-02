package Controller;

import Dao.DBOrderDAO;
import Dao.OrderDao;
import Models.Order;
import Models.User;
import Service.AuthService;
import Service.OrderService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Controller class for the Order History interface.
 * Responsible for displaying past transactions to the customer,
 * managing order status visibility, and generating invoices for previous purchases.
 * * @author Zafer Mert Serinken
 */
public class OrderHistoryController {

    @FXML private TableView<Order> historyTable;
    @FXML private TableColumn<Order, String> idColumn;
    @FXML private TableColumn<Order, String> dateColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, String> totalColumn;

    private OrderDao orderDao = new DBOrderDAO();
    private OrderService orderService = new OrderService();
    private User currentUser;

    /**
     * Initializes the controller. Sets up the TableView columns by binding
     * them to the Order model properties and loads the user's order history.
     */
    @FXML
    public void initialize() {
        currentUser = AuthService.getInstance().getCurrentUser();

        // Bind TableColumns to Order properties
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOrderTime().toString()));

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        totalColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f TL", cellData.getValue().getTotalAmount())));

        loadOrders();
    }

    /**
     * Fetches the order records for the currently authenticated customer
     * from the database and updates the UI table.
     */
    private void loadOrders() {
        if (currentUser != null) {
            historyTable.setItems(FXCollections.observableArrayList(
                    orderDao.getOrdersByCustomer(currentUser.getId())
            ));
        }
    }

    /**
     * Handles the request to view an invoice for a selected order.
     * Generates the invoice string via OrderService and displays it
     * in the system console and an alert dialog.
     */
    @FXML
    private void handleViewInvoice() {
        Order selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String invoice = orderService.generateInvoice(selected);

            // Log to console as per current implementation
            System.out.println("--- Invoice displayed in console ---");
            System.out.println(invoice);

            showAlert("Invoice Generated", "Invoice has been printed to the console.\n(Order ID: " + selected.getId() + ")");
        } else {
            showAlert("No Selection", "Please select an order to view invoice.");
        }
    }

    /**
     * Utility method to display a standard information alert.
     * * @param title   The title of the alert window.
     * @param content The descriptive message to be shown to the user.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}