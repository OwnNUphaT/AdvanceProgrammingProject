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
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    private static List<File> savedFiles = new ArrayList<>();

    public static void addSavedFile(File filePath) {
        savedFiles.add(filePath);
    }

    public static List<File> getSavedFiles() {
        return savedFiles;
    }

    private Stage stage;

    @FXML
    private ListView downloadList;

    @FXML
    private Button downloadBtn;

    @FXML
    private ImageView backIcon;

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

        downloadBtn.setOnAction(event -> downloadSelectedFiles());
    }




    private void downloadSelectedFiles() {
        List<File> allFiles = new ArrayList<>();

        for (String filePath : fileMap.values()) {
            allFiles.add(new File(filePath));
        }

        if (allFiles.isEmpty()) {
            showAlert("Error", "No files available for download.");
            return;
        }

        if (allFiles.size() > 1) {
            // Download as a zip file
            downloadAsZip(allFiles);
        } else {
            // Download single file
            downloadFile(allFiles.get(0));
        }
    }



    private void downloadAsZip(List<File> files) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Save ZIP File");

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            String zipFilePath = selectedDirectory.getAbsolutePath() + File.separator + "downloaded_files.zip";

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    try (FileOutputStream fos = new FileOutputStream(zipFilePath);
                         ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                        for (File file : files) {
                            try (FileInputStream fis = new FileInputStream(file)) {
                                ZipEntry zipEntry = new ZipEntry(file.getName());
                                zipOut.putNextEntry(zipEntry);

                                byte[] bytes = new byte[1024];
                                int length;
                                while ((length = fis.read(bytes)) >= 0) {
                                    zipOut.write(bytes, 0, length);
                                }

                                zipOut.closeEntry();
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            task.setOnSucceeded(workerStateEvent -> showAlert("Success", "ZIP file downloaded successfully."));

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void downloadFile(File file) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        DataModel dataModel = new DataModel();

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
        file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                Image resizedImage = dataModel.getEditedImage();

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
