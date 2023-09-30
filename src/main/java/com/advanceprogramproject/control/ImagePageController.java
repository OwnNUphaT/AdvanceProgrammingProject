package com.advanceprogramproject.control;

import com.advanceprogramproject.model.DataModel;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
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

    private int percent;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setupImagePreview();

        // Add the listener to adjust image percentage
        percentage.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            percent = newValue.intValue();
            dimensionLabel.setText(percent + "%");
            Image resizedImage = percentImage(imagePreview.getImage(), percent);
            // You can use the resizedImage as needed, but don't set it to imagePreview
        });

        // Setting the width and height of the image
        if (imagePreview.getImage() != null) {
            resizeImage(imagePreview.getImage(), widthField.getText(), heightField.getText());
        }

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
                // Load the image directly from the file path
                Image image = new Image(selectedFile.toURI().toString());
                imagePreview.setImage(image);
            } catch (Exception e) {
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


// ...

    private Image resizeImage(Image image, String width, String height) {
        try {
            int newWidth = !width.isEmpty() ? Integer.parseInt(width) : (int) image.getWidth();
            int newHeight = !height.isEmpty() ? Integer.parseInt(height) : (int) image.getHeight();

            // Create a writable image with the desired dimensions
            WritableImage writableImage = new WritableImage(newWidth, newHeight);

            // Get pixel readers and writers
            PixelReader pixelReader = image.getPixelReader();
            PixelWriter pixelWriter = writableImage.getPixelWriter();

            // Copy pixels from the original image to the new image
            for (int y = 0; y < newHeight; y++) {
                for (int x = 0; x < newWidth; x++) {
                    pixelWriter.setArgb(x, y, pixelReader.getArgb((int) (x * image.getWidth() / newWidth), (int) (y * image.getHeight() / newHeight)));
                }
            }

            return writableImage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private Image editedImage(Image image) {
        Image resizedImage;
        if (percent == 0) {
            resizedImage = resizeImage(image, widthField.getText(), heightField.getText());
        } else {
            resizedImage = percentImage(image, percent);
        }
        return resizedImage;
    }


    private void saveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        // Selected format
        String selectedFormat = imageFormat.getValue();

        FileChooser.ExtensionFilter extensionFilter = null;

        if ("JPG".equals(selectedFormat)) {
            extensionFilter = new FileChooser.ExtensionFilter("JPG", "*.jpg");
        } else if ("PNG".equals(selectedFormat)) {
            extensionFilter = new FileChooser.ExtensionFilter("PNG", "*.png");
        }

        if (extensionFilter != null) {
            fileChooser.getExtensionFilters().setAll(extensionFilter);
        }


        // Choose the directory for the file
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                Image resizedImage = editedImage(imagePreview.getImage());

                if (resizedImage != null) {
                    BufferedImage originalImage = SwingFXUtils.fromFXImage(resizedImage, null);

                    // Create a new BufferedImage with the correct color model for JPEG
                    BufferedImage convertedImage = new BufferedImage(
                            originalImage.getWidth(),
                            originalImage.getHeight(),
                            BufferedImage.TYPE_INT_RGB
                    );
                    convertedImage.createGraphics().drawImage(originalImage, 0, 0, Color.WHITE, null);

                    // Save the image in the selected format
                    if (!selectedFormat.equals("JPG")) {
                        ImageIO.write(convertedImage, "png", file);
                        showAlert("Success", "Image is Saved!");
                    } else {
                        ImageIO.write(convertedImage, "jpg", file);
                        showAlert("Success", "Image is Saved!");
                    }
                } else {
                    showAlert("Error", "No image selected to save.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Error", "An error occurred while saving the image: " + ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error", "Unexpected error: " + ex.getMessage());
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
