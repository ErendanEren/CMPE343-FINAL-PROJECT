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

    private UserDAO userDAO = new DBUserDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = AuthService.getInstance().getCurrentUser();
        if (currentUser != null) {
            nameField.setText(currentUser.getFullName());
            phoneField.setText(currentUser.getPhone());
            emailField.setText(currentUser.getEmail());
            addressField.setText(currentUser.getAddress());
            // Password usually not shown
        }
    }

    @FXML
    private void handleSave() {
        if (currentUser == null) return;

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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
