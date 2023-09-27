package com.advanceprogramproject.control;

import com.advanceprogramproject.model.DataModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
    @FXML
    private TextField widthField;
    @FXML
    private Button downloadBtn;
    @FXML
    private TextField heightField;
    @FXML
    private ChoiceBox imageFormat;
    private Scene scene;

    @FXML
    private ImageView imagePreview;

    private DataModel dataModel;
    int percent;


    public void setStage(Stage stage) {
        this.stage = stage;
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Calculate the initial percentage based on the original size
        double initialPercent = 50.0;

        // Set the initial value of the dimension slider
        dimensionSlider.setValue(initialPercent);

        // Update the label to show the initial percentage
        dimensionLabel.setText(Integer.toString((int) initialPercent) + "%");


        DataModel dataModel = DataModel.getInstance();

        // Check if the file path is not null before using it
        if (dataModel.getDropFilePath() != null) {
            // Use dataModel.getDropFilePath() to access the file path
            String filePath = dataModel.getDropFilePath();
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    try {
                        // Convert the file path to a URL with the file: protocol
                        URL fileUrl = file.toURI().toURL();

                        // Load the image using the URL
                        Image image = new Image(fileUrl.toString());

                        // Set the loaded image to the ImageView
                        imagePreview.setImage(image);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (RuntimeException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("File does not exist: " + filePath);
                }
                // Load the image or perform other operations with the file path
            } else {
                System.out.println("File path is null or not set.");
            }

        // Add the listener to adjust image dimensions
        dimensionSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                int percent = (int) dimensionSlider.getValue();
                dimensionLabel.setText(Integer.toString(percent) + "%");

                // Adjust the image dimensions based on the slider value
                double originalWidth = imagePreview.getImage().getWidth();
                double originalHeight = imagePreview.getImage().getHeight();
                double scaleFactor = percent / 100.0;
                imagePreview.setFitWidth(scaleFactor * originalWidth);
                imagePreview.setFitHeight(scaleFactor * originalHeight);
            }
        });


        //quality slider percentage
        qualitySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                percent = (int) qualitySlider.getValue();
                qualityLabel.setText(Integer.toString(percent) + "%");

                // Calculate the smoothing value based on the quality percentage.
                // Assuming that higher quality means more smoothing.
                double smoothingValue = (100.0 - percent) / 100.0; // Inverse relationship

                // Set the smoothing value to control image quality.
                imagePreview.setSmooth(smoothingValue > 0.0);

            }
        });

        String[] fileFormat = {"JPG", "PNG"};
        imageFormat.getItems().addAll(fileFormat);


        //Download Button
        downloadBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png")
            );

            // Selected the format
            String selectedFormat = (String) imageFormat.getSelectionModel().getSelectedItem();


            // Choose the directory for the file
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(imagePreview.getImage(), null), selectedFormat, file);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });


        //Back to main-view page.
        BackBtnImage.setOnAction(event -> {
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
    // Method to show a simple alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}