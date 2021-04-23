package de.tryanixx.hdcapes;

import de.tryanixx.hdcapes.cooldown.CooldownManager;
import de.tryanixx.hdcapes.listeners.RenderEntityListener;
import de.tryanixx.hdcapes.manager.HDCapesManager;
import de.tryanixx.hdcapes.settingselements.ButtonElement;
import de.tryanixx.hdcapes.settingselements.CapeTemplateElement;
import de.tryanixx.hdcapes.settingselements.DiscordElement;
import de.tryanixx.hdcapes.settingselements.PreviewElement;
import de.tryanixx.hdcapes.updater.UpdateChecker;
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

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";

    private HDCapesManager hdCapesManager;

    private CooldownManager cooldownManager;

    public static final int MAX_HEIGHT = 1100;
    public static final int MAX_WIDTH = 1408;
    private boolean seeowncapeonly;
    private boolean disablePreview;

    private File tempFile;

    private HashMap<UUID, Boolean> fetchedUsers = new HashMap<>();

    public static int serverVersion;
    public static final int CLIENT_VERSION = 2;
    public static final String CLIENT_VERSIONPRETTY = "1.0";

    @Override
    public void onEnable() {
        instance = this;
        api.registerForgeListener(hdCapesManager = new HDCapesManager());

        cooldownManager = new CooldownManager();

        executorService.execute(new UpdateChecker());

        RequestAPI.fetchAndCacheUsersScheduled();
        LabyMod.getInstance().getDynamicTextureManager();
        api.getEventManager().register(new RenderEntityListener());

    }

    @Override
    public void loadConfig() {
        this.seeowncapeonly = getConfig().has("seeowncapeonly") ? getConfig().get("seeowncapeonly").getAsBoolean() : false;
    }

    @Override
    protected void fillSettings(List<SettingsElement> subSettings) {
        subSettings.add(new ButtonElement(1, "Choose texture (Offline-Preview)", new ControlElement.IconData(Material.ITEM_FRAME), "Click", false, buttonElement -> openFileDialog()));
        subSettings.add(new ButtonElement(2, "Upload texture", new ControlElement.IconData(Material.REDSTONE_COMPARATOR), "Click", true, buttonElement -> hdCapesManager.uploadCapeTexture()));
        subSettings.add(new ButtonElement(3, "Delete texture", new ControlElement.IconData(Material.BARRIER), "Click", true, buttonElement -> hdCapesManager.deleteCape()));
        subSettings.add(new ButtonElement(4, "Refresh", new ControlElement.IconData(Material.BED), "Click", true, buttonElement -> refreshCosmetics()));
        subSettings.add(new BooleanElement("Only see own HDCape", this, new ControlElement.IconData(Material.BLAZE_ROD), "seeowncapeonly", this.seeowncapeonly).addCallback(aBoolean -> {
            seeowncapeonly = aBoolean;
            refreshCosmetics();
        }));
        subSettings.add(new DiscordElement("Discord", "Discord", "Discord"));
        subSettings.add(new CapeTemplateElement("CapeTemplate", "CapeTemplate", "CapeTemplate"));
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
                        BufferedImage image = Utils.parseImage(img, true);
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

    private void refreshCosmetics() {
        disablePreview = true;
        fetchedUsers.clear();
        try {
            UserManager.class.getDeclaredMethod("refresh", null).invoke(LabyMod.getInstance().getUserManager());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        RequestAPI.fetchAndCacheUser();
        disablePreview = false;
    }

    public static HDCapes getInstance() {
        return instance;
    }

    public ExecutorService getExecutorService() {
        return executorService;
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

    public boolean isDisablePreview() {
        return disablePreview;
    }
}
