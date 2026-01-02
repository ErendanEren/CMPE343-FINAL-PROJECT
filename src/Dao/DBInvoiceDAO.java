package Dao;

import Database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DBInvoiceDAO implements InvoiceDAO {

    @Override
    public void saveInvoice(int orderId, String invoiceText) {
        // Save to BOTH invoice_text and pdf_blob to ensure visibility and meet blob requirement
        String sql = "INSERT INTO group09_greengrocer.invoice (order_id, invoice_text, pdf_blob, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setCharacterStream(2, new java.io.StringReader(invoiceText), invoiceText.length());
            ps.setBytes(3, invoiceText.getBytes()); // Save as BLOB too
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
            System.out.println("DB: Invoice saved (Text & BLOB) for Order ID: " + orderId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
