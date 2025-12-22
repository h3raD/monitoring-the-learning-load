module com.course.loadmonitorstudents {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.jfr;
    requires org.apache.pdfbox;
    requires javafx.base;
    requires jbcrypt;
    requires java.desktop;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.services.calendar;
    requires google.api.client;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client.auth;
    requires org.json;
    requires junit;


    opens com.course.loadmonitorstudents to javafx.fxml, javafx.base;
    opens com.course.loadmonitorstudents.controller to javafx.fxml, javafx.base;
    opens com.course.loadmonitorstudents.model to javafx.fxml, javafx.base;
    opens com.course.loadmonitorstudents.util to javafx.fxml, javafx.base;
    opens com.course.loadmonitorstudents.dto to javafx.fxml, javafx.base;
    opens com.course.loadmonitorstudents.service to javafx.fxml, javafx.base;

    exports com.course.loadmonitorstudents;
    exports com.course.loadmonitorstudents.controller;
    exports com.course.loadmonitorstudents.model;
    exports com.course.loadmonitorstudents.dto;
    exports com.course.loadmonitorstudents.util;
    exports com.course.loadmonitorstudents.service;

}