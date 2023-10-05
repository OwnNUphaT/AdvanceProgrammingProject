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
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;



import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class TextPageController implements Initializable {
    private Stage stage;
    @FXML
    private Label VisibilityLabel;
    @FXML
    private Label SizeLabel;
    @FXML
    private Slider TextSizeSlider;
    @FXML
    private Slider rotationSlider;
    @FXML
    private Button BackBtnText;
    @FXML
    private ImageView imagePreview;
    @FXML
    private TextField textField;
    @FXML
    private Button clear;
    @FXML
    private ChoiceBox<String> fontDrop;
    @FXML
    private ChoiceBox<String> formatDrop;
    @FXML
    private ChoiceBox alignmentDrop;
    DataModel dataModel = DataModel.getInstance();
    double x = 0, y = 0;



    private Scene scene;
    int percent;

    @FXML
    public void handleDownloadAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        // Selected format
        String selectedFormat = formatDrop.getValue();


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
                            originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB
                    );
                    convertedImage.createGraphics().drawImage(originalImage, 0, 0, java.awt.Color.WHITE, null);
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
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        rotationSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                SizeLabel.setText(newValue.intValue() + "");
            }
        });

        TextSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                percent = (int) TextSizeSlider.getValue();
                VisibilityLabel.setText(Integer.toString(percent) + "");
            }
        });

        fontDrop.getItems().addAll(Font.getFamilies());



        // Check if the file path is not null before using it
        if (dataModel.getDropFilePaths() != null) {
            List<File> selectedFiles = dataModel.getDropFilePaths();

            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                for (File selectedFile : selectedFiles) {
                    try {
                        Image image = new Image(selectedFile.toURI().toString());
                        imagePreview.setImage(image);

                        Arrays.stream(selectedFile.getName().split("[\\s\\W]+"))
                                .forEach(word -> System.out.println("Word: " + word));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("No files selected or the list is empty.");
            }

            // Adding Format ChoiceBox
            formatDrop.getItems().addAll("JPG", "PNG");

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

            alignmentDrop.getItems().addAll("Top Left", "Top Right", "Bottom Left", "Bottom Right", "Center");

        }
    }
    // Method to update the watermark based on the current settings
    @FXML
    public Image applyWatermark() {

        String watermarkText = textField.getText();
        if (watermarkText.isEmpty()) {
            return null;
        }

        double TextSize = TextSizeSlider.getValue();
        Image originalImage = imagePreview.getImage();
        Canvas canvas = new Canvas(originalImage.getWidth(), originalImage.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw the original image
        gc.drawImage(originalImage, 0, 0);

        // Set up graphics context for watermarking
        String selectedFont = fontDrop.getSelectionModel().getSelectedItem();
        gc.setFont(new Font(selectedFont, TextSize));

        gc.setFill(Color.BLACK);
        gc.setGlobalAlpha(0.5);

        Text textNode = new Text(watermarkText);
        textNode.setFont(gc.getFont());
        double textWidth = textNode.getLayoutBounds().getWidth();
        double textHeight = textNode.getLayoutBounds().getHeight();

        // Call the alignment method
        alignWatermark(textWidth, textHeight, originalImage);

        gc.translate(x + textWidth / 2, y - textHeight / 2); // Adjusted for text baseline
        gc.rotate(rotationSlider.getValue());
        gc.translate(-x - textWidth / 2, -y + textHeight / 2); // Adjusted for text baseline

        // Draw the rotated text
        gc.fillText(watermarkText, x, y);


        WritableImage watermarkedImage = canvas.snapshot(null, null);
        imagePreview.setImage(watermarkedImage);
        return originalImage;
    }


    @FXML
    public void resetWatermark() {
        DataModel dataModel = DataModel.getInstance();
        if (dataModel.getDropFilePaths() != null) {
            List<File> selectedFiles = dataModel.getDropFilePaths();

            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                for (File selectedFile : selectedFiles) {
                    try {
                        Image image = new Image(selectedFile.toURI().toString());
                        imagePreview.setImage(image);

                        Arrays.stream(selectedFile.getName().split("[\\s\\W]+"))
                                .forEach(word -> System.out.println("Word: " + word));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("No files selected or the list is empty.");
            }
        }
    }
    private void alignWatermark(double textWidth, double textHeight, Image originalImage) {

        String alignment = (String) alignmentDrop.getSelectionModel().getSelectedItem();

        switch (alignment) {
            case "Top Left":
                x = 10; // Small margin from top-left
                y = textHeight + 10; // Y position is adjusted for text baseline
                break;
            case "Top Right":
                x = originalImage.getWidth() - textWidth - 10;
                y = textHeight + 10;
                break;
            case "Bottom Left":
                x = 10;
                y = originalImage.getHeight() - 10;
                break;
            case "Bottom Right":
                x = originalImage.getWidth() - textWidth - 10;
                y = originalImage.getHeight() - 10;
                break;
            case "Center":
                x = (originalImage.getWidth() - textWidth) / 2;
                y = (originalImage.getHeight() - textHeight) / 2 + textHeight; // Adjust y position for text baseline
                break;
        }

    }

    private Image editedImage(Image image) {
        applyWatermark();

        return applyWatermark();
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