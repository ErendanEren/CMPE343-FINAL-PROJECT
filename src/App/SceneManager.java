package App;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class responsible for managing scene transitions and data sharing
 * between different controllers in the JavaFX application.
 *
 * @author dulgerarda
 * @author zafermertserinken
 */
public class SceneManager {

    /**
     * Internal storage for passing objects and data between different scenes.
     */
    private static final Map<String, Object> data = new HashMap<>();

    /**
     * Stores a piece of data associated with a specific key.
     *
     * @param key   The unique identifier for the data
     * @param value The object to be stored
     */
    public static void putData(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Retrieves stored data associated with the given key.
     *
     * @param key The unique identifier for the data to retrieve
     * @return The object associated with the key, or null if not found
     */
    public static Object getData(String key) {
        return data.get(key);
    }

    /**
     * Switches the current stage to a new scene defined by the provided FXML path.
     *
     * @param event    The ActionEvent triggered by a UI component
     * @param fxmlPath The resource path to the FXML file
     */
    public static void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("FXML Yükleme Hatası: " + fxmlPath);
            e.printStackTrace();
        }
    }
}