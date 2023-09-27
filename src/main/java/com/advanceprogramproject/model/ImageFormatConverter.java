package com.advanceprogramproject.model;

import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageFormatConverter {
    public static void convertImage(Image image, String outputFormat, File outputFile) throws IOException {
        BufferedImage bufferedImage = convertFXImageToBufferedImage(image);

        // Determine the output format based on the user's choice
        String format = outputFormat.equalsIgnoreCase("PNG") ? "png" : "jpg";

        // Write the BufferedImage to the output file
        ImageIO.write(bufferedImage, format, outputFile);
    }

    // Helper method to convert JavaFX Image to BufferedImage
    public static BufferedImage convertFXImageToBufferedImage(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] buffer = new int[width * height];
        image.getPixelReader().getPixels(0, 0, width, height, javafx.scene.image.PixelFormat.getIntArgbInstance(), buffer, 0, width);

        bufferedImage.setRGB(0, 0, width, height, buffer, 0, width);

        return bufferedImage;
    }
}
