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

/**
 * Controller class for managing the staff (carriers) interface.
 * Provides functionality for owners to hire and fire staff members and view carrier lists.
 * * @author Selçuk Aloba
 */
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

    /**
     * Initializes the controller class. Sets up the table, loads data, and
     * configures the button actions.
     * * @param location The location used to resolve relative paths for the root object.
     * @param resources The resources used to localize the root object.
     * @author Selçuk Aloba
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
        setupActions();
    }

    /**
     * Configures the table columns by mapping them to the fields in the User model.
     * * @author Selçuk Aloba
     */
    private void setupTable() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    /**
     * Fetches all carrier data from the database and populates the TableView.
     * * @author Selçuk Aloba
     */
    private void loadData() {
        carrierList = FXCollections.observableArrayList(userDAO.getAllCarriers());
        tableStaff.setItems(carrierList);
    }

    /**
     * Sets up action listeners for hiring and firing staff members.
     * Handles user creation and deletion logic.
     * * @author Selçuk Aloba
     */
    private void setupActions() {
        btnHire.setOnAction(event -> {
            try {
                User newCarrier = new User(
                        txtUsername.getText(),
                        txtPassword.getText(),
                        txtFullName.getText(),
                        txtPhone.getText(),
                        txtEmail.getText(),
                        "",
                        "CARRIER"
                );
                userDAO.addCarrier(newCarrier);
                loadData();
                clearForm();
            } catch (Exception e) {
                System.out.println("Ekleme hatası: " + e.getMessage());
            }
        });

        btnFire.setOnAction(event -> {
            User selected = tableStaff.getSelectionModel().getSelectedItem();
            if (selected != null) {
                userDAO.deleteUser(selected.getUsername());
                loadData();
            }
        });
    }

    /**
     * Clears all input fields in the hiring form.
     * * @author Selçuk Aloba
     */
    private void clearForm() {
        txtUsername.clear();
        txtPassword.clear();
        txtFullName.clear();
        txtPhone.clear();
        txtEmail.clear();
    }
}