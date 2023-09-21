package com.advanceprogramproject.control;

import com.advanceprogramproject.model.FilePath;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    private ImageView imagePreview;
    int percent;
    FilePath filePath = new FilePath();

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
        if (filePath.getFile() != null) {
            try {
                FileInputStream fileInputStream = new FileInputStream(filePath.getFile());
                Image image = new Image(fileInputStream);
                imagePreview.setImage(image);
                fileInputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Image Not Found");
        }
    }
}
