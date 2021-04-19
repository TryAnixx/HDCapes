package de.tryanixx.hdcapes.utils;

import de.tryanixx.hdcapes.HDCapes;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Utils {
    public static BufferedImage getScaledImage(BufferedImage srcImg) {
        Image image = srcImg.getScaledInstance(HDCapes.MAX_WIDTH, HDCapes.MAX_HEIGHT, Image.SCALE_SMOOTH);

        return toBufferedImage(image);
    }

    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }
}
