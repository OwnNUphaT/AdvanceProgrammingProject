module com.example.advanceprogramproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.advanceprogramproject to javafx.fxml;
    opens com.advanceprogramproject.control;
    exports com.advanceprogramproject;
    exports com.advanceprogramproject.control;
}