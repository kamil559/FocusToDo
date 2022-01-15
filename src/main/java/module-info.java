module com.example.focustodoapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.focustodoapp to javafx.fxml;
    exports com.example.focustodoapp;
    exports com.example.focustodoapp.controllers;
    opens com.example.focustodoapp.controllers to javafx.fxml;
}