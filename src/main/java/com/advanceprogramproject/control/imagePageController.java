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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
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
    private ListView imagePreview;

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
        String selectedFile = dataModel.getSelected();
        System.out.println("Selected File: " + selectedFile);

        if (selectedFile != null && !selectedFile.isEmpty()) {
            imagePreview.getItems().add(selectedFile);
        }



        // Add the listener to adjust image dimensions
        dimensionSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                int percent = (int) dimensionSlider.getValue();
                dimensionLabel.setText(Integer.toString(percent) + "%");

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

            }
        });

        String[] fileFormat = {"JPG", "PNG"};
        imageFormat.getItems().addAll(fileFormat);


        // Download Button
        downloadBtn.setOnAction(event -> saveImage());




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
    private void saveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );

        // Selected format
        String selectedFormat = (String) imageFormat.getSelectionModel().getSelectedItem();

        // Choose the directory for the file
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                // Check if any item is selected in the ListView
                if (!imagePreview.getSelectionModel().getSelectedItems().isEmpty()) {
                    String fileName = (String) imagePreview.getSelectionModel().getSelectedItem();

                    // Load the original image
                    String fileImage = dataModel.getDropFilePaths().get(0).toString(); // Assuming there's only one image
                    Image image = new Image(new File(fileImage).toURI().toURL().toString());

                    // Convert the JavaFX Image to a BufferedImage
                    BufferedImage originalImage = SwingFXUtils.fromFXImage(image, null);

                    // Save the image in the selected format
                    ImageIO.write(originalImage, selectedFormat, file);
                } else {
                    showAlert("Error", "No image selected to save.");
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
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