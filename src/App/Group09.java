package App;

import javafx.application.Application;
import javafx.stage.Stage;

public class Group09 extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Initialize SceneManager
            Utils.SceneManager.getInstance().setPrimaryStage(stage);



            // Load initial view
            Utils.SceneManager.switchSceneStatic("/fxml/Login.fxml");

            stage.setTitle("Group09 GreenGrocer");
            // stage.show() is handled by SceneManager, but we insure it just in case or SceneManager might just set scene.
            // SceneManager.switchScene calls show(), so we are good.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
