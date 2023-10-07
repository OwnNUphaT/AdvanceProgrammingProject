package com.advanceprogramproject.control;

import com.advanceprogramproject.model.DataModel;
import javafx.application.Platform;
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
import javafx.stage.DirectoryChooser;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private ChoiceBox<String> fontDrop;
    @FXML
    private ChoiceBox alignmentDrop;
    @FXML
    private ChoiceBox<String> formatDrop;
    @FXML
    private Button nextButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button downloadCurrentButton;

    double x = 0, y = 0;
    private Scene scene;
    int percent;
    private int currentImageIndex = 0;

    @FXML
    public void handleDownloadCurrentImage() {
        Image currentImage = imagePreview.getImage();

        // Check if there is a current image to download
        if (currentImage != null) {
            // Use FileChooser to select the directory to save the image
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp");
            fileChooser.getExtensionFilters().add(extFilter);

            File selectedFile = fileChooser.showSaveDialog(stage);

            if (selectedFile != null) {
                try {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(currentImage, null);

                    // Determine file extension based on the selected format
                    String ext = formatDrop.getSelectionModel().getSelectedItem().toLowerCase();

                    if ("jpg".equals(ext)) {
                        // For JPEG, remove alpha channel (transparency)
                        BufferedImage convertedImg = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                        convertedImg.createGraphics().drawImage(bufferedImage, 0, 0, java.awt.Color.WHITE, null);
                        bufferedImage = convertedImg;
                    }

                    // Save the image with the selected extension
                    ImageIO.write(bufferedImage, ext, selectedFile);

                    // You can add a success message or alert here
                    System.out.println("Image saved successfully.");
                } catch (IOException e) {
                    // Handle any potential exceptions
                    e.printStackTrace();
                }
            }
        } else {
            // Handle the case where there is no current image to download
            System.out.println("No image to download.");
        }
    }

    @FXML
    public void handleDownloadAction() {
        // Use DirectoryChooser instead of FileChooser to select the directory to save images
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Save Images");
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            DataModel dataModel = DataModel.getInstance();

            if (dataModel.getDropFilePaths() == null || dataModel.getDropFilePaths().isEmpty()) {
                System.out.println("No images to save.");
                return;
            }

            System.out.println(dataModel.getDropFilePaths().size());

            ExecutorService executor = Executors.newFixedThreadPool(6);
            executor.submit(() -> {
                Platform.runLater(() -> {
                    for (int i = 1; i < dataModel.getDropFilePaths().size(); i++) {
                        try {
                            File file = dataModel.getDropFilePaths().get(i);
                            saveMultiImage(file , i, selectedDirectory);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            System.out.println("MalFormed");
                        } catch (IOException e) {
                            System.out.println("IO");
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("The Processing is finished");
                });
            });
            executor.shutdown();
        }
    }

    public void saveMultiImage(File file, int i, File selectedDirectory) throws IOException, MalformedURLException {

        // Load the original image
        Image originalImage = new Image(file.toURI().toURL().toString());
        System.out.println(originalImage);

        // Apply the watermark on the image
        imagePreview.setImage(originalImage);
        applyWatermark();

        // Get the watermarked image
        Image watermarkedImage = imagePreview.getImage();

        // Convert to BufferedImage
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(watermarkedImage, null);

        // Determine file extension
        String ext = formatDrop.getSelectionModel().getSelectedItem().toLowerCase();

        if ("jpg".equals(ext)) {
            // For JPEG, remove alpha channel (transparency)
            BufferedImage convertedImg = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            convertedImg.createGraphics().drawImage(bufferedImage, 0, 0, java.awt.Color.WHITE, null);
            bufferedImage = convertedImg;
        }

        // Construct the filename for the watermarked image
        File outputFile = new File(selectedDirectory, "watermarked_" + i + "." + ext);

        // Save the watermarked image
        ImageIO.write(bufferedImage, ext, outputFile);
        System.out.println("Image Saved");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /////////////////////////////////   initialize   /////////////////////////////////
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Inside the initialize() method:
        formatDrop.getItems().addAll("PNG", "JPG", "BMP");
        formatDrop.getSelectionModel().selectFirst(); // Default to PNG


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

        DataModel dataModel = DataModel.getInstance();

        // Check if the file path is not null before using it
        if (dataModel.getDropFilePaths() != null) {
            // Use dataModel.getDropFilePath() to access the file path
            // Load the original image
            String fileImage = dataModel.getDropFilePaths().get(0).toString();
            if (fileImage != null) {
                File file = new File(fileImage);
                if (file.exists()) {
                    try {

                        // Assuming there's only one image
                        Image image = new Image(new File(fileImage).toURI().toURL().toString());

                        // Set the loaded image to the ImageView
                        imagePreview.setImage(image);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (RuntimeException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("File does not exist: " + fileImage);
                }
                // Load the image or perform other operations with the file path
            } else {
                System.out.println("File path is null or not set.");
            }

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

            downloadCurrentButton.setDisable(true); // Initially disable the button
            imagePreview.imageProperty().addListener((observable, oldValue, newValue) -> {
                // Enable the button when a new image is set
                downloadCurrentButton.setDisable(newValue == null);
            });

            alignmentDrop.getItems().addAll("Top Left", "Top Right", "Bottom Left", "Bottom Right", "Center");

            try {
                updateImagePreview();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

        }
    }

    ////////////////////////// END initialize //////////////////////////////

    @FXML
    public void handleNextImage() {
        currentImageIndex++;
        if (currentImageIndex >= DataModel.getInstance().getDropFilePaths().size()) {
            currentImageIndex = 0; // loop back to the first image if we're at the end
        }
        try {
            updateImagePreview();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void handlePrevImage() {
        currentImageIndex--;
        if (currentImageIndex < 0) {
            currentImageIndex = DataModel.getInstance().getDropFilePaths().size() - 1; // loop back to the last image if we're at the beginning
        }
        try {
            updateImagePreview();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void updateImagePreview() throws  MalformedURLException {
        File fileImage = DataModel.getInstance().getDropFilePaths().get(currentImageIndex);
        Image image;
        image = new Image(fileImage.toURI().toURL().toString());
        imagePreview.setImage(image);

    }



    // Method to update the watermark based on the current settings
    @FXML
    public void applyWatermark() {

        String watermarkText = textField.getText();
        if (watermarkText.isEmpty()) {
            return;
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
    }


    @FXML
    public void resetWatermark() {
        DataModel dataModel = DataModel.getInstance();
        if (dataModel.getDropFilePaths() != null) {
            String fileImage = dataModel.getDropFilePaths().get(0).toString();
            if (fileImage != null) {
                File file = new File(fileImage);
                if (file.exists()) {
                    try {
                        // Reload the original image
                        Image image = new Image(new File(fileImage).toURI().toURL().toString());

                        // Set the loaded image to the ImageView
                        imagePreview.setImage(image);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (RuntimeException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("File does not exist: " + fileImage);
                }
            } else {
                System.out.println("File path is null or not set.");
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


}