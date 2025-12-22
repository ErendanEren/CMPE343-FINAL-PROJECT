package App;

import javafx.application.Application;
import javafx.stage.Stage;

public class Group09 extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // SceneRouter logic embedded directly for standalone usage
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 960, 540);
            
            stage.setTitle("Group09 GreenGrocer");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
