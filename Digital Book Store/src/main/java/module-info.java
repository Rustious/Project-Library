module org.example.assignment2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens Controller to javafx.fxml;
    exports Controller;
    exports SubClasses;
    opens SubClasses to javafx.fxml;
    exports Databases;
    opens Databases to javafx.fxml;
}