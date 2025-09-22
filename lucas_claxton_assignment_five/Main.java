

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        DrawingController controller = new DrawingController();
        Scene scene = new Scene(controller.getMainPane(), 800, 600);

        primaryStage.setTitle("Drawing App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    
}
