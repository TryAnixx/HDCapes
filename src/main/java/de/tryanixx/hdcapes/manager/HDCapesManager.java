package de.tryanixx.hdcapes.manager;

import de.tryanixx.hdcapes.utils.CustomImage;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

public class HDCapesManager {

    private HashMap<UUID, BufferedImage> textureQueue = new HashMap<>();
    private String lastErrorMessage;

    @SubscribeEvent
    public void handle(TickEvent.ClientTickEvent event) {
        if(lastErrorMessage != null) {
            LabyMod.getInstance().getGuiCustomAchievement().displayAchievement("HDCapes", lastErrorMessage);
            LabyMod.getInstance().getGuiCustomAchievement().updateAchievementWindow();
            lastErrorMessage = null;
        }
        if(textureQueue.isEmpty()) return;
        for(UUID key : textureQueue.keySet()) {
            BufferedImage img = textureQueue.get(key);
            if(setCape(img, key.toString())) {
                LabyMod.getInstance().getUserManager().getCosmeticImageManager().getCloakImageHandler().getResourceLocations().put(key, new ResourceLocation("capes/" + key.toString()));
            }
        }
        textureQueue.clear();
    }
    private boolean setCape(BufferedImage img, String uuid) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        ResourceLocation loc = new ResourceLocation("capes/" + uuid);
        ITextureObject old = textureManager.getTexture(loc);
        if (old instanceof CustomImage && ((CustomImage) old).isLoaded()) {
            ((CustomImage) old).deleteGlTexture();
            old = null;
        }
        BufferedImage image = parseImage(img, true);
        CustomImage textureCosmetic = new CustomImage(loc, image);
        return textureManager.loadTexture(loc, textureCosmetic);
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

    public HashMap<UUID, BufferedImage> getTextureQueue() {
        return textureQueue;
    }

    public void setLastErrorMessage(String lastErrorMessage) {
        this.lastErrorMessage = lastErrorMessage;
    }
}
