package Controller;

import Models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import Dao.DBProductDAO;
import Dao.ProductDAO;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controls the main customer interface including product listing, searching, and sorting.
 * Manages the data flow between the UI components and the database layer.
 *
 * @author Zafer Mert Serinken
 */
public class CustomerController implements Initializable {

    @FXML private TextField searchField;
    @FXML private FlowPane veggieContainer;
    @FXML private FlowPane fruitContainer;
    @FXML private Label welcomeLabel;

    private ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private ProductDAO productDAO = new DBProductDAO();

    /**
     * Initializes the controller class.
     * Fetches product data from the database, sets up initial displays,
     * and attaches listeners for search functionality.
     *
     * @param location  The location used to resolve relative paths for the root object.
     * @param resources The resources used to localize the root object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load product data from the database
        loadData();

        // Initial display of data sorted alphabetically by name
        refreshProductDisplays("");

        // Register a listener for real-time search filtering
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshProductDisplays(newValue);
        });

        // Set personalized welcome message for the current user
        Models.User currentUser = Service.AuthService.getInstance().getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getFullName());
        } else {
            welcomeLabel.setText("Welcome, Guest");
        }
    }

    /**
     * Filters the product list based on the search query, sorts the results by name,
     * and updates the UI containers.
     *
     * @param query The search keyword entered by the user.
     */
    private void refreshProductDisplays(String query) {
        veggieContainer.getChildren().clear();
        fruitContainer.getChildren().clear();

        String lowerCaseQuery = query.toLowerCase();

        // Use Stream API for filtering and sorting requirements
        List<Product> filteredList = allProducts.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerCaseQuery))
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toList());

        // Populate category containers dynamically
        for (Product p : filteredList) {
            try {
                if ("VEGETABLE".equalsIgnoreCase(p.getType())) {
                    veggieContainer.getChildren().add(createProductCard(p));
                } else if ("FRUIT".equalsIgnoreCase(p.getType())) {
                    fruitContainer.getChildren().add(createProductCard(p));
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error loading product card for: " + p.getName());
            }
        }
    }

    /**
     * Dynamically loads a ProductCard FXML component and populates it with product details.
     *
     * @param product   The product model to be displayed on the card.
     * @return          A Node representing the visual product card.
     * @throws IOException If the FXML file cannot be loaded.
     */
    private Node createProductCard(Product product) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProductCard.fxml"));
        Node cardNode = loader.load();

        ProductCardController cardController = loader.getController();
        cardController.setProductData(product);

        return cardNode;
    }

    /**
     * Synchronizes the in-memory observable list with the product records in the database.
     */
    private void loadData() {
        allProducts.setAll(productDAO.getAllProducts());
    }

    /**
     * Opens the Shopping Cart interface in a new modal window.
     */
    @FXML
    private void handleShowCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CartView.fxml"));

            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();

            stage.setTitle("My Shopping Cart");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not open Cart View. Check file path!");
        }
    }

    /**
     * Opens the User Profile management interface in a new modal window.
     */
    @FXML
    private void handleShowProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Profile.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("My Profile");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not open Profile View.");
        }
    }

    /**
     * Logs the current user out of the application and redirects to the login screen.
     */
    @FXML
    private void handleLogout() {
        Service.AuthService.getInstance().logout();
        Utils.SceneManager.switchSceneStatic("/fxml/Login.fxml");
    }
}