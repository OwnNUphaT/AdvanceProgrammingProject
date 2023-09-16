package com.advanceprogramproject.control;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ImportPageController {
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

    public void Initialize() {

        imageBtn.setOnAction(event -> {
            stage.close();
        });

        textBtn.setOnAction(event -> {
            System.out.println("lets put some watermark on");
        });

    }
}
