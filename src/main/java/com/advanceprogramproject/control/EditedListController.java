package com.advanceprogramproject.control;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditedListController implements Initializable {
    private static List<String> savedFiles = new ArrayList<>();

    public static void addSavedFile(String filePath) {
        savedFiles.add(filePath);
    }

    public static List<String> getSavedFiles() {
        return savedFiles;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
