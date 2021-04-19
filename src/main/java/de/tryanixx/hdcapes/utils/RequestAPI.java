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

public class RequestAPI {

    private static ExecutorService exservice = Executors.newSingleThreadScheduledExecutor();

    public static void fetchandcacheusers() {
        exservice.execute(() -> {
            try {
                String users = getURLContent("http://tryanixxaddons.de.cool/hdcapes/database.php");
                JsonArray object = new JsonParser().parse(users).getAsJsonArray();
                object.forEach(jsonElement -> HDCapes.getInstance().getFetchedUsers().put(UUID.fromString(jsonElement.getAsJsonObject().get("uuid").getAsString()), false));
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
                //TODO DELETE CAPE CLIENTSIDE AND REMOVE
                HDCapes.getInstance().setTempFile(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public static boolean upload() {
        exservice.execute(() -> {

            if (!hasCape(LabyMod.getInstance().getPlayerUUID())) {
                //TODO CONNOR KANN HIER AUCH IRGENDWAS COOLES DESIGN MÄßIGES PROVIDEN LOL GIHUB SIEHT DAS WNEN DU DAS LIEST KEINE AHNUNG BIST DU TOLL VIELLEICHT!
                JOptionPane.showMessageDialog(null, "You dont own / activated a cape!", "HDCapes", JOptionPane.ERROR_MESSAGE);
               return;
            }

            if (HDCapes.getInstance().getCooldownManager().isCooldown()) {
                //TODO CONNOR MUSS PROVIDEN
                JOptionPane.showMessageDialog(null, "Please wait " + HDCapes.getInstance().getCooldownManager().getRemainingTime() + " seconds!", "HDCapes", JOptionPane.ERROR_MESSAGE);
               return;
            }

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
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                httpUrlConnection.getInputStream()));
                in.close();
                fis.close();

                if (httpUrlConnection.getResponseCode() == 31) {
                    JOptionPane.showMessageDialog(null, "This cloak got banned from HDCapes!", "HDCapes", JOptionPane.ERROR_MESSAGE);
                    int code = httpUrlConnection.getResponseCode();
                    System.out.println("CODE: " + code);
                    return;
                }

               // if (httpUrlConnection.getResponseCode() != 200 || httpUrlConnection.getResponseCode() != 31) {
                //     int code = httpUrlConnection.getResponseCode();
                //  System.out.println("CODE: " + code);
                //  JOptionPane.showMessageDialog(null, "Error! Please contact our Support!", "HDCapes", JOptionPane.ERROR_MESSAGE);
                //  return;
                //}
                HDCapes.getInstance().setTempFile(null);
                HDCapes.getInstance().getCooldownManager().startCooldown();
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
}
