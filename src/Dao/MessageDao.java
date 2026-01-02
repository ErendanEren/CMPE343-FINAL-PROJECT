package Dao;

import Models.Message;
import Database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {

    /**
     * Mesaj gönderir.
     * DB Şeması: id, customer_id, owner_id, sender_role, body, created_at
     */
    public boolean sendMessage(Message msg) {
        String sql = "INSERT INTO group09_greengrocer.message (customer_id, owner_id, sender_role, body, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Message modelinde senderId ve receiverId var.
            // Eğer gönderen Carrier ise:
            // owner_id = senderId (Carrier/Staff)
            // customer_id = receiverId (Customer)
            // sender_role = 'CARRIER' veya 'OWNER'

            // Eğer gönderen Customer ise:
            // customer_id = senderId
            // owner_id = receiverId (Staff/Carrier ID, şimdilik sabit veya parametrik olabilir, ama user profilinden gelince receiverId=Carrier olacak mı? )

            // Basitleştirme:
            // Message modelindeki senderId ve receiverId alanlarını bağlama göre kullanacağız.
            // Fakat 'sender_role'ü nereden bileceğiz?
            // HACK: Şimdilik senderId, currentUser.getId() olduğu için, Role bilgisini Message modeline eklesek iyi olurdu.
            // Ancak mevcut Message modelini bozmadan şöyle yapalım:
            // CarrierController'da msg.setSenderId(carrier) ve msg.setReceiverId(customer) set ediliyor.
            // ProfileController'da (Customer) msg.setSenderId(customer) ve msg.setReceiverId(?? kime gidiyor? 1 nolu admin? veya siparişin kuryesi?)

            // Varsayım: İletişim Customer <-> Staff(Carrier/Owner) arasında.
            // CarrierController.java'yı incelediğimizde: msg.setSenderId(currentUser.getId()) [Carrier], msg.setReceiverId(selected.getCustomerId())

            // Bu durumda:
            int customerId;
            int ownerId; // Staff/Carrier ID
            String role;

            // Ancak Message objesinde role yok.
            // Şunu yapabiliriz: Eğer senderId == receiverId ise mantıksız olur.
            // DAO içinde role'ü tahmin edemeyiz. En iyisi Message modeline 'senderRole' alanı eklemek (veya parametre geçmek).

            // Hızlı çözüm için: Message modelini güncellememek adına,
            // CarrierController bu metodu çağırırken "CARRIER" olduğunu belirtmeli.
            // Ama metod imzası (Message msg).

            // O zaman şöyle yapalım:
            // MessageDao, 'msg.getContent()' (body) içine bakarak bir şey yapamaz.
            // Biz CarrierController ve ProfileController'da mantığı biliyoruz.
            // En temizi: Message modeline `senderRole` eklemek.
            // Ama User modelinde ve DB'de zaten Role maplememiz var.

            // Varsayım: Customer ID'leri ile Staff ID'lerinin çakışma ihtimali var mı? User tablosu ortak (id auto increment).
            // O zaman ID'den user'ı bulup rolüne bakabiliriz.

            String senderRole = getUserRole(msg.getSenderId(), conn);

            if ("CUSTOMER".equalsIgnoreCase(senderRole)) {
                customerId = msg.getSenderId();
                ownerId = msg.getReceiverId(); // Customer kime atıyor?
                role = "CUSTOMER";
            } else {
                // Gönderen STAFF, CARRIER veya OWNER
                customerId = msg.getReceiverId(); // Alıcı Customer
                ownerId = msg.getSenderId();     // Gönderen Staff
                // ENUM CONSTRIANT: sender_role only accepts 'CUSTOMER' or 'OWNER'
                // CARRIER is not in Enum, so we map it to OWNER (representing Staff side)
                role = "OWNER";
            }

            ps.setInt(1, customerId);
            ps.setInt(2, ownerId);
            ps.setString(3, role);
            ps.setString(4, msg.getContent());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Yardımcı method: ID'den rol bulma
    private String getUserRole(int userId, Connection conn) {
        String query = "SELECT role FROM group09_greengrocer.user_info WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "UNKNOWN";
    }

    public List<Message> getMessagesForUser(int userId) {
        List<Message> messages = new ArrayList<>();
        // Burada kullanıcının tüm mesajlarını getireceğiz.
        // Kullanıcı Customer ise customer_id = userId
        // Kullanıcı Staff ise owner_id = userId

        String sql = "SELECT * FROM group09_greengrocer.message WHERE customer_id = ? OR owner_id = ? ORDER BY created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message();
                    msg.setId(rs.getInt("id"));

                    int cid = rs.getInt("customer_id"); // 2
                    int oid = rs.getInt("owner_id");    // 4
                    String senderRole = rs.getString("sender_role"); // "CARRIER"

                    // Modeli doldururken Sender ve Receiver'ı role'e göre atamalıyız.
                    if ("CUSTOMER".equalsIgnoreCase(senderRole)) {
                        msg.setSenderId(cid);
                        msg.setReceiverId(oid);
                    } else {
                        // Gönderen Staff/Carrier
                        msg.setSenderId(oid);
                        msg.setReceiverId(cid);
                    }

                    msg.setContent(rs.getString("body"));

                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        msg.setSentAt(ts.toLocalDateTime());
                    }

                    messages.add(msg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}