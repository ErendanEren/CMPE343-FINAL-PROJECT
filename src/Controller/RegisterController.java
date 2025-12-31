package Controller;

import Service.AuthService;
import javafx.fxml.FXML;
import Utils.SceneManager;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.control.Label;

public class RegisterController {

    @FXML private MFXTextField usernameField;
    @FXML private MFXPasswordField passwordField;
    @FXML private MFXTextField fullNameField;
    @FXML private MFXTextField phoneField;
    @FXML private MFXTextField emailField;
    @FXML private MFXTextField addressArea;
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
            SceneManager.switchSceneStatic("/login.fxml");
        } else {
            statusLabel.setText("Registration failed. Username may be taken.");
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchSceneStatic("/login.fxml");
    }
}
