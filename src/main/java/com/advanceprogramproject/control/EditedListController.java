package com.advanceprogramproject.control;

import com.advanceprogramproject.model.DataModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class EditedListController implements Initializable {
    private HashMap<String, String> fileMap = new HashMap<>();
    private static List<File> savedFiles = new ArrayList<>();

    public static void addSavedFile(File filePath) {
        savedFiles.add(filePath);
    }

    public static List<File> getSavedFiles() {
        return savedFiles;
    }
    public Stage stage;
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

            // Check if the first file is a ZIP file and remove it
            if (droppedFiles.get(0).getName().endsWith(".zip")) {
                String zipFileName = droppedFiles.get(0).getName();
                downloadList.getItems().remove(zipFileName);
                fileMap.remove(zipFileName);
            }
        }

        //Back to ImagePage
        backIcon.setOnMouseClicked(event -> {
            try{
                stage.close();

                FXMLLoader loader = new FXMLLoader(EditedListController.class.getResource("/com/advanceprogramproject/views/image-page.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);

                ImagePageController controller = loader.getController();
                controller.setStage(stage);

                stage.setScene(scene);
                stage.show();

            }catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
