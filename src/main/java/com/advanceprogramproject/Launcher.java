package com.advanceprogramproject;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {
    @Override
    public void start(Stage primarystage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("main-view.fxml"));

        Scene scene1 = new Scene(fxmlLoader.load(), 930, 480);

        //Attach the icon to the stage/window
        primarystage.getIcons().add(new Image(Launcher.class.getResourceAsStream("images/logo_size.jpg")));

        primarystage.setResizable(false);
        primarystage.setTitle("OPCODE Watermark");
        primarystage.setScene(scene1);
        primarystage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}