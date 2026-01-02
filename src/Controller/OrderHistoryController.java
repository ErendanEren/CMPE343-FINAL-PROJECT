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
 * Controller class for managing the Order History interface.
 * Allows users to view their past orders, status, and generate invoices.
 *
 * @author Arda Dülger
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
     * Initializes the controller class. Sets up table column bindings and
     * triggers the initial loading of order data for the current user.
     *
     * @author Arda Dülger
     */
    @FXML
    public void initialize() {
        currentUser = AuthService.getInstance().getCurrentUser();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOrderTime().toString()));

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        totalColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f TL", cellData.getValue().getTotalAmount())));

        loadOrders();
    }

    /**
     * Fetches order history data from the database based on the current user's ID
     * and populates the history table.
     *
     * @author Arda Dülger
     */
    private void loadOrders() {
        if (currentUser != null) {
            historyTable.setItems(FXCollections.observableArrayList(
                    orderDao.getOrdersByCustomer(currentUser.getId())
            ));
        }
    }

    /**
     * Generates a text-based invoice for the selected order in the table
     * and displays it through the system console and a user alert.
     *
     * @author Arda Dülger
     */
    @FXML
    private void handleViewInvoice() {
        Order selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String invoice = orderService.generateInvoice(selected);
            System.out.println("--- Invoice displayed in console ---");
            System.out.println(invoice);
            showAlert("Invoice Generated", "Invoice has been printed to the console.\n(Order ID: " + selected.getId() + ")");
        } else {
            showAlert("No Selection", "Please select an order to view invoice.");
        }
    }

    /**
     * Helper method to display an information alert dialog.
     *
     * @param title The title of the alert window.
     * @param content The information message to be displayed.
     * @author Arda Dülger
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}