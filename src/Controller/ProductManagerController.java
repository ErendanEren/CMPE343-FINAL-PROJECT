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

    // Veri erişim nesnemiz (DB)
    private ProductDAO productDAO = new DBProductDAO();
    private ObservableList<Product> productList;
    private String selectedImagePath = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
        setupActions();

        // ComboBox doldur
        comboType.setItems(FXCollections.observableArrayList("FRUIT", "VEGETABLE"));
    }

    private void setupTable() {
        // Product.java içindeki getter isimleriyle eşleşmeli
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerKg"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockKg"));
        colThreshold.setCellValueFactory(new PropertyValueFactory<>("thresholdKg"));
    }

    private void loadData() {
        System.out.println("ProductManagerController: Loading data...");
        productList = FXCollections.observableArrayList(productDAO.getAllProducts());
        System.out.println("ProductManagerController: List size: " + productList.size());
        tableProducts.setItems(productList);
    }

    private void setupActions() {
        // Resim Seçme
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

        // Ekleme
        // Ekleme
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
                    // Fallback if probe fails
                    if(mimeType == null) {
                        if(selectedImagePath.endsWith(".png")) mimeType = "image/png";
                        else if(selectedImagePath.endsWith(".jpg") || selectedImagePath.endsWith(".jpeg")) mimeType = "image/jpeg";
                    }
                }

                // ID is 0 for new products, DB will auto-increment
                Product newProduct = new Product(0, name, type, price, stock, threshold, imageBytes, mimeType);
                productDAO.addProduct(newProduct);
                loadData();
                clearForm();

            } catch (Exception e) {
                System.out.println("Hata: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Tablodan seçim
        tableProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtName.setText(newSelection.getName());
                txtPrice.setText(String.valueOf(newSelection.getPricePerKg()));
                txtStock.setText(String.valueOf(newSelection.getStockKg()));
                txtThreshold.setText(String.valueOf(newSelection.getThresholdKg()));
                comboType.setValue(newSelection.getType());
                selectedImagePath = ""; // Reset path on selection, unless re-selected
            }
        });

        // Güncelleme
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
                        selected.setImagePath(null); // Clear path as we use blob
                    }

                    productDAO.updateProduct(selected);
                    loadData();
                    clearForm();
                    System.out.println("Ürün güncellendi: " + selected.getName());
                } catch (Exception e) {
                    System.out.println("Güncelleme hatası: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        // Silme
        btnDelete.setOnAction(event -> {
            Product selected = tableProducts.getSelectionModel().getSelectedItem();
            if (selected != null) {
                productDAO.deleteProduct(selected.getId());
                loadData();
                clearForm();
                System.out.println("Ürün silindi: " + selected.getName());
            }
        });
    }

    private void clearForm() {
        txtName.clear();
        txtPrice.clear();
        txtStock.clear();
        txtThreshold.clear();
        comboType.clearSelection();
        selectedImagePath = "";
        btnChooseImage.setText("Resim Seç");
    }
}