package Controller;

import Service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextArea addressArea;
    @FXML private Label statusLabel;

    private AuthService authService;

    public RegisterController() {
        this.authService = new AuthService();
    }

    @FXML
    private void handleRegister() {
        String u = usernameField.getText();
        String p = passwordField.getText();
        
        // Basic validation
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
            // Possibly wait or button to go back
            // For now, instant redirect to login
            SceneManager.switchScene("/login.fxml");
        } else {
            statusLabel.setText("Registration failed. Username may be taken.");
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/login.fxml");
    }
}
