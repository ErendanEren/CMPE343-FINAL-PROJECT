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
        try {
            // Check if user exists? (DB constraint will throw error if duplicate username)
            Dao.UserDAO userDAO = new Dao.DBUserDAO();
            User newUser = new User(username, password, fullName, phone, email, address, "CUSTOMER");

            userDAO.addUser(newUser);
            System.out.println("AuthService: Register successful for " + username);
            return true;
        } catch (Exception e) {
            System.err.println("AuthService: Register failed -> " + e.getMessage());
            return false;
        }
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }
}
