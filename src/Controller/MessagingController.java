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
 * Controller class for managing the shared messaging system.
 * Responsible for the system's messaging infrastructure and communication handling.
 *
 * @author Arda Dülger
 */
public class MessagingController {

    @FXML private ListView<String> messageListView;
    @FXML private TextField txtMessageInput;
    @FXML private Label lblContactName;

    private MessageDao messageDao = new MessageDao();
    private User currentUser;
    private int targetUserId;

    /**
     * Initializes the messaging controller. Retrieves the logged-in user
     * and target contact information from the SceneManager.
     *
     * @author Arda Dülger
     */
    @FXML
    public void initialize() {
        this.currentUser = (User) SceneManager.getData("currentUser");

        Object target = SceneManager.getData("targetUserId");
        if (target != null) {
            this.targetUserId = (int) target;
            lblContactName.setText("Sohbet Edilen ID: " + targetUserId);
        }

        handleRefresh();
    }

    /**
     * Handles the message sending process. Validates input content,
     * creates a Message object, and persists it via MessageDao.
     *
     * @author Arda Dülger
     */
    @FXML
    private void handleSend() {
        String content = txtMessageInput.getText().trim();
        if (content.isEmpty() || currentUser == null) return;

        Message msg = new Message();
        msg.setSenderId(currentUser.getId());
        msg.setReceiverId(this.targetUserId);
        msg.setContent(content);

        if (messageDao.sendMessage(msg)) {
            txtMessageInput.clear();
            handleRefresh();
        }
    }

    /**
     * Refreshes the message list view by fetching chronological messages
     * from the database and applying directional visual labels.
     *
     * @author Arda Dülger
     */
    @FXML
    private void handleRefresh() {
        if (currentUser == null) return;

        List<Message> messages = messageDao.getMessagesForUser(currentUser.getId());
        messageListView.getItems().clear();

        for (Message m : messages) {
            String senderLabel = (m.getSenderId() == currentUser.getId()) ? "[SİZ]: " : "[GELEN]: ";
            messageListView.getItems().add(senderLabel + m.getContent());
        }
    }
}