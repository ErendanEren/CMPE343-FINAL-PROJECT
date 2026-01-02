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
 * This module fulfills Member 4's responsibility for the System Messaging infrastructure.
 * Enables communication between different user roles within the application.
 *
 * @author Zafer Mert Serinken
 */
public class MessagingController {

    @FXML private ListView<String> messageListView;
    @FXML private TextField txtMessageInput;
    @FXML private Label lblContactName;

    private MessageDao messageDao = new MessageDao();
    private User currentUser;
    private int targetUserId;

    /**
     * Initializes the controller.
     * Retrieves the authenticated user from the system session and
     * fetches the target recipient's information passed from other modules.
     */
    @FXML
    public void initialize() {
        // Retrieve current authenticated user from session data
        this.currentUser = (User) SceneManager.getData("currentUser");

        // Retrieve target user information passed from Carrier or Customer modules
        Object target = SceneManager.getData("targetUserId");
        if (target != null) {
            this.targetUserId = (int) target;
            lblContactName.setText("Chatting with ID: " + targetUserId);
        }

        handleRefresh();
    }

    /**
     * Handles the message transmission process.
     * Validates input content and persists the message to the database via MessageDao.
     */
    @FXML
    private void handleSend() {
        String content = txtMessageInput.getText().trim();
        if (content.isEmpty() || currentUser == null) return;

        Message msg = new Message();
        msg.setSenderId(currentUser.getId());
        msg.setReceiverId(this.targetUserId);
        msg.setContent(content);

        // Persist the message record to the database
        if (messageDao.sendMessage(msg)) {
            txtMessageInput.clear();
            handleRefresh();
        }
    }

    /**
     * Synchronizes the UI message list with the latest database records.
     * Fetches messages in chronological order and applies visual labels
     * based on the sender's identity.
     */
    @FXML
    private void handleRefresh() {
        if (currentUser == null) return;

        // Fetch chronological messages for the current user from the database
        List<Message> messages = messageDao.getMessagesForUser(currentUser.getId());
        messageListView.getItems().clear();

        for (Message m : messages) {
            // Apply visual labels based on the message direction
            String senderLabel = (m.getSenderId() == currentUser.getId()) ? "[YOU]: " : "[INCOMING]: ";
            messageListView.getItems().add(senderLabel + m.getContent());
        }
    }
}