package com.advanceprogramproject.control;

import com.advanceprogramproject.model.DataModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainViewController implements Initializable {
    private HashMap<String, String> fileMap = new HashMap<>();
    @FXML
    private ListView<String> importListView;
    @FXML
    private ImageView importImage;
    @FXML
    private Label importLabel;
    @FXML
    private Button nextBtn;
    @FXML
    private Button chooseFileBtn;

    public ArrayList<String> imagesFile = new ArrayList<>();
    private DataModel dataModel = new DataModel();
    private Stage stage;
    private Scene scene;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel; // Set the shared DataModel
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setting OnDragOver
        importListView.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".png") || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpg") || db.getFiles().get(0).getName().toLowerCase().endsWith(".zip");
            if (db.hasFiles() && isAccepted) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            } else {
                event.consume();
            }
        });

        // Setting OnDragDropped
        importListView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                List<File> files = db.getFiles();

                // Check if there is at least one ZIP file
                boolean hasZipFile = files.stream()
                        .anyMatch(file -> file.getName().toLowerCase().endsWith(".zip"));

                // If there is a ZIP file, unzip it and display all files in the importListView
                if (hasZipFile) {
                    files.stream()
                            .filter(file -> file.getName().toLowerCase().endsWith(".zip"))
                            .forEach(file -> {
                                String fileName = file.getName(); // Extract only the file name
                                System.out.println("File path set: " + file.getAbsolutePath());

                                // Set the file path in the dataModel
                                DataModel dataModel = DataModel.getInstance();
                                dataModel.addDropFilePath(file);
                                dataModel.setFileName(fileName);

                                // Add the file name to the inputListView and the absolute path to the list
                                fileMap.put(fileName, file.getAbsolutePath());

                                try {
                                    // Unzip the file
                                    unzip(file.getAbsolutePath(), "path/to/extract/folder");

                                    // Display all files in the importListView
                                    Path extractPath = Paths.get("path/to/extract/folder");
                                    List<String> extractedFiles = Files.walk(extractPath)
                                            .filter(path -> Files.isRegularFile(path) && !path.getFileName().toString().endsWith(".zip"))
                                            .map(Path::getFileName)
                                            .map(Path::toString)
                                            .collect(Collectors.toList());

                                    extractedFiles.forEach(fileName1 -> {
                                        System.out.println("File path set: " + extractPath.resolve(fileName1));
                                        importListView.getItems().add(fileName1);

                                        // Set the file path in the dataModel
                                        dataModel.addDropFilePath(extractPath.resolve(fileName1).toFile());
                                        dataModel.setFileName(fileName1);

                                        // Add the file name to the inputListView and the absolute path to the list
                                        fileMap.put(fileName1, extractPath.resolve(fileName1).toString());
                                    });

                                } catch (IOException e) {
                                    e.printStackTrace(); // Handle the exception appropriately
                                }
                            });

                    // Make the importImage and label disappear
                    importImage.setVisible(false);
                    importLabel.setVisible(false);

                    event.setDropCompleted(true);
                } else {
                    // If there is no ZIP file, process single image files
                    files.stream()
                            .filter(file -> file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg"))
                            .forEach(file -> {
                                String fileName = file.getName(); // Extract only the file name
                                importListView.getItems().add(fileName);
                                System.out.println("File path set: " + file.getAbsolutePath());

                                // Set the file path in the dataModel
                                DataModel dataModel = DataModel.getInstance();
                                dataModel.addDropFilePath(file);
                                dataModel.setFileName(fileName);

                                // Add the file name to the inputListView and the absolute path to the list
                                fileMap.put(fileName, file.getAbsolutePath());
                            });

                    // Make the importImage and label disappear
                    importImage.setVisible(false);
                    importLabel.setVisible(false);

                    event.setDropCompleted(true);
                }
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });

        chooseFileBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose an Image File");

            // Set the file extension filters if needed (e.g., for images)
            FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.zip");
            fileChooser.getExtensionFilters().add(imageFilter);

            // Show the file dialog and get the selected file
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                String fileName = selectedFile.getName(); // Extract only the file name
                importListView.getItems().add(fileName);
                System.out.println("File path set: " + selectedFile.getAbsolutePath());

                // Hidden the ImageImport
                importLabel.setVisible(false);
                importImage.setVisible(false);

                // Set the file path in the dataModel
                DataModel dataModel = DataModel.getInstance();
                dataModel.addDropFilePath(selectedFile);
                dataModel.setFileName(fileName);

                // Check if there is at least one ZIP file
                boolean isZipFile = fileName.toLowerCase().endsWith(".zip");

                // If there is a ZIP file, unzip it and display all files in the importListView
                // If there is a ZIP file, unzip it and display all files in the importListView
                if (isZipFile) {
                    try {
                        // Unzip the file
                        unzip(selectedFile.getAbsolutePath(), "path/to/extract/folder");

                        // Display all files in the importListView
                        Path extractPath = Paths.get("path/to/extract/folder");
                        List<String> extractedFiles = Files.walk(extractPath)
                                .filter(path -> Files.isRegularFile(path) && !path.getFileName().toString().endsWith(".zip"))
                                .map(Path::getFileName)
                                .map(Path::toString)
                                .collect(Collectors.toList());

                        extractedFiles.forEach(fileName1 -> {
                            System.out.println("File path set: " + extractPath.resolve(fileName1));
                            importListView.getItems().add(fileName1);

                            // Set the file path in the dataModel
                            dataModel.addDropFilePath(extractPath.resolve(fileName1).toFile());
                            // Remove the original ZIP file from the importListView
                            importListView.getItems().remove(fileName);
                            dataModel.setFileName(fileName1);

                            // Add the file name to the inputListView and the absolute path to the list
                            fileMap.put(fileName1, extractPath.resolve(fileName1).toString());
                        });



                    } catch (IOException e) {
                        e.printStackTrace(); // Handle the exception appropriately
                    }
                }
            }
        });


        // Goes to the imported-page.fxml when nextBtn is push.
        nextBtn.setOnAction(event -> {
            try {
                stage.close(); //Close Previous Stage

                FXMLLoader loader = new FXMLLoader(MainViewController.class.getResource("/com/advanceprogramproject/views/imported-page.fxml"));
                Parent root = loader.load();

                // Pass the current stage reference to the new controller
                ImportPageController importPageController = loader.getController();
                importPageController.setStage(stage); // new stage

                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public static void unzip(String zipFilePath, String extractFolderPath) throws IOException {
        byte[] buffer = new byte[1024];

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipInputStream.getNextEntry();

            while (entry != null) {
                String entryPath = extractFolderPath + File.separator + entry.getName();
                File entryFile = new File(entryPath);

                // Ensure that the parent directories exist
                File parent = entryFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }


                if (entry.isDirectory()) {
                    entryFile.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(entryFile)) {
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }

                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
        }
    }
}
