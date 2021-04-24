package de.tryanixx.hdcapes.utils;

import com.mojang.authlib.exceptions.AuthenticationException;
import de.tryanixx.hdcapes.HDCapes;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {

    public static BufferedImage getScaledImage(BufferedImage src) {
        int w = HDCapes.MAX_WIDTH;
        int h = HDCapes.MAX_HEIGHT;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int x, y;
        int ww = src.getWidth();
        int hh = src.getHeight();
        for (x = 0; x < w; x++) {
            for (y = 0; y < h; y++) {
                int col = src.getRGB(x * ww / w, y * hh / h);
                img.setRGB(x, y, col);
            }
        }
        return img;
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

    public static BufferedImage parseImage(BufferedImage img, boolean parseImage) {
        if (img != null && parseImage) {
            int imageWidth = 64;
            int imageHeight = 32;

            BufferedImage srcImg = img;
            int srcWidth = srcImg.getWidth();
            int srcHeight = srcImg.getHeight();
            while ((imageWidth < srcWidth) || (imageHeight < srcHeight)) {
                imageWidth *= 2;
                imageHeight *= 2;
            }
            BufferedImage imgNew = new BufferedImage(imageWidth, imageHeight, 2);
            Graphics g = imgNew.getGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            return imgNew;
        }
        return img;
    }

    public static boolean authenticate() {
        Minecraft mc = Minecraft.getMinecraft();
        Session session = mc.getSession();
        if (session == null) {
            return false;
        }
        try {
            mc.getSessionService().joinServer(session.getProfile(), session.getToken(), "5b65c3ce12db8a41cb2a69be14d51b30b75698d8");
            return true;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getURLContent(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
        con.setConnectTimeout(5000);
        con.setRequestProperty("User-Agent", HDCapes.USER_AGENT);
        con.connect();
        return IOUtils.toString(con.getInputStream(), "UTF-8");
    }
}
