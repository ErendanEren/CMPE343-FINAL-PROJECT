package Controller;

import Service.AuthService;
import Utils.SceneManager;
import Dao.CarrierDAO;
import Dao.MessageDao;
import Models.CarrierRating;
import Models.Message;
import Models.Order;
import Models.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class CarrierController {


    @FXML private TableView<Order> availableTable;
    @FXML private TableColumn<Order, Integer> colId;
    @FXML private TableColumn<Order, String> colAddress;
    @FXML private TableColumn<Order, Double> colAmount;

    // 2. SEKME: Active Orders (Kuryenin Üzerindeki Siparişler)
    @FXML private TableView<Order> activeTable;
    @FXML private TableColumn<Order, Integer> colActiveId;
    @FXML private TableColumn<Order, String> colActiveAddress;

    // 3. SEKME: Completed Orders (Tamamlananlar)
    @FXML private TableView<Order> completedTable;
    @FXML private TableColumn<Order, Integer> colCompId;
    @FXML private TableColumn<Order, String> colCompAddress;
    @FXML private TableColumn<Order, Double> colCompAmount;
    // Yeni eklenen gerçek teslimat tarihi sütunu
    @FXML private TableColumn<Order, LocalDateTime> colCompDeliveredAt;

    // 4. SEKME: Puanlar ve Performans
    @FXML private ListView<String> ratingListView;
    @FXML private Label lblAverageRating;

    private CarrierDAO carrierDAO = new CarrierDAO();
    private MessageDao messageDao = new MessageDao();
    private User currentUser;

    /**
     * Ekran yüklendiğinde otomatik çalışan metot.
     */
    @FXML
    public void initialize() {
        this.currentUser = (User) SceneManager.getData("currentUser");

        setupTableColumns();
        refreshTables();
        loadRatings();

        // 3 saniyede bir tabloyu otomatik yenile (Anlık Takip)
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), event -> {
                    refreshTables();
                })
        );
        timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timeline.play();
    }

    private void setupTableColumns() {
        // Available Table
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddressSnapshot"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        // Active Table
        colActiveId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colActiveAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddressSnapshot"));

        // Completed Table
        colCompId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCompAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddressSnapshot"));
        colCompAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        // delivered_at verisini Order modelindeki deliveredAt alanı ile eşleştiriyoruz
        colCompDeliveredAt.setCellValueFactory(new PropertyValueFactory<>("deliveredAt"));
    }

    /**
     * Veritabanındaki güncel siparişleri uygun sekmelere dağıtır.
     */
    private void refreshTables() {
        availableTable.setItems(FXCollections.observableArrayList(carrierDAO.getAvailableOrders()));

        if (currentUser != null) {
            activeTable.setItems(FXCollections.observableArrayList(
                    carrierDAO.getOrdersByCarrier(currentUser.getId(), "ASSIGNED")
            ));
            completedTable.setItems(FXCollections.observableArrayList(
                    carrierDAO.getOrdersByCarrier(currentUser.getId(), "DELIVERED")
            ));
        }
    }

    /**
     * Bir siparişi kuryenin üzerine zimmetler.
     */
    @FXML
    private void handlePickUp() {
        Order selected = availableTable.getSelectionModel().getSelectedItem();
        if (selected != null && currentUser != null) {
            boolean success = carrierDAO.assignOrderToCarrier(selected.getId(), currentUser.getId());
            if (success) {
                refreshTables();
                showAlert("Success", "Order successfully assigned to you.");
            } else {
                refreshTables();
                showAlert("Conflict Error", "This order was just picked up by another carrier!");
            }
        } else if (selected == null) {
            showAlert("Selection Error", "Please select an order to pick up.");
        }
    }

    /**
     * Siparişi teslim edildi olarak işaretler ve delivered_at zamanını kaydeder.
     */
    @FXML
    private void handleDeliver() {
        Order selected = activeTable.getSelectionModel().getSelectedItem();
        if (selected != null && currentUser != null) {
            // DAO içindeki completeOrder artık veritabanındaki delivered_at sütununu güncelliyor
            boolean success = carrierDAO.completeOrder(selected.getId());
            if (success) {
                refreshTables();
                showAlert("Success", "Order marked as delivered.");
            }
        } else if (selected == null) {
            showAlert("Selection Error", "Please select an active order to mark as delivered.");
        }
    }

    /**
     * Müşteriye anlık mesaj gönderir.
     */
    @FXML
    private void handleSendMessage() {
        Order selected = activeTable.getSelectionModel().getSelectedItem();

        if (selected != null && currentUser != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Messenger");
            dialog.setHeaderText("Contact Customer for Order #" + selected.getId());
            dialog.setContentText("Enter your message:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(content -> {
                if (!content.trim().isEmpty()) {
                    Message msg = new Message();
                    msg.setSenderId(currentUser.getId());
                    msg.setReceiverId(selected.getCustomerId());
                    msg.setContent(content);

                    if (messageDao.sendMessage(msg)) {
                        showAlert("Sent", "Message sent to customer.");
                    } else {
                        showAlert("Error", "Could not send message.");
                    }
                }
            });
        } else {
            showAlert("Selection Error", "Please select an order to contact the customer.");
        }
    }

    /**
     * Kuryenin performans verilerini ve müşteri değerlendirmelerini yükler.
     */
    private void loadRatings() {
        if (currentUser != null && ratingListView != null) {
            List<CarrierRating> ratings = carrierDAO.getRatingsForCarrier(currentUser.getId());
            double sum = 0;
            ratingListView.getItems().clear();

            for (CarrierRating r : ratings) {
                sum += r.getRating();
                ratingListView.getItems().add("⭐ " + r.getRating() + "/5 | " + r.getComment());
            }

            if (!ratings.isEmpty() && lblAverageRating != null) {
                double avg = sum / ratings.size();
                lblAverageRating.setText(String.format("Performance: %.1f / 5.0", avg));
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void handleLogout() {
        AuthService.getInstance().logout();
        SceneManager.switchSceneStatic("/fxml/Login.fxml");
    }
}