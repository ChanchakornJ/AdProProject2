module se233.project2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires java.desktop;


    exports se233.project2;
    opens se233.project2 to javafx.fxml;
}