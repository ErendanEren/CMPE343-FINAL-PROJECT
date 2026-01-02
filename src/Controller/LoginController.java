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
 * Handles user authentication processes and manages role-based redirection
 * to specific dashboards (Customer, Carrier, or Owner).
 * * @author Zafer Mert Serinken
 */
public class LoginController {

    @FXML private MFXTextField usernameField;
    @FXML private MFXPasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthService authService;

    /**
     * Constructs a new LoginController.
     * Initializes the connection to the AuthService singleton.
     */
    public LoginController() {
        this.authService = AuthService.getInstance();
    }

    /**
     * Automatically called after the FXML file has been loaded.
     * Can be used for additional UI setup logic.
     */
    @FXML
    public void initialize() {
        // Initialization logic can be added here if needed
    }

    /**
     * Handles the login action.
     * Validates input fields, authenticates credentials via AuthService,
     * and redirects the user to the appropriate dashboard based on their role.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validate that fields are not empty
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        // Attempt authentication
        User user = authService.login(username, password);

        if (user != null) {
            // Success: clear errors and store session data
            errorLabel.setText("");
            SceneManager.putData("currentUser", user);

            // Navigate to the correct interface based on user role
            switch (user.getRole()) {
                case "CUSTOMER":
                    SceneManager.switchSceneStatic("/fxml/CustomerDashboard.fxml");
                    break;
                case "CARRIER":
                    SceneManager.switchSceneStatic("/fxml/CarrierDashboard.fxml");
                    break;
                case "OWNER":
                    SceneManager.switchSceneStatic("/fxml/OwnerDashboard.fxml");
                    break;
                default:
                    errorLabel.setText("Unknown role: " + user.getRole());
            }
        } else {
            // Failure: display error message
            errorLabel.setText("Invalid username or password.");
        }
    }

    /**
     * Navigates the user to the registration (Sign Up) screen.
     */
    @FXML
    private void handleSignUp() {
        SceneManager.switchSceneStatic("/fxml/register.fxml");
    }
}