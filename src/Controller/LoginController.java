package Controller;

import Service.AuthService;
import Models.User;
import Utils.SceneManager;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller class for the Login interface.
 * Manages user authentication, input validation, and role-based redirection.
 * * @author Eren Çakır Bircan
 */
public class LoginController {

    @FXML private MFXTextField usernameField;
    @FXML private MFXPasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthService authService;

    /**
     * Constructor for LoginController.
     * Initializes the authentication service.
     * * @author Eren Çakır Bircan
     */
    public LoginController() {
        this.authService = new AuthService();
    }

    /**
     * Initializes the controller class after the FXML has been loaded.
     * * @author Eren Çakır Bircan
     */
    @FXML
    public void initialize() {
    }

    /**
     * Handles the login action by validating inputs, authenticating the user
     * through AuthService, and redirecting to the appropriate dashboard based on user role.
     * * @author Eren Çakır Bircan
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        User user = authService.login(username, password);

        if (user != null) {
            errorLabel.setText("");
            SceneManager.putData("currentUser", user);

            switch (user.getRole()) {
                case "CUSTOMER":
                    SceneManager.switchSceneStatic("/fxml/CustomerDashboard.fxml");
                    break;
                case "CARRIER":
                    SceneManager.switchSceneStatic("/fxml/StaffManager.fxml");
                    break;
                case "OWNER":
                    SceneManager.switchSceneStatic("/fxml/OwnerDashboard.fxml");
                    break;
                default:
                    errorLabel.setText("Unknown role: " + user.getRole());
            }
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }

    /**
     * Navigates the user to the registration (sign up) screen.
     * * @author Eren Çakır Bircan
     */
    @FXML
    private void handleSignUp() {
        SceneManager.switchSceneStatic("/fxml/register.fxml");
    }
}