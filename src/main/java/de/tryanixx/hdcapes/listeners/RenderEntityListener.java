package de.tryanixx.hdcapes.listeners;

import de.tryanixx.hdcapes.HDCapes;
import net.labymod.api.events.RenderEntityEvent;
import net.minecraft.entity.Entity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class RenderEntityListener implements RenderEntityEvent {
    @Override
    public void onRender(Entity entity, double v, double v1, double v2, float v3) {
        if (HDCapes.getInstance().getFetchedUsers().containsKey(entity.getUniqueID())) {
            if (!HDCapes.getInstance().getFetchedUsers().get(entity.getUniqueID())) {
                HDCapes.getInstance().getExecutorService().execute(() -> {
                    URL url = null;
                    try {
                        url = new URL("http://tryanixxaddons.de.cool/hdcapes/capes/" + entity.getUniqueID() + ".png");
                        BufferedImage img = ImageIO.read(url);
                        HDCapes.getInstance().getHdCapesManager().getTextureQueue().put(entity.getUniqueID(), img);
                        HDCapes.getInstance().getFetchedUsers().replace(entity.getUniqueID(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
