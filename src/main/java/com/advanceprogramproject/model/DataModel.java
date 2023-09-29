package com.advanceprogramproject.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private static DataModel instance;
    private List<File> dropFilePaths = new ArrayList<>();
    private String selected;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

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

    public void setSelectedFile(String selected) {
        this.selected = selected;
    }

    public String getSelected() {
        return selected;
    }

    public void setDropFilePaths(List<File> dropFilePaths) {
        this.dropFilePaths = dropFilePaths;
    }

    public void addDropFilePath(File dropFilePath) {
        this.dropFilePaths.add(dropFilePath);
    }
}
