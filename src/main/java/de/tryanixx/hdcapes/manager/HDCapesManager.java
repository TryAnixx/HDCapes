package de.tryanixx.hdcapes.manager;

import de.tryanixx.hdcapes.utils.CustomImage;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class HDCapesManager {

    private HashMap<UUID, BufferedImage> textureQueue = new HashMap<>();

    private int ticker;
    private boolean queueing;

    @SubscribeEvent
    public void handle(TickEvent.ClientTickEvent event) {
        //TODO FIX QUOTE
      ticker++;
      if (ticker % 50 != 0) return;
      if (textureQueue.isEmpty()) return;
      if (queueing) return;
      queueing = true;
      Iterator<Map.Entry<UUID, BufferedImage>> it = textureQueue.entrySet().iterator();
      while (it.hasNext()) {
          Map.Entry<UUID, BufferedImage> pair = it.next();
          BufferedImage img = pair.getValue();
          UUID keyUUID = pair.getKey();
          if (setCape(img, keyUUID.toString())) {
              LabyMod.getInstance().getUserManager().getCosmeticImageManager().getCloakImageHandler().getResourceLocations().put(keyUUID, new ResourceLocation("capes/" + keyUUID.toString()));
          }
          it.remove();
      }
      queueing = false;
    }
    public boolean setCape(BufferedImage img, String uuid) {
        long start = System.currentTimeMillis();
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        ResourceLocation loc = new ResourceLocation("capes/" + uuid);
        ITextureObject old = textureManager.getTexture(loc);
        if (old instanceof CustomImage && ((CustomImage) old).isLoaded()) {
            ((CustomImage) old).deleteGlTexture();
            old = null;
        }
        CustomImage textureCosmetic = new CustomImage(loc, img);
        boolean sucess = textureManager.loadTexture(loc, textureCosmetic);
        long end = System.currentTimeMillis() - start;
        System.out.println(end + " UUID: " + uuid);
        return sucess;
    }

    public HashMap<UUID, BufferedImage> getTextureQueue() {
        return textureQueue;
    }
}
