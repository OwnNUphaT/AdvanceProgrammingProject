package com.advanceprogramproject.model;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static File saveImageToFile(Image image, String selectedFormat) throws IOException {
        File file = File.createTempFile("edited_image_" + System.currentTimeMillis(), "." + selectedFormat);

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ImageIO.write(bufferedImage, selectedFormat, file);

        return file;
    }
}
