package Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Failed to load scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void switchSceneStatic(String fxmlPath) {
        if (instance != null) {
            instance.switchScene(fxmlPath);
        }
    }

    private static final Map<String, Object> dataMap = new HashMap<>();

    public static void putData(String key, Object value) {
        dataMap.put(key, value);
    }

    public static Object getData(String key) {
        return dataMap.get(key);
    }
}
