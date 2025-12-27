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

public class OwnerController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private MFXButton btnProducts;
    @FXML private MFXButton btnStaff;
    @FXML private MFXButton btnReports;
    @FXML private MFXButton btnLogout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Yollara "/fxml" eklendi:
        btnProducts.setOnAction(e -> loadPage("/fxml/ProductManager.fxml"));

        btnStaff.setOnAction(e -> loadPage("/fxml/StaffManager.fxml"));

        btnReports.setOnAction(e -> loadPage("/fxml/Reports.fxml"));

        btnLogout.setOnAction(e -> {
            System.out.println("Çıkış yapılıyor...");
        });
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainPane.setCenter(view); // Ortadaki alanı değiştir
        } catch (IOException e) {
            System.err.println("Sayfa yüklenemedi: " + fxmlPath);
            e.printStackTrace();
        }
    }
}