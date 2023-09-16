package com.advanceprogramproject.control;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TextPageController implements Initializable {
    private Stage stage;
    @FXML
    private Label visibilityLabel;
    @FXML
    private Label paddingLabel;
    @FXML
    private Label sizeLabel;
    @FXML
    private Slider VisibilitySlider;
    @FXML
    private Slider paddSlider;
    @FXML
    private Slider sizeSlider;



    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //TODO: Make all slider Label display the value of the slider under the slider

    }
}
