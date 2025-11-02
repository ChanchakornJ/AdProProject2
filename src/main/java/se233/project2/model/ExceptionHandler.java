//package se233.project2.model;
//
//public class CustomException extends Exception {
//    public CustomException(String message) {
//        super(message);
//    }
//    public CustomException(String message, Throwable cause) {
//            super(message, cause);
//    }
//}

package se233.project2.model;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ExceptionHandler  {

    public static void handleException(Exception e) {
        e.printStackTrace();

//        Platform.runLater(() -> {
//            Alert alert = new Alert(AlertType.ERROR);
//            alert.setTitle("Application Error");
//            alert.setHeaderText("An error occurred!");
//            alert.setContentText(e.getMessage());
//            alert.showAndWait();
//        });
    }

    public static void registerGlobalHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            if (throwable instanceof Exception e) {
                handleException(e);
            } else {
                // handle Throwable (rare cases)
                throwable.printStackTrace();
            }
        });
    }
}
