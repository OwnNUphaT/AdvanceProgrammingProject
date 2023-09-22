package com.advanceprogramproject.control;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.io.IOException;
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
    @FXML
    private Button BackBtnImage;
    private Scene scene;
    int percent;

    public void setStage(Stage stage) {
        this.stage = stage;
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //dimension slider percentage
        dimensionSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                percent = (int) dimensionSlider.getValue();
                dimensionLabel.setText(Integer.toString(percent) + "%");
            }
        });

        //quality slider percentage
        qualitySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                percent = (int) qualitySlider.getValue();
                qualityLabel.setText(Integer.toString(percent) + "%");
            }
        });

        //TODO: Display the image file that'd been dropped. To the imagePreview

        //Back to main-view page.
        BackBtnImage.setOnAction(event -> {
            try {
                stage.close();

                FXMLLoader loader = new FXMLLoader(MainViewController.class.getResource("/com/advanceprogramproject/views/main-view.fxml"));
                Parent root = loader.load();
                // Pass the current stage reference to the new controller
                MainViewController mainViewController = loader.getController();
                mainViewController.setStage(stage);

                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException();
            }

        });
    }
}
