package com.advanceprogramproject.control;

import com.advanceprogramproject.model.DataModel;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EditedListController implements Initializable {
    private static HashMap<String, String> fileMap = new HashMap<>();

    private Stage stage;
    private DataModel dataModel;

    @FXML
    private ListView downloadList;

    @FXML
    private Button downloadBtn;

    @FXML
    private ImageView backIcon;
    @FXML
    private Button clearBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO: Load up the list for saved file with the selected format

        backIcon.setOnMouseClicked(event -> {
            try {
                stage.close();

                FXMLLoader loader = new FXMLLoader(EditedListController.class.getResource("/com/advanceprogramproject/views/image-page.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);

                ImagePageController controller = loader.getController();
                controller.setStage(stage);

                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        clearBtn.setOnAction(event -> {
            downloadList.getItems().clear();
        });

        downloadBtn.setOnAction(event -> download());
    }

    public void setDataModel(DataModel dataModel) {
        downloadList.getItems().clear();
        this.dataModel = dataModel;

        // Get the edited images from DataModel and add them to the ListView
        List<Image> editedImages = dataModel.getEditedImage();

        // Save the edited images to files
        if (editedImages != null && !editedImages.isEmpty()) {
            try {
                for (Image image : editedImages) {
                    File editedImageFile = saveImageToFile(image);
                    // Add the file to the ListView
                    downloadList.getItems().add(editedImageFile.getName());
                    // Add the file path to the map
                    fileMap.put(editedImageFile.getName(), editedImageFile.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Method to save an Image to a File with the selected format
    private File saveImageToFile(Image image) throws IOException {
        String selectedFormat = dataModel.getSelectedFormat(); // Assuming selectedFormat is a String like "jpg" or "png"
        File file = File.createTempFile("edited_image_" + System.currentTimeMillis(), "." + selectedFormat);

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ImageIO.write(bufferedImage, selectedFormat, file);

        return file;
    }

    private void download() {
        List<String> savedFile = downloadList.getItems();

        if (savedFile.isEmpty()) {
            showAlert("No Files Selected", "Please select files to download.");
            return;
        }

        if (savedFile.size() > 1) {
            downloadFilesAsZip(savedFile);
        } else {
            downloadFiles();
        }
    }


    private void downloadFiles() { // Download the image.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        // Selected format
        String selectedFormat = dataModel.getSelectedFormat();


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
                List<Image> editedImages  = dataModel.getEditedImage();

                if (editedImages  != null  && !editedImages.isEmpty()) {
                    // Iterate over each edited image and save it
                    for (Image resizedImage : editedImages) {
                        BufferedImage originalImage = SwingFXUtils.fromFXImage(resizedImage, null);

                        // Create a new BufferedImage with the correct color model for JPEG
                        BufferedImage convertedImage = new BufferedImage(
                                originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB
                        );
                        convertedImage.createGraphics().drawImage(originalImage, 0, 0, Color.WHITE, null);

                        // Generate a unique file name based on timestamp
                        String fileName = "edited_image_" + System.currentTimeMillis() + "." + selectedFormat.toLowerCase();

                        // Save the image in the selected format
                        if ("JPG".equals(selectedFormat)) {
                            ImageIO.write(convertedImage, "jpg", new File(file.getParentFile(), fileName));
                        } else if ("PNG".equals(selectedFormat)) {
                            ImageIO.write(convertedImage, "png", new File(file.getParentFile(), fileName));
                        }
                    }

                    showAlert("Success", "Images are Saved!");
                } else {
                    showAlert("Error", "No images selected to save.");
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

    private void downloadFilesAsZip(List<String> filePaths) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("downloaded_files.zip");

        File zipFile = fileChooser.showSaveDialog(stage);

        if (zipFile != null) {
            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                for (String filePath : filePaths) {
                    File file = new File(filePath);

                    if (!file.exists()) {
                        showAlert("Error", "File not found: " + filePath);
                        return;  // Stop processing if a file is not found
                    }

                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, length);
                        }
                    }

                    zos.closeEntry();
                }

                showAlert("Download Successful", "Files downloaded as a zip successfully.");

            } catch (IOException e) {
                showAlert("Error", "Error downloading files as zip: " + e.getMessage());
            }
        }
    }



    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}