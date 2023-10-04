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
                                dataModel.setFileData(fileName, file.getAbsolutePath());

                                // Add the file names to the inputListView
                                List<String> extractedFiles = null;
                                try {
                                    extractedFiles = unzip(file.getAbsolutePath());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                importListView.getItems().addAll(extractedFiles);
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
                                dataModel.setFileData(fileName, file.getAbsolutePath());

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
                dataModel.setFileData(fileName, selectedFile.getAbsolutePath());

                // Check if there is at least one ZIP file
                boolean isZipFile = fileName.toLowerCase().endsWith(".zip");

                if (isZipFile) {
                    try {
                        // Unzip the file
                        List<String> extractedFiles = unzip(selectedFile.getAbsolutePath());
                        importListView.getItems().addAll(extractedFiles);

                        // ... (remaining code)
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
    public static List<String> unzip(String zipFilePath) throws IOException {
        List<String> extractedFiles = new ArrayList<>();
        byte[] buffer = new byte[1024];

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipInputStream.getNextEntry();

            while (entry != null) {
                // Assuming you want to display the file name without the directory structure
                String fileName = new File(entry.getName()).getName();
                extractedFiles.add(fileName);

                // You can add the file name directly to your importListView here
                // importListView.getItems().add(fileName);

                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
        }

        return extractedFiles;
    }

}
