package Database;

import Models.User;
import java.security.MessageDigest;
import java.sql.*;

/**
 * Central database utility class responsible for managing the JDBC connection
 * and handling user authentication logic.
 */
public class DatabaseConnection {

    private static final String DATABASE_NAME = "group09_greengrocer";
    private static final String DATABASE_USER = "myuser";
    private static final String DATABASE_PASSWORD = "1234";

    private static final String CONNECTION_STRING =
            "jdbc:mysql://localhost:3306/" + DATABASE_NAME +
                    "?useSSL=false&allowPublicKeyRetrieval=true" +
                    "&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    CONNECTION_STRING,
                    DATABASE_USER,
                    DATABASE_PASSWORD
            );
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public static User login(String username, String password) {
        String sql = "SELECT * FROM group09_greengrocer.user_info WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                String storedPassword = rs.getString("password_hash");
                String hashedInput = hashPassword(password);

                // Hybrid check: plain text (legacy/initial) or hashed
                boolean isMatch = storedPassword.equals(password) || storedPassword.equals(hashedInput);

                if (!isMatch) {
                    return null;
                }

                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(storedPassword); // Store the hash
                user.setFullName(rs.getString("full_name"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address_line"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                return user;
            }

        } catch (SQLException e) {
            System.err.println("Database login error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String hashPassword(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plainText.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed inside DB Connection", e);
        }
    }
}
