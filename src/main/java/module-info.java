module com.autohub.autohub {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.autohub.autohub to javafx.fxml;
    exports com.autohub.autohub;
}