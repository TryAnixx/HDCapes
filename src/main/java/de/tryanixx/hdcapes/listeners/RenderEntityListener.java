package de.tryanixx.hdcapes.listeners;

import de.tryanixx.hdcapes.HDCapes;
import net.labymod.api.events.RenderEntityEvent;
import net.labymod.main.LabyMod;
import net.minecraft.entity.Entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RenderEntityListener implements RenderEntityEvent {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Override
    public void onRender(Entity entity, double v, double v1, double v2, float v3) {
        if (HDCapes.getInstance().getFetchedUsers().containsKey(entity.getUniqueID())) {
            if (!HDCapes.getInstance().getFetchedUsers().get(entity.getUniqueID())) {
                if(!LabyMod.getInstance().getUserManager().isWhitelisted(entity.getUniqueID())) return;
                if(HDCapes.getInstance().isSeeowncapeonly()) {
                    if(entity.getUniqueID().equals(LabyMod.getInstance().getPlayerUUID())) {
                        HDCapes.getInstance().getFetchedUsers().replace(entity.getUniqueID(), true);
                        executorService.execute(() -> {
                            URL url = null;
                            try {
                                url = new URL("http://tryanixxaddons.de.cool/hdcapes/capes/" + entity.getUniqueID() + ".png");
                                BufferedImage img = ImageIO.read(url);
                                BufferedImage image = parseImage(img, true);
                                HDCapes.getInstance().getHdCapesManager().getTextureQueue().put(entity.getUniqueID(), image);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        return;
                    }
                }
                HDCapes.getInstance().getFetchedUsers().replace(entity.getUniqueID(), true);
                executorService.execute(() -> {
                    URL url = null;
                    try {
                        url = new URL("http://tryanixxaddons.de.cool/hdcapes/capes/" + entity.getUniqueID() + ".png");
                        BufferedImage img = ImageIO.read(url);
                        BufferedImage image = parseImage(img, true);
                        HDCapes.getInstance().getHdCapesManager().getTextureQueue().put(entity.getUniqueID(), image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
    protected BufferedImage parseImage(BufferedImage img, boolean parseImage) {
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
}
