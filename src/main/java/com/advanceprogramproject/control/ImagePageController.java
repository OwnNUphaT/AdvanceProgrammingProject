package com.advanceprogramproject.control;

import com.advanceprogramproject.model.DataModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class ImagePageController implements Initializable {
    @FXML
    private Label dimensionLabel;
    @FXML
    private Slider percentage;

    @FXML
    private Button downloadBtn;
    @FXML
    private ChoiceBox<String> imageFormat;
    @FXML
    private ImageView imagePreview;
    @FXML
    private TextField widthField;
    @FXML
    private TextField heightField;

    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupImagePreview();

        // Add the listener to adjust image percentage
        percentage.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            int percent = newValue.intValue();
            dimensionLabel.setText(percent + "%");
            Image resizedImage = percentImage(imagePreview.getImage(), percent);
            // You can use the resizedImage as needed, but don't set it to imagePreview
        });

        // Setting the width and height of the image
        resizeImage(imagePreview.getImage(), widthField.getText(), heightField.getText());

        // Initialize image format choice box
        imageFormat.getItems().addAll("JPG", "PNG");

        // Download Button
        downloadBtn.setOnAction(event -> saveImage());
    }

    private void setupImagePreview() {
        DataModel dataModel = DataModel.getInstance();
        File selectedFile = dataModel.getSelected();

        // Check if the file path is not null before using it
        if (selectedFile != null && selectedFile.exists()) {
            try {
                // Convert the file path to a URL with the file: protocol
                URL fileUrl = selectedFile.toURI().toURL();

                // Load the image using the URL
                Image image = new Image(fileUrl.toString());

                // Set the loaded image to the ImageView
                imagePreview.setImage(image);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File does not exist or is null.");
        }
    }

    private Image percentImage(Image image, double percentage) {
        try {
            double width = image.getWidth() * (1 + (percentage / 100.0));
            double height = image.getHeight() * (1 + (percentage / 100.0));

            return new Image(image.getUrl(), width, height, true, true);
        } catch (Exception e) {
            e.printStackTrace();  // Handle the exception appropriately
            return null;  // Return null or a default image in case of an error
        }
    }

    private Image resizeImage(Image image, String width, String height) {
        try {
            int newWidth = 0;
            int newHeight = 0;

            if (!width.isEmpty()) {
                newWidth = (int) ((image.getWidth() * 0.0) + Integer.parseInt(width));
            }

            if (!height.isEmpty()) {
                newHeight = (int) ((image.getHeight() * 0.0) + Integer.parseInt(height));
            }

            return new Image(image.getUrl(), newWidth, newHeight, true,true);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );

        // Selected format
        String selectedFormat = imageFormat.getValue();

        // Choose the directory for the file
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                // Get the resized image from the ImageView
                Image resizedImage = resizeImage(imagePreview.getImage(), widthField.getText(), heightField.getText());

                if (resizedImage != null) {
                    // Convert the JavaFX Image to a BufferedImage
                    BufferedImage originalImage = SwingFXUtils.fromFXImage(resizedImage, null);

                    // Save the image in the selected format
                    if (!ImageIO.write(originalImage, selectedFormat, file)) {
                        showAlert("Error", "Failed to save image.");
                    }
                } else {
                    showAlert("Error", "No image selected to save.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Error", "An error occurred while saving the image.");
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

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
