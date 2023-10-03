package com.advanceprogramproject.control;

import com.advanceprogramproject.model.DataModel;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
        List<File> droppedFiles = getSavedFiles();

        if (droppedFiles != null && !droppedFiles.isEmpty()) {
            for (File file : droppedFiles) {
                String fileName = file.getName();
                downloadList.getItems().add(fileName);
                fileMap.put(fileName, file.getAbsolutePath());
            }

            if (droppedFiles.get(0).getName().endsWith(".zip")) {
                String zipFileName = droppedFiles.get(0).getName();
                downloadList.getItems().remove(zipFileName);
                fileMap.remove(zipFileName);
            }
        }

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

    public void updateListView() {
        downloadList.getItems().clear();
        fileMap.clear();

        List<File> droppedFiles = getSavedFiles();

        if (droppedFiles != null && !droppedFiles.isEmpty()) {
            for (File file : droppedFiles) {
                String fileName = file.getName();
                downloadList.getItems().add(fileName);
                fileMap.put(fileName, file.getAbsolutePath());
            }
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
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

        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            try {
                DataModel dataModel = DataModel.getInstance();
                dataModel.setSelectedFile(file);

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
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
