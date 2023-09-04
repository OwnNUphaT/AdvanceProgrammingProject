module com.example.advanceprogramproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.advanceprogramproject to javafx.fxml;
    exports com.advanceprogramproject;
}