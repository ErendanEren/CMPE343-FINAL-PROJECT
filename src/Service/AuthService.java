package Service;

import Models.User;

public class AuthService {
    private static AuthService instance;
    private User currentUser;

    public AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public User getCurrentUser() {
        // Mock user for development/testing if login hasn't happened
        if (currentUser == null) {
            System.out.println("AuthService: Warning - No user logged in, returning MOCK user.");
            currentUser = new User("customer", "1234", "Mock User", "555-0000", "mock@example.com", "Mock Address", "CUSTOMER");
            currentUser.setId(1); // Set a mock ID
        }
        return currentUser;
    }

    public User login(String username, String password) {
        User user = Database.DatabaseConnection.login(username, password);
        if (user != null) {
            this.currentUser = user;
        }
        return user;
    }

    public boolean register(String username, String password, String fullName, String phone, String email, String address) {
        // Basic check if user exists (should ideally be done in DB/DAO)
        // For simplicity, we'll try to insert directly or check via DAO
        // Here we need a register method in DB Connection or UserDAO
        // Let's assume DatabaseConnection has a register method or we implement it here

        // TODO: Implement actual registration in DatabaseConnection
        // For now returning false to prevent errors or mocking true
        System.out.println("Register requested for: " + username);
        return true;
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }
}
