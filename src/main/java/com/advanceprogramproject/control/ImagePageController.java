package com.advanceprogramproject.control;

import com.advanceprogramproject.model.DataModel;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class ImagePageController implements Initializable {
    @FXML
    private Label dimensionLabel;
    @FXML
    private Slider percentage;
    @FXML
    private Button BackBtnImage;
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
    DataModel dataModel = new DataModel();
    private int currentImageIndex = 0;


    @FXML
    private void downloadImage() { // Download the image.
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
                            originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB
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

    @FXML
    public void downloadAll() {
        // Use DirectoryChooser instead of FileChooser to select the directory to save images
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Save Images");
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            List<Image> editedImages = getEditedImages(); // Get the list of edited images

            if (editedImages == null || editedImages.isEmpty()) {
                System.out.println("No images to save.");
                return;
            }

            int counter = 0; // For naming the saved images

            for (Image editedImage : editedImages) {
                try {
                    BufferedImage originalImage = SwingFXUtils.fromFXImage(editedImages.get(counter), null);

                    // Create a new BufferedImage with the correct color model for JPEG
                    BufferedImage convertedImage = new BufferedImage(
                            originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB
                    );
                    convertedImage.createGraphics().drawImage(originalImage, 0, 0, Color.WHITE, null);

                    // Determine file extension
                    String ext = imageFormat.getValue().toLowerCase();

                    // Construct the filename for the watermarked image
                    File outputFile = new File(selectedDirectory, "resizedImage_" + counter + "." + ext);

                    // Save the image in the selected format
                    ImageIO.write(convertedImage, ext, outputFile);

                    showAlert("Success", "Image is Saved!");

                    counter++;
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupImagePreview();


        // Processing the Percentage Value
        percentage.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            percent = newValue.intValue();
            dimensionLabel.setText(percent + "%");
            Image resizedImage = percentImage(imagePreview.getImage(), percent);
        });

        if (imagePreview.getImage() != null) {
            resizeImage(imagePreview.getImage(), widthField.getText(), heightField.getText());
        }


        // Adding Format Items
        imageFormat.getItems().addAll("JPG", "PNG");
        dataModel.setSelectedFormat(imageFormat.getValue());

        // Processing the Back to the previous page
        BackBtnImage.setOnAction(event -> {
            try {
                stage.close();

                FXMLLoader loader = new FXMLLoader(ImagePageController.class.getResource("/com/advanceprogramproject/views/imported-page.fxml"));
                Parent root = loader.load();

                ImportPageController controller = loader.getController();
                controller.setStage(stage);

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        });
    }

    // Set up the image to show up in to the ImagePreview
    private void setupImagePreview() { // Setting up the listView
        DataModel dataModel = DataModel.getInstance();
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

    //Process the percent resize of the image
    private Image percentImage(Image image, double percentage) {
        try {
            double width = image.getWidth() * (1 + (percentage / 100.0));
            double height = image.getHeight() * (1 + (percentage / 100.0));
            return new Image(image.getUrl(), width, height, true, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Process the percentage resize with the text flied
    private Image resizeImage(Image image, String width, String height) {
        try {
            int newWidth = !width.isEmpty() ? Integer.parseInt(width) : (int) image.getWidth();
            int newHeight = !height.isEmpty() ? Integer.parseInt(height) : (int) image.getHeight();

            return new Image(image.getUrl(), newWidth, newHeight, true,true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void handleNextImage() {
        currentImageIndex++;
        if (currentImageIndex >= DataModel.getInstance().getDropFilePaths().size()) {
            currentImageIndex = 0; // loop back to the first image if we're at the end
        }
        updateImagePreview();
    }

    @FXML
    public void handlePrevImage() {
        currentImageIndex--;
        if (currentImageIndex < 0) {
            currentImageIndex = DataModel.getInstance().getDropFilePaths().size() - 1; // loop back to the last image if we're at the beginning
        }
        updateImagePreview();
    }

    private void updateImagePreview() {
        File fileImage = DataModel.getInstance().getDropFilePaths().get(currentImageIndex);
        Image image;
        try {
            image = new Image(fileImage.toURI().toURL().toString());
            imagePreview.setImage(image);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    //
    private List<Image> getEditedImages() {
        List<File> selectedFiles = DataModel.getInstance().getDropFilePaths();
        List<Image> editedImages = new ArrayList<>();

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            for (File selectedFile : selectedFiles) {
                try {
                    if (!selectedFile.getName().toLowerCase().endsWith(".zip")) {
                        System.out.println("Processing file: " + selectedFile.getName());
                        Image originalImage = new Image(selectedFile.toURI().toString());
                        Image editedImage = editedImage(originalImage); // Call the method once
                        editedImages.add(editedImage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No files selected or the list is empty.");
        }

        return editedImages;
    }




    // Choose between the percentage and text flied value
    private Image editedImage(Image image) { // Create editedImage
        Image resizedImage;
        if (percent == 0) {
            resizedImage = resizeImage(image, widthField.getText(), heightField.getText());
        } else {
            resizedImage = percentImage(image, percent);
        }
        dataModel.setSelectedFormat(imageFormat.getValue());
        dataModel.setEditedImage(resizedImage);
        return resizedImage;
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
