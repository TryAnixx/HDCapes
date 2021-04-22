package de.tryanixx.hdcapes.updater;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.tryanixx.hdcapes.HDCapes;
import de.tryanixx.hdcapes.utils.FileDownloader;
import de.tryanixx.hdcapes.utils.Utils;
import net.labymod.addon.AddonLoader;
import net.minecraft.realms.RealmsSharedConstants;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URLConnection;

public class UpdateChecker implements Runnable {

    private static File initFile() {
        File dir = null;
        File file = null;
        try {
            dir = AddonLoader.getAddonsDirectory();
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
            final String[] ver = RealmsSharedConstants.VERSION_STRING.split("\\.");
            dir = new File("LabyMod/", "addons-" + ver[0] + "." + ver[1]);
        }
        if (dir != null && dir.exists()) {
            file = new File(dir, "HDCapes.jar");
            if (!file.exists()) {
                File[] listFiles;
                for (int length = (listFiles = dir.listFiles()).length, i = 0; i < length; ++i) {
                    final File f = listFiles[i];
                    if (f.getName().toLowerCase().contains("hdcapes")) {
                        file = f;
                        break;
                    }
                }
            }
        }
        if (dir != null && file != null) {
            if (file.exists()) {
                return file;
            }
        }
        try {
            final URLConnection con = HDCapes.class.getProtectionDomain().getCodeSource().getLocation().openConnection();
            file = new File(((JarURLConnection) con).getJarFileURL().getPath());
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return file;
    }

    @Override
    public void run() {
        try {
            String content = Utils.getURLContent("http://tryanixxaddons.de.cool/hdcapes/info.json");
            JsonObject object = new JsonParser().parse(content).getAsJsonObject();
            HDCapes.serverVersion = object.get("version").getAsInt();
            if (HDCapes.CLIENT_VERSION < HDCapes.serverVersion) {
                File file = initFile();
                Runtime.getRuntime().addShutdownHook(new Thread(new FileDownloader("https://dl.dropboxusercontent.com/s/e3093h029r8m78n/HDCapes.jar", file)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
