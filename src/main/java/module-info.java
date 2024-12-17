module cherryontop {

    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires java.desktop;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.apache.pdfbox;
    requires org.apache.commons.logging;


    exports controller;
    opens controller to javafx.fxml; // Replace 'org.openjfx' with your package name

    exports model;
    opens model to javafx.fxml;
}