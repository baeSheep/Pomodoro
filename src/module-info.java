module Pomodoro_v2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires javafx.base;
    requires java.logging;

    // Export package controller to javafx.fxml
    exports controller to javafx.fxml;
    
    // Open package controller to javafx.fxml
    opens controller to javafx.fxml;
    
    // Open package application to necessary modules
    opens application to javafx.graphics, javafx.fxml, javafx.base;
    
    // Other exports and opens if needed
}
