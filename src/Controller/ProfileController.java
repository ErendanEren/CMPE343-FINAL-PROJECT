package Controller;

import Dao.DBUserDAO;
import Dao.UserDAO;
import Models.User;
import Service.AuthService;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Controller class for the User Profile interface.
 * Fulfills Member 2's responsibility to manage user profiles, order history,
 * and personal communication history.
 *
 * @author Zafer Mert Serinken
 */
public class ProfileController {

    @FXML private MFXTextField nameField;
    @FXML private MFXTextField phoneField;
    @FXML private MFXTextField emailField;
    @FXML private MFXTextField addressField;
    @FXML private MFXPasswordField passwordField;

    @FXML private javafx.scene.control.TableView<Models.Order> orderTable;
    @FXML private javafx.scene.control.TableColumn<Models.Order, Integer> colOrderId;
    @FXML private javafx.scene.control.TableColumn<Models.Order, String> colDate;
    @FXML private javafx.scene.control.TableColumn<Models.Order, String> colStatus;
    @FXML private javafx.scene.control.TableColumn<Models.Order, Double> colTotal;
    @FXML private javafx.scene.control.TableColumn<Models.Order, String> colDelivered;

    @FXML private javafx.scene.control.ListView<String> messageList;

    private UserDAO userDAO = new DBUserDAO();
    private Dao.OrderDao orderDao = new Dao.DBOrderDAO();
    private Dao.MessageDao messageDao = new Dao.MessageDao();
    private User currentUser;

    /**
     * Initializes the controller class.
     * Loads the current user's profile information into the form and
     * populates the order history and message tabs.
     */
    @FXML
    public void initialize() {
        currentUser = AuthService.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Populate personal information fields
            nameField.setText(currentUser.getFullName());
            phoneField.setText(currentUser.getPhone());
            emailField.setText(currentUser.getEmail());
            addressField.setText(currentUser.getAddress());

            // Initialize table structures
            setupOrderTable();

            // Load historical data from database
            loadOrders();
            loadMessages();
        }
    }

    /**
     * Configures the cell value factories for the order history TableView.
     * Maps Order model properties to UI columns.
     */
    private void setupOrderTable() {
        colOrderId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("orderTime"));
        colStatus.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
        colTotal.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("totalAmount"));
        colDelivered.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("deliveredAt"));
    }

    /**
     * Handles the save action for profile updates.
     * Performs field validation and persists changes to the database.
     */
    @FXML
    private void handleSave() {
        if (currentUser == null) return;

        // Perform field validation [Requirement: Member 2 Infrastructure]
        if (!Utils.ValidationUtils.validateNameField(nameField, "Full Name")) {
            return;
        }

        currentUser.setFullName(nameField.getText());
        currentUser.setPhone(phoneField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setAddress(addressField.getText());

        // Update password if a new value is provided
        String newPass = passwordField.getText();
        if (newPass != null && !newPass.isEmpty()) {
            currentUser.setPasswordHash(newPass);
        }

        // Persist updated user info to the database
        userDAO.updateUser(currentUser);
        showAlert("Success", "Profile updated successfully!");
    }

    /**
     * Event handler to refresh the order history view manually.
     */
    @FXML
    private void handleRefreshOrders() {
        loadOrders();
    }

    /**
     * Event handler to refresh the message history view manually.
     */
    @FXML
    private void handleRefreshMessages() {
        loadMessages();
    }

    /**
     * Fetches order records for the current customer from the database.
     */
    private void loadOrders() {
        if (currentUser != null) {
            java.util.List<Models.Order> orders = orderDao.getOrdersByCustomer(currentUser.getId());
            orderTable.setItems(javafx.collections.FXCollections.observableArrayList(orders));
        }
    }

    /**
     * Fetches message records for the current user and formats them for the ListView.
     */
    private void loadMessages() {
        if (currentUser != null) {
            java.util.List<Models.Message> msgs = messageDao.getMessagesForUser(currentUser.getId());
            messageList.getItems().clear();
            for (Models.Message m : msgs) {
                // Apply readable formatting to message strings
                String display = String.format("[%s] From %d: %s", m.getSentAt(), m.getSenderId(), m.getContent());
                messageList.getItems().add(display);
            }
        }
    }

    /**
     * Utility method to display a standard information alert dialog.
     *
     * @param title   The title of the alert window.
     * @param content The message body to display.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}