package Utils;

import Database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class CheckLoyaltySettings {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM group09_greengrocer.owner_settings LIMIT 1");
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            System.out.println("Cols:");
            for (int i = 1; i <= colCount; i++) {
                System.out.println(meta.getColumnName(i) + " (" + meta.getColumnTypeName(i) + ")");
            }

            if (rs.next()) {
                System.out.println("Data:");
                for (int i = 1; i <= colCount; i++) {
                    System.out.println(meta.getColumnName(i) + ": " + rs.getObject(i));
                }
            } else {
                System.out.println("Table is empty.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
