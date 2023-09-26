package com.advanceprogramproject.model;

public class DataModel {
    private static DataModel instance;
    private String dropFilePath;

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

    public String getDropFilePath() {
        return dropFilePath;
    }

    public void setDropFilePath(String dropFilePath) {
        this.dropFilePath = dropFilePath;
    }
}
