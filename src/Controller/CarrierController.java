package Controller;

import App.SceneManager;
import Dao.CarrierDAO;
import Dao.MessageDao;
import Models.CarrierRating;
import Models.Message;
import Models.Order;
import Models.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing the Carrier dashboard interface.
 * Handles order assignments, delivery updates, messaging with customers,
 * and displaying performance ratings.
 * * @author Arda Dülger
 */
public class CarrierController {

    @FXML private TableView<Order> availableTable;
    @FXML private TableColumn<Order, Integer> colId;
    @FXML private TableColumn<Order, String> colAddress;
    @FXML private TableColumn<Order, Double> colAmount;

    @FXML private TableView<Order> activeTable;
    @FXML private TableColumn<Order, Integer> colActiveId;
    @FXML private TableColumn<Order, String> colActiveAddress;

    @FXML private TableView<Order> completedTable;
    @FXML private TableColumn<Order, Integer> colCompId;
    @FXML private TableColumn<Order, String> colCompAddress;
    @FXML private TableColumn<Order, Double> colCompAmount;
    @FXML private TableColumn<Order, LocalDateTime> colCompDeliveredAt;

    @FXML private ListView<String> ratingListView;
    @FXML private Label lblAverageRating;

    private CarrierDAO carrierDAO = new CarrierDAO();
    private MessageDao messageDao = new MessageDao();
    private User currentUser;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded. It sets up the current user,
     * table columns, and loads initial data.
     * * @author Arda Dülger
     */
    @FXML
    public void initialize() {
        this.currentUser = (User) SceneManager.getData("currentUser");

        setupTableColumns();
        refreshTables();
        loadRatings();
    }

    /**
     * Configures the cell value factories for all TableView columns,
     * mapping them to the respective fields in the Order model.
     * * @author Arda Dülger
     */
    private void setupTableColumns() {
        // Available Table
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddressSnapshot"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        // Active Table
        colActiveId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colActiveAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddressSnapshot"));

        // Completed Table
        colCompId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCompAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddressSnapshot"));
        colCompAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colCompDeliveredAt.setCellValueFactory(new PropertyValueFactory<>("deliveredAt"));
    }

    /**
     * Fetches current order data from the database and updates the
     * Available, Active, and Completed order tables based on the current carrier's status.
     * * @author Arda Dülger
     */
    private void refreshTables() {
        availableTable.setItems(FXCollections.observableArrayList(carrierDAO.getAvailableOrders()));

        if (currentUser != null) {
            activeTable.setItems(FXCollections.observableArrayList(
                    carrierDAO.getOrdersByCarrier(currentUser.getId(), "ASSIGNED")
            ));
            completedTable.setItems(FXCollections.observableArrayList(
                    carrierDAO.getOrdersByCarrier(currentUser.getId(), "DELIVERED")
            ));
        }
    }

    /**
     * Handles the pickup action. Assigns the selected available order
     * to the current carrier and updates the UI.
     * * @author Arda Dülger
     */
    @FXML
    private void handlePickUp() {
        Order selected = availableTable.getSelectionModel().getSelectedItem();
        if (selected != null && currentUser != null) {
            boolean success = carrierDAO.assignOrderToCarrier(selected.getId(), currentUser.getId());
            if (success) {
                refreshTables();
                showAlert("Success", "Order successfully assigned to you.");
            } else {
                refreshTables();
                showAlert("Conflict Error", "This order was just picked up by another carrier!");
            }
        } else if (selected == null) {
            showAlert("Selection Error", "Please select an order to pick up.");
        }
    }

    /**
     * Handles the delivery completion. Marks the selected active order
     * as delivered and records the delivery timestamp in the database.
     * * @author Arda Dülger
     */
    @FXML
    private void handleDeliver() {
        Order selected = activeTable.getSelectionModel().getSelectedItem();
        if (selected != null && currentUser != null) {
            boolean success = carrierDAO.completeOrder(selected.getId());
            if (success) {
                refreshTables();
                showAlert("Success", "Order marked as delivered.");
            }
        } else if (selected == null) {
            showAlert("Selection Error", "Please select an active order to mark as delivered.");
        }
    }

    /**
     * Opens a message dialog to allow the carrier to send a message
     * to the customer associated with the selected active order.
     * * @author Arda Dülger
     */
    @FXML
    private void handleSendMessage() {
        Order selected = activeTable.getSelectionModel().getSelectedItem();

        if (selected != null && currentUser != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Messenger");
            dialog.setHeaderText("Contact Customer for Order #" + selected.getId());
            dialog.setContentText("Enter your message:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(content -> {
                if (!content.trim().isEmpty()) {
                    Message msg = new Message();
                    msg.setSenderId(currentUser.getId());
                    msg.setReceiverId(selected.getCustomerId());
                    msg.setContent(content);

                    if (messageDao.sendMessage(msg)) {
                        showAlert("Sent", "Message sent to customer.");
                    } else {
                        showAlert("Error", "Could not send message.");
                    }
                }
            });
        } else {
            showAlert("Selection Error", "Please select an order to contact the customer.");
        }
    }

    /**
     * Loads and displays the ratings and comments given to the current carrier.
     * Also calculates and displays the average performance score.
     * * @author Arda Dülger
     */
    private void loadRatings() {
        if (currentUser != null && ratingListView != null) {
            List<CarrierRating> ratings = carrierDAO.getRatingsForCarrier(currentUser.getId());
            double sum = 0;
            ratingListView.getItems().clear();

            for (CarrierRating r : ratings) {
                sum += r.getRating();
                ratingListView.getItems().add("⭐ " + r.getRating() + "/5 | " + r.getComment());
            }

            if (!ratings.isEmpty() && lblAverageRating != null) {
                double avg = sum / ratings.size();
                lblAverageRating.setText(String.format("Performance: %.1f / 5.0", avg));
            }
        }
    }

    /**
     * Helper method to display information alerts to the user.
     * * @param title The title of the alert window.
     * @param content The message content to be displayed.
     * @author Arda Dülger
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}