module com.example.socialnetworkapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    opens com.example.socialnetworkapp to javafx.fxml;
    opens com.example.socialnetworkapp.controlers to javafx.fxml;
    exports com.example.socialnetworkapp;
    exports com.example.socialnetworkapp.controlers;
}