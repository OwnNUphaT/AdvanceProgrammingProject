package com.advanceprogramproject.control;

import com.advanceprogramproject.Launcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.ArrayList;
import java.util.HashMap;

public class MainViewController {
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

    public void initialize() {
        // Setting OnDragOver
        importListView.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".png") || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpg");
            if (db.hasString() && isAccepted) {
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

        //TODO: Goes to the imported-page.fxml when nextBtn is push.
        nextBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("@views/imported-page.fxml"));
                Parent root = loader.load();
                // Pass the current stage reference to the new controller
                ImportPageController importPageController = loader.getController();
                importPageController.setStage(stage);

                scene = new Scene(root);
                stage.setScene(scene);

                stage.hide();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        //Let the user be able to choose a file within the browser to choose the file
    }
}
