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
 * Manages the flow between the UI and the data model.
 *
 * @author Zafer Mert Serinken
 */
public class CustomerController implements Initializable {

    // These IDs must match fx:id in SceneBuilder
    @FXML private TextField searchField;
    @FXML private FlowPane veggieContainer; // Place inside the Vegetable TitledPane
    @FXML private FlowPane fruitContainer;  // Place inside the Fruit TitledPane
    @FXML private Label welcomeLabel;

    // In-memory list to hold products until DB is connected
    private ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private ProductDAO productDAO = new DBProductDAO();

    /**
     * Initializes the controller class.
     * Automatically called after the fxml file has been loaded.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Load data (DB)
        loadData();

        // 2. Display initial data sorted by name
        refreshProductDisplays("");

        // 3. Add listener for search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshProductDisplays(newValue);
        });

        // Show user info (mockup)
        welcomeLabel.setText("Welcome, Zafer");
    }

    /**
     * Filters the product list based on the search query and refreshes the UI.
     * Also applies sorting by name.
     *
     * @param query The search keyword entered by the user.
     */
    private void refreshProductDisplays(String query) {
        // Clear current views
        veggieContainer.getChildren().clear();
        fruitContainer.getChildren().clear();

        String lowerCaseQuery = query.toLowerCase();

        // Stream API for Filtering & Sorting
        List<Product> filteredList = allProducts.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerCaseQuery)) // Search Filter
                .sorted(Comparator.comparing(Product::getName)) // Sort by Name [Requirement]
                .collect(Collectors.toList());

        // Distribute to containers
        for (Product p : filteredList) {
            try {
                // Determine which container to add to
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
     * Dynamically loads a ProductCard FXML and populates it with product data.
     *
     * @param product The product object to display.
     * @return The Node (UI element) representing the product card.
     * @throws IOException If FXML loading fails.
     */
    private Node createProductCard(Product product) throws IOException {
        // NOTE: We will create ProductCard.fxml and its controller in the next step.
        // This is how we load reusable components in JavaFX.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/ProductCard.fxml"));
        Node cardNode = loader.load();

        // Get the controller of the card to pass data
        ProductCardController cardController = loader.getController();
        cardController.setProductData(product);

        return cardNode;
    }

    /**
     * Loads products from the database.
     */
    private void loadData() {
        allProducts.setAll(productDAO.getAllProducts());
    }

    /**
     * Opens the Shopping Cart in a new window (Stage).
     * Linked to the "My Cart" button in the main UI.
     */
    @FXML
    private void handleShowCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/CartView.fxml"));

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
}