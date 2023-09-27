package com.advanceprogramproject.model;

import java.io.File;

public class newFilegenrate {
    public String generateUniqueFileName(File directory, String originalFileName) {
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        int counter = 1;
        String uniqueFileName = originalFileName;

        while (new File(directory, uniqueFileName).exists()) {
            uniqueFileName = baseName + "_" + counter + extension;
            counter++;
        }

        return uniqueFileName;
    }

}
