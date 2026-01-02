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
 * Controller class for managing user profile information.
 * Handles displaying current user data and persisting profile updates.
 * * @author Eren Çakır Bircan
 */
public class ProfileController {

    @FXML private MFXTextField nameField;
    @FXML private MFXTextField phoneField;
    @FXML private MFXTextField emailField;
    @FXML private MFXTextField addressField;
    @FXML private MFXPasswordField passwordField;

    private UserDAO userDAO = new DBUserDAO();
    private User currentUser;

    /**
     * Initializes the profile view by retrieving the current user from AuthService
     * and populating the text fields with existing profile data.
     * * @author Eren Çakır Bircan
     */
    @FXML
    public void initialize() {
        currentUser = AuthService.getInstance().getCurrentUser();
        if (currentUser != null) {
            nameField.setText(currentUser.getFullName());
            phoneField.setText(currentUser.getPhone());
            emailField.setText(currentUser.getEmail());
            addressField.setText(currentUser.getAddress());
        }
    }

    /**
     * Collects updated data from the UI fields and updates the current user's profile.
     * If a new password is provided, it updates the password hash before saving via UserDAO.
     * * @author Eren Çakır Bircan
     */
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

    /**
     * Utility method to display a graphical alert with a custom title and message.
     * * @param title The title of the alert dialog.
     * @param content The descriptive text to be displayed in the alert.
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