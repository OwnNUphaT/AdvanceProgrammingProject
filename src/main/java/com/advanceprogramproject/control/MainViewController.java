package com.advanceprogramproject.control;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;

public class MainViewController {
    @FXML
    private Pane importPane;
    @FXML
    private ImageView importImage;
    @FXML
    private Label importLabel;
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
                }
                success = true;
                //after the user have input the file into the list view the import icons will be disappeared
                importImage.setVisible(false);
                importLabel.setVisible(false);
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // TODO: Let the user able to choose within the browser to choose the file



    }
}
