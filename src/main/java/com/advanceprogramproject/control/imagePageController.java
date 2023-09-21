package com.advanceprogramproject.control;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    int percent;

    public void setStage(Stage stage) {
        this.stage = stage;
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        dimensionSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                percent = (int) dimensionSlider.getValue();
                dimensionLabel.setText(Integer.toString(percent) + "%");
            }
        });

        qualitySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                percent = (int) qualitySlider.getValue();
                qualityLabel.setText(Integer.toString(percent) + "%");
            }
        });

        //TODO: Make all slider Label display the value of the slider under the slider
        // Make sure its work na kub

        //TODO: Display the image file that'd been dropped. To the imagePreview

    }
}
