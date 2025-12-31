package Controller;

import Service.AuthService;
import Models.User;
import Utils.SceneManager;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoginController {

    @FXML private MFXTextField usernameField;
    @FXML private MFXPasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    @FXML
    public void initialize() {
        // Init logic if needed
    }

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
            // Login success
            errorLabel.setText("");
            SceneManager.putData("currentUser", user);

            // Redirect based on role
            switch (user.getRole()) {
                case "CUSTOMER":
                    SceneManager.switchSceneStatic("/customer.fxml");
                    break;
                case "CARRIER":
                    SceneManager.switchSceneStatic("/carrier.fxml");
                    break;
                case "OWNER":
                    SceneManager.switchSceneStatic("/owner.fxml");
                    break;
                default:
                    errorLabel.setText("Unknown role: " + user.getRole());
            }
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    private void handleSignUp() {
        SceneManager.switchSceneStatic("/register.fxml");
    }
}
