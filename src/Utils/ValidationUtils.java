package Utils;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ValidationUtils {

    // Sadece harf ve boşluklara izin ver (Sayı ve özel karakter yok)
    private static final String NAME_REGEX = "^[a-zA-Z\\s]+$";

    public static boolean isValidName(String name) {
        return name != null && name.matches(NAME_REGEX);
    }

    public static boolean validateNameField(MFXTextField field, String fieldName) {
        if (!isValidName(field.getText())) {
            showAlert("Validation Error", fieldName + " can only contain letters and spaces.");
            return false;
        }
        return true;
    }

    // Overload for regular TextField if needed
    public static boolean validateNameField(TextField field, String fieldName) {
        if (!isValidName(field.getText())) {
            showAlert("Validation Error", fieldName + " can only contain letters and spaces.");
            return false;
        }
        return true;
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
