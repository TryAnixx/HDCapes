package de.tryanixx.hdcapes;

import de.tryanixx.hdcapes.authenticate.Authenticator;
import de.tryanixx.hdcapes.cooldown.CooldownManager;
import de.tryanixx.hdcapes.listeners.RenderEntityListener;
import de.tryanixx.hdcapes.manager.HDCapesManager;
import de.tryanixx.hdcapes.settingselements.ButtonElement;
import de.tryanixx.hdcapes.settingselements.DiscordElement;
import de.tryanixx.hdcapes.settingselements.PreviewElement;
import de.tryanixx.hdcapes.utils.FileChooser;
import de.tryanixx.hdcapes.utils.RequestAPI;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HDCapes extends LabyModAddon {

    private static HDCapes instance;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private HDCapesManager hdCapesManager;

    private Authenticator authenticator;
    private CooldownManager cooldownManager;

    public static final int MAX_HEIGHT = 1100;
    public static final int MAX_WIDTH = 1408;

    private File tempFile;

    private HashMap<UUID, Boolean> fetchedUsers = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        hdCapesManager = new HDCapesManager();
        authenticator = new Authenticator();
        cooldownManager = new CooldownManager();

        RequestAPI.fetchandcacheusers();

        api.getEventManager().register(new RenderEntityListener());
        api.registerForgeListener(hdCapesManager);
    }

    @Override
    public void loadConfig() {

    }

    @Override
    protected void fillSettings(List<SettingsElement> subSettings) {
        subSettings.add(new ButtonElement(1, "Choose texture (Offline-Preview)", new ControlElement.IconData(Material.ITEM_FRAME), "Click", false, buttonElement -> openFileDialog()));
        subSettings.add(new ButtonElement(2, "Upload texture", new ControlElement.IconData(Material.REDSTONE_COMPARATOR_ON), "Click", true, buttonElement -> uploadCapeTexture()));
        subSettings.add(new ButtonElement(3, "Delete texture", new ControlElement.IconData(Material.BARRIER), "Click", true, buttonElement -> deleteCape()));
        subSettings.add(new DiscordElement("Discord", "Discord", "Discord"));
        subSettings.add(new PreviewElement());
    }

    private void openFileDialog() {
        if (!FileChooser.isOpened()) {
            executorService.execute(() -> {
                File file = FileChooser.openAWTFileDialog();
                if (file != null) {
                    try {
                        if (file.length() > 1024 * 1024) {
                            JOptionPane.showMessageDialog(null, "File too big! Max filesize: 1MB", "HDCapes", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        BufferedImage img = ImageIO.read(file);
                        if (img == null) return;
                        if (img.getWidth() > MAX_WIDTH || img.getHeight() > MAX_HEIGHT) {
                            JOptionPane.showMessageDialog(null, "Wrong resolution! Max resolution: " + MAX_WIDTH + " x " + MAX_HEIGHT, "HDCapes", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        BufferedImage image = parseImage(img, true);
                        hdCapesManager.getTextureQueue().put(api.getPlayerUUID(), image);
                        tempFile = file;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
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

    private void deleteCape() {
        hdCapesManager.reset();
        RequestAPI.deleteCape();
    }

    private void uploadCapeTexture() {
        RequestAPI.upload();
    }

    public static HDCapes getInstance() {
        return instance;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public File getTempFile() {
        return tempFile;
    }

    public void setTempFile(File tempFile) {
        this.tempFile = tempFile;
    }

    public HDCapesManager getHdCapesManager() {
        return hdCapesManager;
    }

    public HashMap<UUID, Boolean> getFetchedUsers() {
        return fetchedUsers;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
