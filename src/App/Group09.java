package App;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main entry point for the Group09 GreenGrocer application.
 * This class initializes the primary stage and manages the initial
 * scene transition using the {@link Utils.SceneManager}.
 *
 * @author Selçuk Aloba
 * @author Eren Çakır Bircan
 */
public class Group09 extends Application {

    /**
     * Starts the JavaFX application by setting up the primary stage
     * and loading the initial login view.
     *
     * @param stage The primary stage for this application, onto which
     * the application scene can be set.
     */
    @Override
    public void start(Stage stage) {
        try {
            Utils.SceneManager.getInstance().setPrimaryStage(stage);

            Utils.SceneManager.switchSceneStatic("/fxml/Login.fxml");

            stage.setTitle("Group09 GreenGrocer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method that serves as the fallback entry point for the application.
     *
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}