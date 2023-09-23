package com.advanceprogramproject.control;

import com.advanceprogramproject.model.DataModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ImportPageController implements Initializable {
    private Stage stage;
    private Scene scene;

    @FXML
    private Button imageBtn;

    @FXML
    private Button textBtn;
    private DataModel dataModel;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Method to close the previous scene
    public void closePreviousScene() {
        if (stage != null) {
            stage.close();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageBtn.setOnAction(event -> {
            try{
                //Closing the current scene
                closePreviousScene();

                //Loading new fxml file
                FXMLLoader loader = new FXMLLoader(ImportPageController.class.getResource("/com/advanceprogramproject/views/image-page.fxml"));
                Parent root = loader.load();

                // Pass the current stage reference to the new controller
                imagePageController controller = loader.getController();
                controller.setStage(stage);


                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            }catch (IOException e) {
                e.printStackTrace();
            }
        });

        textBtn.setOnAction(event -> {
            try{
                //Closing the current scene
                closePreviousScene();

                //Loading new fxml file
                FXMLLoader loader = new FXMLLoader(ImportPageController.class.getResource("/com/advanceprogramproject/views/text-page.fxml"));
                Parent root = loader.load();

                // Pass the current stage reference to the new controller
                TextPageController controller = loader.getController();
                controller.setStage(stage);

                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            }catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }
}
