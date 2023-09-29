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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private Slider rotationSlider;
    @FXML
    private Button BackBtnText;
    @FXML
    private ImageView imagePreview;
    @FXML
    private TextField textField;
    @FXML
    private Button applyWatermarkButton;
    @FXML
    private ChoiceBox<String> fontDrop;
    @FXML
    private ChoiceBox alignmentDrop;


    private Scene scene;
    int percent;

    @FXML
    public void handleDownloadAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Choose the directory for the file
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(imagePreview.getImage(), null), "png", file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get a list of all available font families
        List<String> fontFamilies = Font.getFamilies();
        fontDrop.getItems().addAll(fontFamilies);

        // Textfield for watermark text
        textField.textProperty().addListener((observable, oldValue, newValue) -> updateWatermark());

        // Font choice box
        fontDrop.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateWatermark());

        // Adding the alignment Choice Box
        String[] alignmentList = {"Top Left", "Center", "Top Right", "Bottom Left", "Bottom Right"};
        alignmentDrop.getItems().addAll(alignmentList);

        // Rotation Slider
        rotationSlider.setShowTickLabels(true);
        rotationSlider.setShowTickMarks(true);

        // Set the major tick unit (step)
        rotationSlider.setMajorTickUnit(10);
        rotationSlider.setMinorTickCount(0);

        // Display the value in SizeLabel
        rotationSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                SizeLabel.setText("Size: " + newValue.intValue());
                updateWatermark();
            }
        });




        //Visibility slider percentage.
        VisibilitySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                percent = (int) VisibilitySlider.getValue();
                VisibilityLabel.setText(Integer.toString(percent) + "%");
            }
        });


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

        }
    }
    // Method to update the watermark based on the current settings
    private void updateWatermark() {
        String watermarkText = textField.getText();
        if (watermarkText.isEmpty()) {
            return;
        }

        // Get the selected font from the ChoiceBox
        String selectedFont = fontDrop.getValue();
        if (selectedFont == null) {
            // If no font is selected, use a default font
            selectedFont = "Arial";
        }

        Image originalImage = imagePreview.getImage();
        Canvas canvas = new Canvas(originalImage.getWidth(), originalImage.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw the original image
        gc.drawImage(originalImage, 0, 0);

        // Set up graphics context for watermarking
        gc.setFont(new Font(selectedFont, 350)); // Use the selected font here
        gc.setFill(Color.BLACK);
        gc.setGlobalAlpha(0.5);

        Text textNode = new Text(watermarkText);
        textNode.setFont(new Font(selectedFont, 100)); // Use the selected font here
        double textWidth = textNode.getLayoutBounds().getWidth();
        double textHeight = textNode.getLayoutBounds().getHeight();

        // Get rotation value from the slider
        double rotationValue = rotationSlider.getValue();

        // Set rotation - Translate to center, rotate, then translate back
        gc.translate(originalImage.getWidth() / 2, originalImage.getHeight() / 2);
        gc.rotate(rotationValue);  // Use the rotation value from the slider
        gc.fillText(watermarkText, -textWidth, textHeight);


        WritableImage watermarkedImage = canvas.snapshot(null, null);
        imagePreview.setImage(watermarkedImage);
    }
}
