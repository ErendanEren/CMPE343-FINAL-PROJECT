package Controller;

import Dao.MockProductDAO;
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

    // Veri erişim nesnemiz (Şimdilik Mock, Eren DB atınca değişecek)
    private ProductDAO productDAO = new MockProductDAO();
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
        productList = FXCollections.observableArrayList(productDAO.getAllProducts());
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
        btnAdd.setOnAction(event -> {
            try {
                String name = txtName.getText();
                String type = comboType.getValue();
                double price = Double.parseDouble(txtPrice.getText());
                double stock = Double.parseDouble(txtStock.getText());
                double threshold = Double.parseDouble(txtThreshold.getText());

                Product newProduct = new Product(name, type, price, stock, threshold, selectedImagePath);
                productDAO.addProduct(newProduct);
                loadData();
                clearForm();

            } catch (Exception e) {
                System.out.println("Hata: " + e.getMessage());
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
                selectedImagePath = newSelection.getImagePath();
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