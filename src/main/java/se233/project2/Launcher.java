package se233.project2;

import javafx.application.Application;
import javafx.stage.Stage;
import se233.project2.controller.StageManager;
import se233.project2.model.ExceptionHandler;

public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

@Override
public void start(Stage primaryStage){
    ExceptionHandler.registerGlobalHandler();
    StageManager stageManager = new StageManager(primaryStage);
    stageManager.loadStage(0); // start from menu now
    primaryStage.setTitle("Contra Game");
    primaryStage.show();
}

}
