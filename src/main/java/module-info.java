module com.course.loadmonitorstudents {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.jfr;
    requires org.apache.pdfbox;
    requires javafx.base;

    opens com.course.loadmonitorstudents to javafx.fxml;
    opens com.course.loadmonitorstudents.controller to javafx.fxml;
    opens com.course.loadmonitorstudents.model to javafx.fxml;
    opens com.course.loadmonitorstudents.util to javafx.fxml;

    exports com.course.loadmonitorstudents;
    exports com.course.loadmonitorstudents.controller;
    exports com.course.loadmonitorstudents.model;
}