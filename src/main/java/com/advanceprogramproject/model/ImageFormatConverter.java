package com.advanceprogramproject.model;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ImageFormatConverter {
    public static String convertAndSaveImage(Image image, String format) {
        try {
            // Create a unique filename using UUID
            String fileName = UUID.randomUUID().toString() + "." + format.toLowerCase();

            // Convert JavaFX Image to BufferedImage
            BufferedImage bufferedImage = convertFXImageToBufferedImage(image);

            // Create a file and save the image with the specified format
            File file = new File(fileName);
            ImageIO.write(bufferedImage, format, file);

            return fileName; // Return the generated filename
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null in case of an error
        }
    }

    // Helper method to convert JavaFX Image to BufferedImage
    private static BufferedImage convertFXImageToBufferedImage(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        byte[] buffer = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

        // Get the pixels from the JavaFX Image
        image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(), buffer, 0, width * 4);

        return bufferedImage;
    }
}
