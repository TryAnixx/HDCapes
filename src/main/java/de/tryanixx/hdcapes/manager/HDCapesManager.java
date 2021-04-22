package de.tryanixx.hdcapes.manager;

import de.tryanixx.hdcapes.HDCapes;
import de.tryanixx.hdcapes.utils.CustomImage;
import de.tryanixx.hdcapes.utils.RequestAPI;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.labymod.user.cosmetic.custom.handler.CloakImageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class HDCapesManager {

    private HashMap<UUID, BufferedImage> textureQueue = new HashMap<>();

    private boolean queueing;

    private boolean backup;
    private ResourceLocation originalcape;
    private int ticker;
    private User user;

    @SubscribeEvent
    public void handle(TickEvent.ClientTickEvent event) {
        ticker++;
        if(ticker % 30 == 0) HDCapes.getInstance().getCooldownManager().update();
        if (ticker % 40 != 0) return;
        if (ticker == 40) {
            user = LabyMod.getInstance().getUserManager().getUser(LabyMod.getInstance().getPlayerUUID());
        } else if (!LabyMod.getInstance().getUserManager().getUser(user.getUuid()).equals(user)) {
            user = LabyMod.getInstance().getUserManager().getUser(LabyMod.getInstance().getPlayerUUID());
            HDCapes.getInstance().getFetchedUsers().clear();
            RequestAPI.fetchAndCacheUser();
            queueing = false;
        }
        if (textureQueue.isEmpty()) return;
        if (queueing) return;
        queueing = true;
        Iterator<Map.Entry<UUID, BufferedImage>> it = textureQueue.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, BufferedImage> pair = it.next();
            BufferedImage img = pair.getValue();
            UUID keyUUID = pair.getKey();
            if (setCape(img, keyUUID.toString())) {
                CloakImageHandler cloakImageHandler = LabyMod.getInstance().getUserManager().getCosmeticImageManager().getCloakImageHandler();
                if (!cloakImageHandler.getResourceLocations().containsKey(keyUUID))
                    continue;
                if (!backup && LabyMod.getInstance().getPlayerUUID().equals(keyUUID)) {
                    backup = true;
                    originalcape = cloakImageHandler.getResourceLocations().get(keyUUID);
                }
                cloakImageHandler.getResourceLocations().put(keyUUID, new ResourceLocation("hdcapes/" + keyUUID.toString()));
            }
            it.remove();
        }
        queueing = false;
    }

    public boolean setCape(BufferedImage img, String uuid) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        ResourceLocation loc = new ResourceLocation("hdcapes/" + uuid);
        ITextureObject old = textureManager.getTexture(loc);
        if (old instanceof CustomImage && ((CustomImage) old).isLoaded()) {
            ((CustomImage) old).deleteGlTexture();
        }
        CustomImage textureCosmetic = new CustomImage(loc, img);
        boolean sucess = textureManager.loadTexture(loc, textureCosmetic);
        return sucess;
    }

    public HashMap<UUID, BufferedImage> getTextureQueue() {
        return textureQueue;
    }

    public void reset() {
        CloakImageHandler cloakImageHandler = LabyMod.getInstance().getUserManager().getCosmeticImageManager().getCloakImageHandler();
        if (originalcape != null) {
            cloakImageHandler.getResourceLocations().put(LabyMod.getInstance().getPlayerUUID(), originalcape);
        }
    }
    public void deleteCape() {
        reset();
        RequestAPI.deleteCape();
    }

    public void uploadCapeTexture() {
        if (HDCapes.getInstance().getTempFile() == null) {
            HDCapes.getInstance().getExecutorService().execute(() -> {
                JOptionPane.showMessageDialog(null, "Please insert your texture again!", "HDCapes", JOptionPane.ERROR_MESSAGE);
            });
            return;
        }
        RequestAPI.upload();
    }
}
