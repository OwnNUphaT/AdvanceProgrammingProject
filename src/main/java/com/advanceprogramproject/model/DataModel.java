package com.advanceprogramproject.model;

import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataModel {
    private static DataModel instance;
    private List<File> dropFilePaths = new ArrayList<>();
    private File selected;
    private Image editedImage;

    public Image getEditedImage() {
        return editedImage;
    }

    public void setEditedImage(Image editedImage) {
        this.editedImage = editedImage;
    }



    public String getSelectedFormat() {
        return selectedFormat;
    }

    public void setSelectedFormat(String selectedFormat) {
        this.selectedFormat = selectedFormat;
    }

    private String selectedFormat;
    private Map<String, String> fileData = new HashMap<>();


    public DataModel() {
        // Private constructor to prevent external instantiation
    }

    public static DataModel getInstance() {
        if (instance == null) {
            instance = new DataModel();
        }
        return instance;
    }

    public List<File> getDropFilePaths() {
        return dropFilePaths;
    }

    public void setSelectedFile(File selected) {
        this.selected = selected;
    }

    public File getSelected() {
        return selected;
    }

    public void setDropFilePaths(List<File> dropFilePaths) {
        this.dropFilePaths = dropFilePaths;
    }

    public void addDropFilePath(File dropFilePath) {
        this.dropFilePaths.add(dropFilePath);
    }

    // New methods to store and retrieve file data
    public void setFileData(String fileName, String filePath) {
        fileData.put(fileName, filePath);
    }

    public Map<String, String> getFileData() {
        return fileData;
    }

    public void setFileName(String fileName) {
    }
}
