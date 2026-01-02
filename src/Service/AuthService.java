package Service;

import Models.User;
import Database.DatabaseConnection;

/**
 * Singleton service class that manages user authentication and session state.
 * It provides methods for logging in, registering new users, and tracking the current session.
 * author selcukaloba and erencakirbircan
 */
public class AuthService {

    /**
     * The single instance of the AuthService.
     */
    private static AuthService instance;

    /**
     * The currently authenticated user in the system.
     */
    private User currentUser;

    /**
     * Private constructor to prevent direct instantiation (Singleton pattern).
     */
    private AuthService() {}

    /**
     * Returns the global instance of the AuthService.
     * Uses synchronized access to ensure thread safety during initialization.
     *
     * @return The singleton instance of {@link AuthService}
     */
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Retrieves the user who is currently logged into the application.
     *
     * @return The current {@link User} object, or null if no user is logged in.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Explicitly sets the current user session.
     *
     * @param currentUser The {@link User} object to set as the current user
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Terminates the current user session by clearing the stored user reference.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Authenticates a user through the database and starts a session if successful.
     *
     * @param username The username provided for authentication
     * @param password The password provided for authentication
     * @return The authenticated {@link User} object if successful; null otherwise.
     */
    public User login(String username, String password) {
        User user = DatabaseConnection.login(username, password);
        if (user != null) {
            this.currentUser = user;
        }
        return user;
    }

    /**
     * Registers a new customer in the system using the provided details.
     *
     * @param username The desired username
     * @param password The desired password
     * @param fullName The user's full name
     * @param phone The user's contact phone number
     * @param email The user's email address
     * @param address The user's physical address
     * @return true if the registration is successful; false if an error occurs.
     */
    public boolean register(String username, String password, String fullName, String phone, String email, String address) {
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