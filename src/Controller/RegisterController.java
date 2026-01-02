package Controller;

import Service.AuthService;
import javafx.fxml.FXML;
import Utils.SceneManager;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.control.Label;

/**
 * Controller class for the User Registration interface.
 * Handles user input collection, validation, and account creation via AuthService.
 * * @author Eren Çakır Bircan
 */
public class RegisterController {

    @FXML private MFXTextField usernameField;
    @FXML private MFXPasswordField passwordField;
    @FXML private MFXTextField fullNameField;
    @FXML private MFXTextField phoneField;
    @FXML private MFXTextField emailField;
    @FXML private MFXTextField addressArea;
    @FXML private Label statusLabel;

    private AuthService authService;

    /**
     * Constructor for RegisterController.
     * Initializes the authentication service used for registration processes.
     * * @author Eren Çakır Bircan
     */
    public RegisterController() {
        this.authService = new AuthService();
    }

    /**
     * Processes the registration request. Validates the password length,
     * attempts to register the user through AuthService, and redirects to the login screen upon success.
     * * @author Eren Çakır Bircan
     */
    @FXML
    private void handleRegister() {
        String u = usernameField.getText();
        String p = passwordField.getText();

        if (p.length() < 4) {
            statusLabel.setText("Password must be at least 4 characters.");
            return;
        }

        boolean success = authService.register(
                u, p,
                fullNameField.getText(),
                phoneField.getText(),
                emailField.getText(),
                addressArea.getText()
        );

        if (success) {
            statusLabel.setText("Registration successful! Redirecting...");
            SceneManager.switchSceneStatic("/login.fxml");
        } else {
            statusLabel.setText("Registration failed. Username may be taken.");
        }
    }

    /**
     * Navigates the user back to the login screen.
     * * @author Eren Çakır Bircan
     */
    @FXML
    private void handleBack() {
        SceneManager.switchSceneStatic("/fxml/Login.fxml");
    }
}