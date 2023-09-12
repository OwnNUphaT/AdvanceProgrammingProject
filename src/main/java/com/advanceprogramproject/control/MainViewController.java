package com.advanceprogramproject.control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainViewController {
    @FXML
    private Pane importPane;
    @FXML
    private AnchorPane importPage;
    @FXML
    private ImageView importImage;
    @FXML
    private Label importLabel;

    public ArrayList<String> imagesFile;
    private Stage stage;
    private Scene scene;

    public void initialize() {
        // Setting OnDragOver
        importPane.setOnDragOver(event -> {
            if (event.getGestureSource() != importPane && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        // Setting OnDragDropped
        importPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                // Handle the dropped files here
                for (File file : db.getFiles()) {
                    // You can process the file here
                    System.out.println("Dropped file: " + file.getAbsolutePath());
                    imagesFile.add(file.getAbsolutePath());
                }
                success = true;
                // After the user has input the file into the list view, the import icons will be disappeared
                importImage.setVisible(false);
                importLabel.setVisible(false);
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // TODO: Let the user be able to choose a file within the browser to choose the file
    }
}