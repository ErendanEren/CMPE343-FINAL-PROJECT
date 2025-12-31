package Controller;

import Dao.DBUserDAO;
import Dao.UserDAO;
import Models.User;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class StaffManagerController implements Initializable {

    @FXML private TableView<User> tableStaff;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colFullName;
    @FXML private TableColumn<User, String> colPhone;
    @FXML private TableColumn<User, String> colEmail;

    @FXML private MFXTextField txtUsername;
    @FXML private MFXPasswordField txtPassword;
    @FXML private MFXTextField txtFullName;
    @FXML private MFXTextField txtPhone;
    @FXML private MFXTextField txtEmail;

    @FXML private MFXButton btnHire;
    @FXML private MFXButton btnFire;

    private UserDAO userDAO = new DBUserDAO();
    private ObservableList<User> carrierList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
        setupActions();
    }

    private void setupTable() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void loadData() {
        carrierList = FXCollections.observableArrayList(userDAO.getAllCarriers());
        tableStaff.setItems(carrierList);
    }

    private void setupActions() {
        // İşe Al (Ekle)
        btnHire.setOnAction(event -> {
            try {
                User newCarrier = new User(
                        txtUsername.getText(),
                        txtPassword.getText(), // Gerçekte hashlenmeli ama şimdilik düz
                        txtFullName.getText(),
                        txtPhone.getText(),
                        txtEmail.getText(),
                        "", // Adres boş olabilir kurye için
                        "CARRIER"
                );
                userDAO.addCarrier(newCarrier);
                loadData(); // Tabloyu yenile
                clearForm();
            } catch (Exception e) {
                System.out.println("Ekleme hatası: " + e.getMessage());
            }
        });

        // Kov (Sil)
        btnFire.setOnAction(event -> {
            User selected = tableStaff.getSelectionModel().getSelectedItem();
            if (selected != null) {
                userDAO.deleteUser(selected.getUsername());
                loadData();
            }
        });
    }

    private void clearForm() {
        txtUsername.clear();
        txtPassword.clear();
        txtFullName.clear();
        txtPhone.clear();
        txtEmail.clear();
    }
}