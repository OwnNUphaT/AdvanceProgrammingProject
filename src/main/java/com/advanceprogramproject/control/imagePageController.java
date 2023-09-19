package com.advanceprogramproject.control;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class imagePageController implements Initializable {
    private Stage stage;
    @FXML
    private Label dimensionLabel;
    @FXML
    private Label qualityLabel;
    @FXML
    private Slider dimensionSlider;
    @FXML
    private Slider qualitySlider;

    public void setStage(Stage stage) {
        this.stage = stage;
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO: Make all slider Label display the value of the slider under the slider
        // Make sure its work na kub

        //TODO: Display the image file that'd been dropped. To the imagePreview

    }
}
