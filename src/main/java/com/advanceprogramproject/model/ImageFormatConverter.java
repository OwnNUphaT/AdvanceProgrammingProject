package com.advanceprogramproject.model;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageFormatConverter {
    public static BufferedImage convertImage(Image image) {
        // Convert JavaFX Image to BufferedImage
        BufferedImage bufferedImage = convertFXImageToBufferedImage(image);
        return bufferedImage;
    }



    // Helper method to convert JavaFX Image to BufferedImage
    public static BufferedImage convertFXImageToBufferedImage(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] buffer = new int[width * height];
        image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), buffer, 0, width);

        bufferedImage.setRGB(0, 0, width, height, buffer, 0, width);

        return bufferedImage;
    }

}
