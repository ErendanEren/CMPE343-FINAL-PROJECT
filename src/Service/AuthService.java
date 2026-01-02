package Service;

import Models.User;
import Database.DatabaseConnection;

public class AuthService {

    private static AuthService instance;
    private User currentUser;

    private AuthService() {}

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User login(String username, String password) {
        // Use DatabaseConnection for actual DB logic
        User user = DatabaseConnection.login(username, password);
        if (user != null) {
            this.currentUser = user;
        }
        return user;
    }

    public boolean register(String username, String password, String fullName, String phone, String email, String address) {
        // Basic check if user exists
        // Ideally we check DBUserDAO here or reuse DatabaseConnection if needed.
        // For simplicity reusing DAO approach or adding direct logic.
        // Let's rely on DBUserDAO to add user.

        try {
            Models.User newUser = new Models.User(username, password, fullName, phone, email, address, "CUSTOMER");
            Dao.DBUserDAO userDAO = new Dao.DBUserDAO();
            userDAO.addUser(newUser);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
