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
import de.tryanixx.hdcapes.utils.Utils;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.user.UserManager;
import net.labymod.utils.Material;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
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
    private boolean seeowncapeonly;

    private File tempFile;

    private HashMap<UUID, Boolean> fetchedUsers = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        api.registerForgeListener(hdCapesManager = new HDCapesManager());

        authenticator = new Authenticator();
        cooldownManager = new CooldownManager();

        RequestAPI.fetchAndCacheUsersScheduled();

        api.getEventManager().register(new RenderEntityListener());

    }

    @Override
    public void loadConfig() {
        this.seeowncapeonly = getConfig().has("seeowncapeonly") ? getConfig().get("seeowncapeonly").getAsBoolean() : false;
    }

    @Override
    protected void fillSettings(List<SettingsElement> subSettings) {
        subSettings.add(new ButtonElement(1, "Choose texture (Offline-Preview)", new ControlElement.IconData(Material.ITEM_FRAME), "Click", false, buttonElement -> openFileDialog()));
        subSettings.add(new ButtonElement(2, "Upload texture", new ControlElement.IconData(Material.REDSTONE_COMPARATOR), "Click", true, buttonElement -> uploadCapeTexture()));
        subSettings.add(new ButtonElement(3, "Delete texture", new ControlElement.IconData(Material.BARRIER), "Click", true, buttonElement -> deleteCape()));
        subSettings.add(new ButtonElement(4, "Refresh", new ControlElement.IconData(Material.BED), "Click", true, buttonElement -> refreshCosmetics()));
        subSettings.add(new BooleanElement("Only see own HDCape", this, new ControlElement.IconData(Material.BLAZE_ROD), "seeowncapeonly", this.seeowncapeonly).addCallback(aBoolean -> {
            refreshCosmetics();
        }));
        subSettings.add(new DiscordElement("Discord", "Discord", "Discord"));
        subSettings.add(new PreviewElement());
    }

    private void openFileDialog() {
        if (!FileChooser.isOpened()) {
            executorService.execute(() -> {
                File file = FileChooser.openFileDialog();
                if (file != null) {
                    try {
                        if (file.length() > 1024 * 1024) {
                            JOptionPane.showMessageDialog(null, "File too big! Max filesize: 1MB", "HDCapes", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        BufferedImage img = ImageIO.read(file);
                        if (img == null) return;
                        if (img.getWidth() > MAX_WIDTH || img.getHeight() > MAX_HEIGHT) {
                            img = Utils.getScaledImage(img);
                            JOptionPane.showMessageDialog(null, "Please use our recommend resolution or cape template! We scaled your texutre." + MAX_WIDTH + " x " + MAX_HEIGHT, "HDCapes", JOptionPane.ERROR_MESSAGE);
                        }
                        BufferedImage image = parseImage(img, true);
                        hdCapesManager.getTextureQueue().put(api.getPlayerUUID(), image);
                        tempFile = file;
                    } catch (IIOException e) {
                        System.out.println("HDCapes » Cant read input file!");
                    } catch (Exception e) {
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

    private void refreshCosmetics() {
        fetchedUsers.clear();
        try {
            UserManager.class.getDeclaredMethod("refresh", null).invoke(LabyMod.getInstance().getUserManager());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        RequestAPI.fetchAndCacheUser();
    }

    private void deleteCape() {
        hdCapesManager.reset();
        RequestAPI.deleteCape();
    }

    private void uploadCapeTexture() {
        if (HDCapes.getInstance().getTempFile() == null) {
            executorService.execute(() -> {
                JOptionPane.showMessageDialog(null, "Please insert your texture again!", "HDCapes", JOptionPane.ERROR_MESSAGE);
            });
            return;
        }
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

    public boolean isSeeowncapeonly() {
        return seeowncapeonly;
    }
}
