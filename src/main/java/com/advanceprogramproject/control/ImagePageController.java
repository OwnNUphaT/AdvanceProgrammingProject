package com.advanceprogramproject.control;

import com.advanceprogramproject.model.DataModel;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ImagePageController implements Initializable {
    @FXML
    private Label dimensionLabel;
    @FXML
    private Slider percentage;

    @FXML
    private Button downloadBtn;
    @FXML
    private Button BackBtnImage;
    @FXML
    private Button saveBtn;
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
    private ExecutorService executorService;
    private List<Future<Image>> imageFutures;
    DataModel dataModel = new DataModel();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupImagePreview();


        percentage.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            percent = newValue.intValue();
            dimensionLabel.setText(percent + "%");
            Image resizedImage = percentImage(imagePreview.getImage(), percent);
        });

        if (imagePreview.getImage() != null) {
            resizeImage(imagePreview.getImage(), widthField.getText(), heightField.getText());
        }



        imageFormat.getItems().addAll("JPG", "PNG");
        dataModel.setSelectedFormat(imageFormat.getValue());

        downloadBtn.setOnAction(event -> downloadImage());

        // Initialize ExecutorService
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(numberOfThreads);

        // Initialize list for holding Future<Image>
        imageFutures = new CopyOnWriteArrayList<>();

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

    private void updateImagePreviewList() {
        // Other code remains the same
        List<File> selectedFiles = dataModel.getDropFilePaths();
        // Submit tasks for image processing
        imageFutures.clear();
        for (File selectedFile : selectedFiles) {
            Future<Image> future = executorService.submit(() -> processImage(selectedFile));
            imageFutures.add(future);
        }
    }

    private Image processImage(File selectedFile) {
        try {
            // Your image processing logic here, e.g., resizing
            return editedImage(new Image(selectedFile.toURI().toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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

    private Image resizeImage(Image image, String width, String height) {
        try {
            int newWidth = !width.isEmpty() ? Integer.parseInt(width) : (int) image.getWidth();
            int newHeight = !height.isEmpty() ? Integer.parseInt(height) : (int) image.getHeight();
            WritableImage writableImage = new WritableImage(newWidth, newHeight);
            PixelReader pixelReader = image.getPixelReader();
            PixelWriter pixelWriter = writableImage.getPixelWriter();

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
