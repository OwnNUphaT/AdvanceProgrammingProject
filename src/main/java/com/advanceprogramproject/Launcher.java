package com.advanceprogramproject;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {
    @FXML
    private Pane importPane;
    @Override
    public void start(Stage primarystage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("main-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 930, 480);

        // Setting border radius


        primarystage.setResizable(false);
        primarystage.setTitle("Watermark");
        primarystage.setScene(scene);
        primarystage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}