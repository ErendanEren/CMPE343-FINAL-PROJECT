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
 * Controller class for the Product Management interface.
 * Fulfills Member 3's responsibility for the Owner Module (Admin).
 * Manages CRUD operations for products, including image handling as BLOBs.
 * * @author Zafer Mert Serinken
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
     * Initializes the controller class.
     * Sets up the product table, loads data from the database, and configures event handlers.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
        setupActions();

        // Populate the type selection ComboBox
        comboType.setItems(FXCollections.observableArrayList("FRUIT", "VEGETABLE"));
    }

    /**
     * Configures the TableView columns.
     * Maps properties to their respective Product model getter names.
     */
    private void setupTable() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerKg"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockKg"));
        colThreshold.setCellValueFactory(new PropertyValueFactory<>("thresholdKg"));
    }

    /**
     * Synchronizes the TableView with product records retrieved from the database.
     */
    private void loadData() {
        System.out.println("ProductManagerController: Loading data...");
        productList = FXCollections.observableArrayList(productDAO.getAllProducts());
        System.out.println("ProductManagerController: List size: " + productList.size());
        tableProducts.setItems(productList);
    }

    /**
     * Defines event handlers for UI actions including image selection,
     * adding, updating, and deleting products.
     */
    private void setupActions() {
        // Image Selection Logic
        btnChooseImage.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Product Image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                selectedImagePath = selectedFile.getAbsolutePath();
                btnChooseImage.setText("Selected: " + selectedFile.getName());
            }
        });

        // Add Product Logic
        btnAdd.setOnAction(event -> {
            try {
                String name = txtName.getText();
                String type = comboType.getValue();
                double price = Double.parseDouble(txtPrice.getText());
                double stock = Double.parseDouble(txtStock.getText());
                double threshold = Double.parseDouble(txtThreshold.getText());

                byte[] imageBytes = null;
                String mimeType = null;

                if (!selectedImagePath.isEmpty()) {
                    File imgFile = new File(selectedImagePath);
                    imageBytes = java.nio.file.Files.readAllBytes(imgFile.toPath());
                    mimeType = java.nio.file.Files.probeContentType(imgFile.toPath());

                    // Fallback MIME type identification if probe fails
                    if(mimeType == null) {
                        if(selectedImagePath.endsWith(".png")) mimeType = "image/png";
                        else if(selectedImagePath.endsWith(".jpg") || selectedImagePath.endsWith(".jpeg")) mimeType = "image/jpeg";
                    }
                }

                // ID set to 0 as database handles auto-increment
                Product newProduct = new Product(0, name, type, price, stock, threshold, imageBytes, mimeType);
                productDAO.addProduct(newProduct);
                loadData();
                clearForm();

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Selection listener to populate fields for editing
        tableProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtName.setText(newSelection.getName());
                txtPrice.setText(String.valueOf(newSelection.getPricePerKg()));
                txtStock.setText(String.valueOf(newSelection.getStockKg()));
                txtThreshold.setText(String.valueOf(newSelection.getThresholdKg()));
                comboType.setValue(newSelection.getType());
                selectedImagePath = "";
            }
        });

        // Update Product Logic
        btnUpdate.setOnAction(event -> {
            Product selected = tableProducts.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    selected.setName(txtName.getText());
                    selected.setType(comboType.getValue());
                    selected.setPricePerKg(Double.parseDouble(txtPrice.getText()));
                    selected.setStockKg(Double.parseDouble(txtStock.getText()));
                    selected.setThresholdKg(Double.parseDouble(txtThreshold.getText()));

                    if (!selectedImagePath.isEmpty()) {
                        File imgFile = new File(selectedImagePath);
                        byte[] imageBytes = java.nio.file.Files.readAllBytes(imgFile.toPath());
                        String mimeType = java.nio.file.Files.probeContentType(imgFile.toPath());

                        if(mimeType == null) {
                            if(selectedImagePath.endsWith(".png")) mimeType = "image/png";
                            else if(selectedImagePath.endsWith(".jpg") || selectedImagePath.endsWith(".jpeg")) mimeType = "image/jpeg";
                        }

                        selected.setImageContent(imageBytes);
                        selected.setMimeType(mimeType);
                        selected.setImagePath(null);
                    }

                    productDAO.updateProduct(selected);
                    loadData();
                    clearForm();
                    System.out.println("Product updated: " + selected.getName());
                } catch (Exception e) {
                    System.out.println("Update error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        // Delete Product Logic
        btnDelete.setOnAction(event -> {
            Product selected = tableProducts.getSelectionModel().getSelectedItem();
            if (selected != null) {
                productDAO.deleteProduct(selected.getId());
                loadData();
                clearForm();
                System.out.println("Product deleted: " + selected.getName());
            }
        });
    }

    /**
     * Clears all input fields in the product form and resets selection state.
     */
    private void clearForm() {
        txtName.clear();
        txtPrice.clear();
        txtStock.clear();
        txtThreshold.clear();
        comboType.clearSelection();
        selectedImagePath = "";
        btnChooseImage.setText("Choose Image");
    }
}