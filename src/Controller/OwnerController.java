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
 * Controller class for the Owner Dashboard.
 * Manages the navigation between product management, staff management,
 * and reporting sections.
 * * @author selçuk aloba
 */
public class OwnerController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private MFXButton btnProducts;
    @FXML private MFXButton btnStaff;
    @FXML private MFXButton btnReports;
    @FXML private MFXButton btnLogout;

    /**
     * Initializes the controller class. Sets up action listeners for dashboard
     * buttons to facilitate dynamic page loading and logout operations.
     * * @param location  The location used to resolve relative paths for the root object.
     * @param resources The resources used to localize the root object.
     * @author selçuk aloba
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnProducts.setOnAction(e -> loadPage("/fxml/ProductManager.fxml"));

        btnStaff.setOnAction(e -> loadPage("/fxml/StaffManager.fxml"));

        btnReports.setOnAction(e -> loadPage("/fxml/Reports.fxml"));

        btnLogout.setOnAction(e -> {
            System.out.println("Çıkış yapılıyor...");
        });
    }

    /**
     * Dynamically loads an FXML view into the center area of the main BorderPane.
     * * @param fxmlPath The resource path to the FXML file to be loaded.
     * @author selçuk aloba
     */
    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainPane.setCenter(view);
        } catch (IOException e) {
            System.err.println("Sayfa yüklenemedi: " + fxmlPath);
            e.printStackTrace();
        }
    }
}