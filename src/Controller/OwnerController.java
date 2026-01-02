package Controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the Owner/Admin interface.
 * Manages the primary navigation for administrative tasks, including
 * product management, staff oversight, and statistical reporting.
 * This module aligns with Member 3's responsibilities.
 * * @author Zafer Mert Serinken
 */
public class OwnerController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private MFXButton btnProducts;
    @FXML private MFXButton btnStaff;
    @FXML private MFXButton btnReports;
    @FXML private MFXButton btnLogout;

    /**
     * Initializes the controller. Sets up the navigation button actions
     * to load specific sub-modules into the central view of the dashboard.
     *
     * @param location  The location used to resolve relative paths for the root object.
     * @param resources The resources used to localize the root object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Navigate to the Product Management view
        btnProducts.setOnAction(e -> loadPage("/fxml/ProductManager.fxml"));

        // Navigate to the Staff/Carrier Management view
        btnStaff.setOnAction(e -> loadPage("/fxml/StaffManager.fxml"));

        // Navigate to the Statistics and Reports view
        btnReports.setOnAction(e -> loadPage("/fxml/Reports.fxml"));

        // Handle user logout and redirection to the login screen
        btnLogout.setOnAction(e -> {
            Service.AuthService.getInstance().logout();
            Utils.SceneManager.switchSceneStatic("/fxml/Login.fxml");
        });
    }

    /**
     * Dynamically loads an FXML page and sets it as the center component of the main BorderPane.
     * Includes debugging logs to monitor resource loading and view switching.
     *
     * @param fxmlPath The relative path to the FXML resource to be loaded.
     */
    private void loadPage(String fxmlPath) {
        System.out.println("DEBUG: Requesting to load FXML: " + fxmlPath);
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("DEBUG: Resource NOT FOUND: " + fxmlPath);
                return;
            }
            System.out.println("DEBUG: Resource Found: " + resource.toExternalForm());

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            // Replace the central area of the BorderPane with the newly loaded view
            mainPane.setCenter(view);

            System.out.println("DEBUG: Scene switched successfully to: " + fxmlPath);
        } catch (IOException e) {
            System.err.println("DEBUG: Failed to load page: " + fxmlPath);
            e.printStackTrace();
        }
    }
}