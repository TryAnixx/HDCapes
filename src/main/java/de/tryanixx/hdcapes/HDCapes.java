package de.tryanixx.hdcapes;

import de.tryanixx.hdcapes.authenticate.Authenticator;
import de.tryanixx.hdcapes.cooldown.CooldownManager;
import de.tryanixx.hdcapes.manager.HDCapesManager;
import de.tryanixx.hdcapes.settingselements.ButtonElement;
import de.tryanixx.hdcapes.settingselements.PreviewElement;
import de.tryanixx.hdcapes.utils.FileChooser;
import de.tryanixx.hdcapes.utils.RequestAPI;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HDCapes extends LabyModAddon {

    private static HDCapes instance;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private HDCapesManager hdCapesManager;
    private CooldownManager cooldownManager;

    private Authenticator authenticator;

    public static final int MAX_HEIGHT = 1100;
    public static final int MAX_WIDTH = 1408;

    private File tempFile;

    @Override
    public void onEnable() {
        instance = this;

        hdCapesManager = new HDCapesManager();
        cooldownManager = new CooldownManager();
        authenticator = new Authenticator();

        api.registerForgeListener(hdCapesManager);
    }

    @Override
    public void loadConfig() {

    }

    @Override
    protected void fillSettings(List<SettingsElement> subSettings) {
        subSettings.add(new PreviewElement());
        subSettings.add(new ButtonElement("Choose texture", this::openFileDialog));
        subSettings.add(new ButtonElement("Upload texture", this::uploadCapeTexture));
        subSettings.add(new ButtonElement("Delete texture", this::deleteCape));
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
                        if (img.getWidth() > MAX_WIDTH || img.getHeight() > MAX_HEIGHT) {
                            JOptionPane.showMessageDialog(null, "Wrong resolution! Max resolution: " + MAX_WIDTH + " x " + MAX_HEIGHT, "HDCapes", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        hdCapesManager.getTextureQueue().put(api.getPlayerUUID(), ImageIO.read(file));
                        tempFile = file;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void deleteCape() {
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

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
