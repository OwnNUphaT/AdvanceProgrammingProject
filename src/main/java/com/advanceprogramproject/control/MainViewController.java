package com.advanceprogramproject.control;

import com.advanceprogramproject.Launcher;
import com.advanceprogramproject.model.FilePath;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

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

    public ArrayList<String> imagesFile = new ArrayList<>();
    private Stage stage;
    private Scene scene;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setting OnDragOver
        importListView.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".png") || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpg");
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
                int total_files = db.getFiles().size();
                for (int i = 0; i < total_files; i++) {
                    File file = db.getFiles().get(i);
                    String fileName = file.getName(); // Extract only the file name
                    importListView.getItems().add(fileName);

                    FilePath filePath = new FilePath();
                    filePath.setFile(file);
                    filePath.setFile(file);
                    System.out.println("File path set: " + file.getAbsolutePath());

                    // Add the file name to the inputListView and the absolute path to the list
                    fileMap.put(fileName, file.getAbsolutePath());
                    event.setDropCompleted(true);
                }

                //Make the importImage and label disappear
                importImage.setVisible(false);
                importLabel.setVisible(false);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();

        });

        // Goes to the imported-page.fxml when nextBtn is push.
        nextBtn.setOnAction(event -> {
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
                throw new RuntimeException(e);
            }
        });
        //hello
    }
}
