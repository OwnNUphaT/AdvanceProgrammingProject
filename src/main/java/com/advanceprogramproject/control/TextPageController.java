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

public class TextPageController implements Initializable {
    private Stage stage;
    @FXML
    private Label VisibilityLabel;
    @FXML
    private Label PaddingLabel;
    @FXML
    private Label SizeLabel;
    @FXML
    private Slider VisibilitySlider;
    @FXML
    private Slider PaddSlider;
    @FXML
    private Slider SizeSlider;
    @FXML
    private Button BackBtnText;
    private Scene scene;
    int percent;



    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Visibility slider percentage.
        VisibilitySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                percent = (int) VisibilitySlider.getValue();
                VisibilityLabel.setText(Integer.toString(percent) + "%");
            }
        });

        //Padding slider percentage.
        PaddSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                percent = (int) PaddSlider.getValue();
                PaddingLabel.setText(Integer.toString(percent) + "%");
            }
        });

        //Size slider percentage.
        SizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                percent = (int) SizeSlider.getValue();
                SizeLabel.setText(Integer.toString(percent) + "%");
            }
        });

        //Back to main-view page.
        BackBtnText.setOnAction(event -> {
            try {
                stage.close();

                FXMLLoader loader = new FXMLLoader(MainViewController.class.getResource("/com/advanceprogramproject/views/imported-page.fxml"));
                Parent root = loader.load();
                // Pass the current stage reference to the new controller
                ImportPageController importPageController = loader.getController();
                importPageController.setStage(stage);

                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException();
            }

        });

    }
}
