package de.tryanixx.hdcapes.listeners;

import de.tryanixx.hdcapes.HDCapes;
import net.labymod.api.events.RenderEntityEvent;
import net.minecraft.entity.Entity;

import javax.imageio.ImageIO;
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
                executorService.execute(() -> {
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
