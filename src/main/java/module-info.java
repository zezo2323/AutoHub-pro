module com.autohub.autohub {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.sql;

    opens com.autohub.autohub to javafx.fxml;
    opens com.autohub.autohub.frontend.controllers to javafx.fxml;

    // ضيف السطرين دول 👇
    opens com.autohub.autohub.backend.models to javafx.base;
    exports com.autohub.autohub.backend.models;

    exports com.autohub.autohub;
    exports com.autohub.autohub.frontend.controllers;
}
