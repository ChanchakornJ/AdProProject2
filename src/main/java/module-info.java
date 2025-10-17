module se233.project2 {
    requires javafx.controls;
    requires javafx.fxml;


    exports se233.project2;
    opens se233.project2 to javafx.fxml;
}