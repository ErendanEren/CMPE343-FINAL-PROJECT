package Controller;

import Service.AuthService;
import Utils.SceneManager;
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
 * Controller class for the Carrier Module.
 * Manages order fulfillment workflow, real-time order tracking,
 * performance analytics, and communication with customers.
 * * @author Zafer Mert Serinken
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
     * Initializes the controller. Sets up table columns, loads initial data,
     * and starts a background timeline to refresh order tables every 3 seconds.
     */
    @FXML
    public void initialize() {
        this.currentUser = (User) SceneManager.getData("currentUser");

        setupTableColumns();
        refreshTables();
        loadRatings();

        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), event -> {
                    refreshTables();
                })
        );
        timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Configures the cell value factories for all TableViews.
     * Maps Order model properties to their respective UI columns.
     */
    private void setupTableColumns() {
        // Available Table Mapping
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddressSnapshot"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        // Active Table Mapping
        colActiveId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colActiveAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddressSnapshot"));

        // Completed Table Mapping
        colCompId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCompAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddressSnapshot"));
        colCompAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colCompDeliveredAt.setCellValueFactory(new PropertyValueFactory<>("deliveredAt"));
    }

    /**
     * Synchronizes the UI tables with the database.
     * Distributes orders into Available, Active (Assigned), and Completed (Delivered) categories.
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
     * Event handler for picking up an available order.
     * Attempts to assign the selected order to the current carrier in the database.
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
     * Event handler for marking an order as delivered.
     * Updates the order status and records the delivery timestamp in the database.
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
     * Event handler for sending a message to the customer of a selected active order.
     * Opens a text input dialog to capture and send the message content.
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
     * Loads performance metrics and customer reviews for the current carrier.
     * Calculates the average rating and updates the visual performance label.
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
     * Utility method to display information alerts to the user.
     * * @param title The title of the alert window.
     * @param content The message to be displayed.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Handles the user logout process.
     * Clears the current session and redirects the user to the Login screen.
     */
    @FXML
    private void handleLogout() {
        AuthService.getInstance().logout();
        SceneManager.switchSceneStatic("/fxml/Login.fxml");
    }
}