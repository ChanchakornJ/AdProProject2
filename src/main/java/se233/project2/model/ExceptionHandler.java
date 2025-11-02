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

public class ExceptionHandler {

    // เมธอดกลางที่ใช้ handle ทุก exception
    public static void handleException(Exception e) {
        // พิมพ์ stack trace
        e.printStackTrace();

        // ถ้าเป็น JavaFX Thread (GUI) ต้องใช้ Platform.runLater เพื่อให้ไม่ block UI
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Application Error");
            alert.setHeaderText("An error occurred!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        });
    }

    // ตั้งค่า global handler สำหรับ uncaught exceptions
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
