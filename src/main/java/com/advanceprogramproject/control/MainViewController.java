package com.advanceprogramproject.control;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;

public class MainViewController {
    @FXML
    private Pane importPane;

    public void initialize() {

        // TODO: Setting OnDragOver and OnDragDropped when the file have been use
        importPane.setOnDragDropped(event -> {

        });

        // TODO: Let the user able to choose within the browser to choose the file

        //TODO: after the user have input the file into the list view the import icons will be disappear
    }
}
