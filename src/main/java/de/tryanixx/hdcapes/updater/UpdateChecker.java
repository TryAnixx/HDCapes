package de.tryanixx.hdcapes.updater;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.tryanixx.hdcapes.HDCapes;
import de.tryanixx.hdcapes.utils.FileDownloader;
import net.labymod.addon.AddonLoader;
import net.minecraft.realms.RealmsSharedConstants;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class UpdateChecker {
    public void check() {
        ScheduledExecutorService exservice = Executors.newSingleThreadScheduledExecutor();
        exservice.execute(() -> {

            try {
                String content = getURLContent("http://tryanixxaddons.de.cool/hdcapes/info.json");
                JsonObject object = new JsonParser().parse(content).getAsJsonObject();
                HDCapes.serverVersion = object.get("version").getAsInt();
                if (HDCapes.CLIENT_VERSION < HDCapes.serverVersion) {
                    File file = initFile();
                    Runtime.getRuntime().addShutdownHook(new Thread(new FileDownloader("https://dl.dropboxusercontent.com/s/e3093h029r8m78n/HDCapes.jar", file)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private String getURLContent(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
        con.setConnectTimeout(5000);
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        con.connect();
        return IOUtils.toString(con.getInputStream(), "UTF-8");
    }

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
}
