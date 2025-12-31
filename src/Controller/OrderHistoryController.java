package Controller;

import Dao.MockOrderDao;
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

public class OrderHistoryController {

    @FXML private TableView<Order> historyTable;
    @FXML private TableColumn<Order, String> idColumn;
    @FXML private TableColumn<Order, String> dateColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private TableColumn<Order, String> totalColumn;

    private OrderDao orderDao = MockOrderDao.getInstance();
    private OrderService orderService = new OrderService();
    private User currentUser;

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

    private void loadOrders() {
        if (currentUser != null) {
            historyTable.setItems(FXCollections.observableArrayList(
                    orderDao.getOrdersByCustomer(currentUser.getId()) // Ensure User class has getId()
            ));
        }
    }

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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
