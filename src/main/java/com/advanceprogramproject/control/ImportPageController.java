package com.advanceprogramproject.control;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ImportPageController implements Initializable {
    private Stage stage;

    @FXML
    private Button imageBtn;

    @FXML
    private Button textBtn;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Method to close the previous scene
    public void closePreviousScene() {
        if (stage != null) {
            stage.close();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageBtn.setOnAction(event -> {
            stage.close();
        });

        textBtn.setOnAction(event -> {
            System.out.println("lets put some watermark on");
        });
    }
}
