package Dao;

import Database.DatabaseConnection;
import Models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUserDAO implements UserDAO {

    @Override
    public List<User> getAllCarriers() {
        List<User> carriers = new ArrayList<>();
        String sql = "SELECT * FROM UserInfo WHERE role = 'CARRIER'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User u = mapResultSetToUser(rs);
                carriers.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carriers;
    }

    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO UserInfo (username, password_hash, role, full_name, phone, email, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, DatabaseConnection.hashPassword(user.getPasswordHash())); // Hash before saving
            ps.setString(3, user.getRole());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getAddress());

            ps.executeUpdate();
            System.out.println("DB: User added -> " + user.getUsername());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addCarrier(User user) {
        user.setRole("CARRIER");
        addUser(user);
    }

    @Override
    public void deleteUser(String username) {
        String sql = "DELETE FROM UserInfo WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();
            System.out.println("DB: User deleted -> " + username);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUser(User user) {
        // ID is safer but username is unique, and prompt uses username.
        String sql = "UPDATE UserInfo SET full_name=?, phone=?, email=?, address=? WHERE username=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getAddress());
            ps.setString(5, user.getUsername());

            ps.executeUpdate();
            System.out.println("DB: User updated -> " + user.getUsername());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setEmail(rs.getString("email"));
        user.setAddress(rs.getString("address"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}
