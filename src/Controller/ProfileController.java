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

public class ProfileController {

    @FXML private MFXTextField nameField;
    @FXML private MFXTextField phoneField;
    @FXML private MFXTextField emailField;
    @FXML private MFXTextField addressField;
    @FXML private MFXPasswordField passwordField;

    // Order History Tab
    @FXML private javafx.scene.control.TableView<Models.Order> orderTable;
    @FXML private javafx.scene.control.TableColumn<Models.Order, Integer> colOrderId;
    @FXML private javafx.scene.control.TableColumn<Models.Order, String> colDate;
    @FXML private javafx.scene.control.TableColumn<Models.Order, String> colStatus;
    @FXML private javafx.scene.control.TableColumn<Models.Order, Double> colTotal;
    @FXML private javafx.scene.control.TableColumn<Models.Order, String> colDelivered;

    // Messages Tab
    @FXML private javafx.scene.control.ListView<String> messageList;

    private UserDAO userDAO = new DBUserDAO();
    private Dao.OrderDao orderDao = new Dao.DBOrderDAO();
    private Dao.MessageDao messageDao = new Dao.MessageDao();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = AuthService.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Fill Info
            nameField.setText(currentUser.getFullName());
            phoneField.setText(currentUser.getPhone());
            emailField.setText(currentUser.getEmail());
            addressField.setText(currentUser.getAddress());

            // Setup Tables
            setupOrderTable();

            // Load Data
            loadOrders();
            loadMessages();
        }
    }

    private void setupOrderTable() {
        colOrderId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("orderTime"));
        colStatus.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
        colTotal.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("totalAmount"));
        colDelivered.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("deliveredAt"));
    }

    @FXML
    private void handleSave() {
        if (currentUser == null) return;

        // VALIDATION
        if (!Utils.ValidationUtils.validateNameField(nameField, "Full Name")) {
            return;
        }

        currentUser.setFullName(nameField.getText());
        currentUser.setPhone(phoneField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setAddress(addressField.getText());

        String newPass = passwordField.getText();
        if (newPass != null && !newPass.isEmpty()) {
            currentUser.setPasswordHash(newPass);
        }

        userDAO.updateUser(currentUser);
        showAlert("Success", "Profile updated successfully!");
    }

    @FXML
    private void handleRefreshOrders() {
        loadOrders();
    }

    @FXML
    private void handleRefreshMessages() {
        loadMessages();
    }

    private void loadOrders() {
        if (currentUser != null) {
            java.util.List<Models.Order> orders = orderDao.getOrdersByCustomer(currentUser.getId());
            orderTable.setItems(javafx.collections.FXCollections.observableArrayList(orders));
        }
    }

    private void loadMessages() {
        if (currentUser != null) {
            java.util.List<Models.Message> msgs = messageDao.getMessagesForUser(currentUser.getId());
            messageList.getItems().clear();
            for (Models.Message m : msgs) {
                // Formatting message display
                String display = String.format("[%s] From %d: %s", m.getSentAt(), m.getSenderId(), m.getContent());
                messageList.getItems().add(display);
            }
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
