package Dao;

import Models.Message;
import Database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {

    /**
     * Yeni bir mesajı veritabanındaki 'Message' tablosuna kaydeder.
     */
    public boolean sendMessage(Message message) {
        // SQL'deki tablo ismi ve sütunlarla birebir aynı: Message (sender_id, receiver_id, content)
        String sql = "INSERT INTO Message (sender_id, receiver_id, content) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, message.getSenderId());
            ps.setInt(2, message.getReceiverId());
            ps.setString(3, message.getContent());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Mesaj gönderme hatası: " + e.getMessage());
            return false;
        }
    }

    /**
     * Belirli bir kullanıcının dahil olduğu tüm mesajları getirir.
     */
    public List<Message> getMessagesForUser(int userId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM Message WHERE sender_id = ? OR receiver_id = ? ORDER BY sent_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message msg = new Message();
        msg.setId(rs.getInt("id"));
        msg.setSenderId(rs.getInt("sender_id"));
        msg.setReceiverId(rs.getInt("receiver_id"));
        msg.setContent(rs.getString("content"));

        Timestamp ts = rs.getTimestamp("sent_at");
        if (ts != null) {
            msg.setSentAt(ts.toLocalDateTime());
        }
        return msg;
    }
}