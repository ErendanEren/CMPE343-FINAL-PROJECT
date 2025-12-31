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

public class SceneManager {
    // Verileri saklamak için Map
    private static final Map<String, Object> data = new HashMap<>();

    // Senin LoginController'da kullandığın isim: putData
    public static void putData(String key, Object value) {
        data.put(key, value);
    }

    // Senin CarrierController'da kullandığın isim: getData
    public static Object getData(String key) {
        return data.get(key);
    }

    // Sahne değiştirme metodu
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