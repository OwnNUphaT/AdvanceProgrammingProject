package com.advanceprogramproject;

import com.advanceprogramproject.control.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {
    @Override
    public void start(Stage primarystage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("views/main-view.fxml"));
        Parent root = fxmlLoader.load();

        MainViewController controller = fxmlLoader.getController();
        controller.setStage(primarystage);

        Scene scene1 = new Scene(root, 930, 480);

        //Attach the icon to the stage/window
        primarystage.getIcons().add(new Image(Launcher.class.getResourceAsStream("images/logo_size.jpg")));


        primarystage.setResizable(false);
        primarystage.setTitle("OPCODE Watermark");//kuy
        primarystage.setScene(scene1);
        primarystage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}