package de.tryanixx.hdcapes.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.tryanixx.hdcapes.HDCapes;
import de.tryanixx.hdcapes.authenticate.Authenticator;
import net.labymod.main.LabyMod;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RequestAPI {

    private static ExecutorService exservice = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public static void fetchAndCacheUsersScheduled() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                String users = getURLContent("http://tryanixxaddons.de.cool/hdcapes/database.php");
                JsonArray object = new JsonParser().parse(users).getAsJsonArray();
                object.forEach(jsonElement -> {
                    String uuid = jsonElement.getAsString();
                    if (!HDCapes.getInstance().getFetchedUsers().containsKey(uuid)) {
                        HDCapes.getInstance().getFetchedUsers().put(UUID.fromString(uuid), false);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    public static void fetchAndCacheUser() {
        HDCapes.getInstance().getFetchedUsers().clear();
        exservice.execute(() -> {
            try {
                String users = getURLContent("http://tryanixxaddons.de.cool/hdcapes/database.php");
                JsonArray object = new JsonParser().parse(users).getAsJsonArray();
                object.forEach(jsonElement -> {
                    String uuid = jsonElement.getAsString();
                    if (!HDCapes.getInstance().getFetchedUsers().containsKey(uuid)) {
                        HDCapes.getInstance().getFetchedUsers().put(UUID.fromString(uuid), false);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void deleteCape() {
        HDCapes.getInstance().getAuthenticator().authenticate(Authenticator.SERVER_HASH);
        exservice.execute(() -> {
            try {
                HttpURLConnection con = (HttpURLConnection) (new URL(
                        "http://tryanixxaddons.de.cool/hdcapes/deletedata.php?name=" + LabyMod.getInstance().getLabyModAPI().getPlayerUsername() + "&uuid=" + LabyMod.getInstance().getLabyModAPI().getPlayerUUID())).openConnection();
                con.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                con.connect();
                int code = con.getResponseCode();
                if (code == 200) {
                    System.out.println("HDCapes » PING SUCCESFULLY");
                }
                HDCapes.getInstance().setTempFile(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean upload() {
        if (!hasCape(LabyMod.getInstance().getPlayerUUID())) {
            exservice.execute(() -> JOptionPane.showMessageDialog(null, "You dont own / activated a cape!", "HDCapes", JOptionPane.ERROR_MESSAGE));
            return true;
        }
        exservice.execute(() -> {
            HDCapes.getInstance().getAuthenticator().authenticate(Authenticator.SERVER_HASH);

            try {
                String params = "name=" + LabyMod.getInstance().getPlayerName() + "&uuid=" + LabyMod.getInstance().getPlayerUUID();
                HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL("http://tryanixxaddons.de.cool/hdcapes/upload.php?" + params + "&uuidhash=" + LabyMod.getInstance().getPlayerUUID().getMostSignificantBits() + "&filename=" + LabyMod.getInstance().getPlayerUUID() + ".png").openConnection();
                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setRequestMethod("POST");
                OutputStream os = httpUrlConnection.getOutputStream();
                BufferedInputStream fis = new BufferedInputStream(new FileInputStream(HDCapes.getInstance().getTempFile()));

                long totalByte = fis.available();
                for (int i = 0; i < totalByte; i++) {
                    os.write(fis.read());
                }

                os.close();

                if (httpUrlConnection.getResponseCode() == 403) {
                    JOptionPane.showMessageDialog(null, "This cloak got banned from HDCapes!", "HDCapes", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                httpUrlConnection.getInputStream()));
                in.close();
                fis.close();

                if (httpUrlConnection.getResponseCode() != 200) {
                    int code = httpUrlConnection.getResponseCode();
                    System.out.println("ERROR CODE: " + code);
                    JOptionPane.showMessageDialog(null, "Error! Please contact our Support!", "HDCapes", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                HDCapes.getInstance().setTempFile(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return false;
    }

    private static boolean hasCape(UUID uuid) {
        try {
            String content = getURLContent("https://dl.labymod.net/userdata/" + uuid.toString() + ".json");
            JsonObject object = new JsonParser().parse(content).getAsJsonObject();
            JsonArray cosmetics = object.getAsJsonArray("c");
            for (JsonElement el : cosmetics) {
                if (el.getAsJsonObject().get("i").getAsInt() == 0) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String getURLContent(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
        con.setConnectTimeout(5000);
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        con.connect();
        return IOUtils.toString(con.getInputStream(), "UTF-8");
    }

    public static ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}
