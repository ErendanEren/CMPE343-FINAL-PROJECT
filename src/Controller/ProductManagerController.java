package Controller;

import Dao.DBProductDAO;
import Dao.ProductDAO;
import Models.Product;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for managing the product inventory.
 * Provides CRUD operations and table management for products.
 * * @author Selçuk Aloba
 * @author Eren Çakır Bircan
 */
public class ProductManagerController implements Initializable {

    @FXML private TableView<Product> tableProducts;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colType;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Double> colStock;
    @FXML private TableColumn<Product, Double> colThreshold;

    @FXML private MFXTextField txtName;
    @FXML private MFXComboBox<String> comboType;
    @FXML private MFXTextField txtPrice;
    @FXML private MFXTextField txtStock;
    @FXML private MFXTextField txtThreshold;
    @FXML private MFXButton btnChooseImage;
    @FXML private MFXButton btnAdd;
    @FXML private MFXButton btnUpdate;
    @FXML private MFXButton btnDelete;

    private ProductDAO productDAO = new DBProductDAO();
    private ObservableList<Product> productList;
    private String selectedImagePath = "";

    /**
     * Initializes the controller class. Sets up the UI components,
     * table columns, and loads initial data from the database.
     * * @param location  The location used to resolve relative paths for the root object.
     * @param resources The resources used to localize the root object.
     * @author Selçuk Aloba
     * @author Eren Çakır Bircan
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
        setupActions();

        comboType.setItems(FXCollections.observableArrayList("FRUIT", "VEGETABLE"));
    }

    /**
     * Configures the TableView columns by mapping them to the Product model properties.
     * * @author Selçuk Aloba
     * @author Eren Çakır Bircan
     */
    private void setupTable() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerKg"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockKg"));
        colThreshold.setCellValueFactory(new PropertyValueFactory<>("thresholdKg"));
    }

    /**
     * Retrieves all product data from the database and refreshes the TableView.
     * * @author Selçuk Aloba
     * @author Eren Çakır Bircan
     */
    private void loadData() {
        productList = FXCollections.observableArrayList(productDAO.getAllProducts());
        tableProducts.setItems(productList);
    }

    /**
     * Configures event listeners for the buttons and table selection
     * to handle adding, updating, and deleting products.
     * * @author Selçuk Aloba
     * @author Eren Çakır Bircan
     */
    private void setupActions() {
        btnChooseImage.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Ürün Resmi Seç");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                selectedImagePath = selectedFile.getAbsolutePath();
                btnChooseImage.setText("Seçildi: " + selectedFile.getName());
            }
        });

        btnAdd.setOnAction(event -> {
            try {
                String name = txtName.getText();
                String type = comboType.getValue();
                double price = Double.parseDouble(txtPrice.getText());
                double stock = Double.parseDouble(txtStock.getText());
                double threshold = Double.parseDouble(txtThreshold.getText());

                Product newProduct = new Product(0, name, type, price, stock, threshold, selectedImagePath);
                productDAO.addProduct(newProduct);
                loadData();
                clearForm();

            } catch (Exception e) {
                System.out.println("Hata: " + e.getMessage());
            }
        });

        tableProducts.getSelectionModel