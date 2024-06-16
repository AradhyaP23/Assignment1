module com.example.assignment1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.assignment1 to javafx.fxml;
    exports com.example.assignment1;
}