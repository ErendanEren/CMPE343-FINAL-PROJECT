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

public class SceneRouter {
    // Verileri (örneğin currentUser) saklamak için bir depo
    private static final Map<String, Object> data = new HashMap<>();

    // Veri kaydetmek için (Login anında kullanılır)
    public static void setData(String key, Object value) {
        data.put(key, value);
    }

    // Veri çekmek için (Controller içinde kullanılır)
    public static Object getData(String key) {
        return data.get(key);
    }

    // --- Sahne değiştirme metodu ---
    public static void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(SceneRouter.class.getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}