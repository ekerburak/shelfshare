module org.openjfx {
    requires javafx.controls;
    exports org.openjfx;
    requires transitive javafx.graphics;
    requires javafx.fxml;

    opens org.openjfx to javafx.fxml; // Replace 'org.openjfx' with your package name

}