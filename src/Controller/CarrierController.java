package Controller;

import App.SceneManager;
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
import java.util.List;
import java.util.Optional;

/**
 * Kurye panelindeki tüm kullanıcı etkileşimlerini yöneten Controller sınıfı.
 * Member 4 sorumlulukları olan sipariş takibi, mesajlaşma ve puanlama sistemini yönetir[cite: 49, 50].
 */
public class CarrierController {

    // 1. SEKME: Available Orders (Boştaki Siparişler) [cite: 52, 121]
    @FXML private TableView<Order> availableTable;
    @FXML private TableColumn<Order, Integer> colId;
    @FXML private TableColumn<Order, String> colAddress;
    @FXML private TableColumn<Order, Double> colAmount;

    // 2. SEKME: Active Orders (Kuryenin Üzerindeki Siparişler) [cite: 52, 121]
    @FXML private TableView<Order> activeTable;
    @FXML private TableColumn<Order, Integer> colActiveId;
    @FXML private TableColumn<Order, String> colActiveAddress;

    // 3. SEKME: Completed Orders (Tamamlananlar) [cite: 52, 121]
    @FXML private TableView<Order> completedTable;
    @FXML private TableColumn<Order, Integer> colCompId;
    @FXML private TableColumn<Order, String> colCompAddress;
    @FXML private TableColumn<Order, Double> colCompAmount;

    // 4. SEKME: Puanlar ve Performans [cite: 60, 130]
    @FXML private ListView<String> ratingListView;
    @FXML private Label lblAverageRating;

    private CarrierDAO carrierDAO = new CarrierDAO();
    private MessageDao messageDao = new MessageDao();
    private User currentUser;

    /**
     * Ekran yüklendiğinde otomatik çalışan metot.
     * Kullanıcı verilerini alır ve tablo sütunlarını eşleştirir[cite: 95, 101].
     */
    @FXML
    public void initialize() {
        // SceneManager üzerinden giriş yapan kurye bilgisini alıyoruz [cite: 95]
        this.currentUser = (User) SceneManager.getData("currentUser");

        // Tablo Sütun Yapılandırması
        setupTableColumns();

        // Başlangıç Verilerini Yükleme
        refreshTables();
        loadRatings();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddressSnapshot"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        colActiveId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colActiveAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddressSnapshot"));

        colCompId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCompAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddressSnapshot"));
        colCompAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
    }

    /**
     * Veritabanındaki güncel siparişleri uygun sekmelere dağıtır[cite: 121, 150].
     */
    private void refreshTables() {
        // Boştaki siparişleri listeler (Available) [cite: 122]
        availableTable.setItems(FXCollections.observableArrayList(carrierDAO.getAvailableOrders()));

        if (currentUser != null) {
            // Kuryenin üzerine aldığı siparişler (Active) [cite: 121, 123]
            activeTable.setItems(FXCollections.observableArrayList(
                    carrierDAO.getOrdersByCarrier(currentUser.getId(), "ASSIGNED")
            ));
            // Kuryenin tamamladığı teslimatlar (Completed) [cite: 121]
            completedTable.setItems(FXCollections.observableArrayList(
                    carrierDAO.getOrdersByCarrier(currentUser.getId(), "DELIVERED")
            ));
        }
    }

    /**
     * Bir siparişi kuryenin üzerine zimmetler.
     * Hata Yönetimi: Sipariş başka bir kurye tarafından az önce alındıysa uyarı verir.
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
                // Kritik mantıksal hata kontrolü: Sipariş artık boşta değil [cite: 158]
                refreshTables();
                showAlert("Conflict Error", "This order was just picked up by another carrier!");
            }
        } else if (selected == null) {
            showAlert("Selection Error", "Please select an order to pick up.");
        }
    }

    /**
     * Siparişi teslim edildi olarak işaretler ve veritabanını günceller[cite: 58, 123].
     */
    @FXML
    private void handleDeliver() {
        Order selected = activeTable.getSelectionModel().getSelectedItem();
        if (selected != null && currentUser != null) {
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
     * Müşteriye anlık mesaj gönderir[cite: 53, 120].
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
     * Kuryenin performans verilerini ve müşteri değerlendirmelerini yükler[cite: 60, 130].
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
}