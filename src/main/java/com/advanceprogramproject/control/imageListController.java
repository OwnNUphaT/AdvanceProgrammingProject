package com.advanceprogramproject.control;

import com.advanceprogramproject.model.DataModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class imageListController implements Initializable {
    private Stage stage;
    @FXML
    private ListView imageLists;
    @FXML
    private Button startBtn;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        DataModel dataModel = DataModel.getInstance();
        List<File> droppedFiles = dataModel.getDropFilePaths();

        if (droppedFiles != null && !droppedFiles.isEmpty()) {
            for (File file : droppedFiles) {
                imageLists.getItems().add(file.getName());
            }
        }



        imageLists.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                // Get the selected item
                String selected = (String) imageLists.getSelectionModel().getSelectedItem();

                // Set the selected file in the DataModel
                dataModel.setSelectedFile(selected);

                // Move to the next page
                toNextPage();
            }
        });



        //Next Page
        startBtn.setOnAction(event -> toNextPage());



    }

    public void toNextPage() {
        try{
            //Closing the current scene
            stage.close();

            //Loading new fxml file
            FXMLLoader loader = new FXMLLoader(ImportPageController.class.getResource("/com/advanceprogramproject/views/image-page.fxml"));
            Parent root = loader.load();

            // Pass the current stage reference to the new controller
            imagePageController controller = loader.getController();
            controller.setStage(stage);


            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
