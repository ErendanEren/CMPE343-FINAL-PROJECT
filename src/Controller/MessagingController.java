package Controller;

import App.SceneManager;
import Dao.MessageDao;
import Models.Message;
import Models.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

/**
 * Ortak mesajlaşma sistemini yöneten sınıf.
 * Member 4 sorumluluğu: Sistem Mesajlaşması altyapısı.
 */
public class MessagingController {

    @FXML private ListView<String> messageListView;
    @FXML private TextField txtMessageInput;
    @FXML private Label lblContactName;

    private MessageDao messageDao = new MessageDao();
    private User currentUser;
    private int targetUserId; // Mesajlaşılan kişinin ID'si

    @FXML
    public void initialize() {
        // Altyapı: Giriş yapan kullanıcıyı sistemden çek [cite: 95]
        this.currentUser = (User) SceneManager.getData("currentUser");

        // Diğer modüllerden (Kurye/Müşteri) gelen hedef kullanıcı bilgisini al
        Object target = SceneManager.getData("targetUserId");
        if (target != null) {
            this.targetUserId = (int) target;
            lblContactName.setText("Sohbet Edilen ID: " + targetUserId);
        }

        handleRefresh();
    }

    @FXML
    private void handleSend() {
        String content = txtMessageInput.getText().trim();
        if (content.isEmpty() || currentUser == null) return;

        Message msg = new Message();
        msg.setSenderId(currentUser.getId());
        msg.setReceiverId(this.targetUserId);
        msg.setContent(content);

        // Altyapı: Mesajı veritabanına kaydet [cite: 63]
        if (messageDao.sendMessage(msg)) {
            txtMessageInput.clear();
            handleRefresh();
        }
    }

    @FXML
    private void handleRefresh() {
        if (currentUser == null) return;

        // Veritabanındaki mesajları kronolojik olarak çek
        List<Message> messages = messageDao.getMessagesForUser(currentUser.getId());
        messageListView.getItems().clear();

        for (Message m : messages) {
            // Mesajın yönüne göre görsel etiket ekle
            String senderLabel = (m.getSenderId() == currentUser.getId()) ? "[SİZ]: " : "[GELEN]: ";
            messageListView.getItems().add(senderLabel + m.getContent());
        }
    }
}