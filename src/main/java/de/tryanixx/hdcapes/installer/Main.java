package de.tryanixx.hdcapes.installer;

import de.tryanixx.hdcapes.HDCapes;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;

public class Main {
    private static HashMap<String, String> lang = new HashMap<>();

    public static void main(String[] args) {
        initLookAndFeel();
        initLanguage();
        try {
            String dir = initDirectory();
            if (!(new File(dir)).exists())
                throw new IOException("No .minecraft/LabyMod directory found!");
            if (showConfirmDialog(String.format(lang.get("installation"), new Object[] { HDCapes.CLIENT_VERSIONPRETTY }))) {
                File run = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                if (run.exists() && run.isFile()) {
                    byte b;
                    int i;
                    String[] arrayOfString;
                    for (i = (arrayOfString = new String[] { "1.8", "1.12" }).length, b = 0; b < i; ) {
                        String version = arrayOfString[b];
                        File addonsDir = new File(String.valueOf(dir) + "addons-" + version);
                        addonsDir.mkdirs();
                        File mod = new File(addonsDir, "HDCapes.jar");
                        if (!mod.exists()) {
                            byte b1;
                            int j;
                            File[] arrayOfFile;
                            for (j = (arrayOfFile = addonsDir.listFiles()).length, b1 = 0; b1 < j; ) {
                                File addons = arrayOfFile[b1];
                                if (addons.getName().toLowerCase().contains("hdcapes"))
                                    addons.delete();
                                b1++;
                            }
                        }
                        Files.copy(run.toPath(), mod.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
                        b++;
                    }
                    showMessageDialog(lang.get("success"), 1);
                } else {
                    throw new IOException("Invalid path: " + run.getAbsolutePath());
                }
            }
        } catch (FileSystemException e) {
            e.printStackTrace();
            if (e.getReason() != null && !e.getReason().isEmpty()) {
                showMessageDialog(String.valueOf(lang.get("closed")) + "\n" + e.getReason(), 0);
            } else {
                showMessageDialog(lang.get("error"), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessageDialog(lang.get("error"), 0);
        }
    }

    private static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String initDirectory() {
        String dir = String.valueOf(System.getenv("APPDATA")) + "/.minecraft/LabyMod/";
        if (!(new File(dir)).exists())
            dir = String.valueOf(System.getProperty("user.home")) + "/Library/Application Support/minecraft/LabyMod/";
        if (!(new File(dir)).exists())
            dir = String.valueOf(System.getProperty("user.home")) + "/.minecraft/LabyMod/";
        return dir;
    }

    private static void initLanguage() {
        if (Locale.getDefault().toString().toLowerCase().contains("de")) {
            lang.put("installation", "HDCapes v%s kann nun installiert werden! \n Minecraft muss bei der Installation geschlossen sein!");
            lang.put("success", "HDCapes Installation abgeschlossen!");
            lang.put("closed", "Ist Minecraft geschlossen?");
            lang.put("error", "Installation fehlgeschlagen! \n Kopiere die Mod in das Verzeichnis .minecraft/LabyMod/addons und starte Minecraft!");
        } else if (Locale.getDefault().toString().toLowerCase().contains("es")) {
            lang.put("installation",
                    "puede instalar el HDCapes v%s ahora! \n tiene que estar cerrador para instalarlo!");
            lang.put("success", "La instalacidel CosmeticsMod a terminado");
            lang.put("closed", "estcerrado?");
            lang.put("error", "instalacifall\n la Mod en la carpeta .minecraft/LabyMod/addons y empeza jugar!");
        } else {
            lang.put("installation", "HDCapes v%s is now ready for installation! \n Close Minecraft before continuing!");
            lang.put("success", "HDCapes installation finished!");
            lang.put("closed", "Minecraft closed?");
            lang.put("error", "Installation failed! \n Copy the file into .minecraft/LabyMod/addons and start Minecraft!");
        }
    }

    private static boolean showConfirmDialog(String msg) {
        return (JOptionPane.showConfirmDialog(null, msg, "HDCapes", 2, 1) == 0);
    }

    private static void showMessageDialog(String msg, int mode) {
        JOptionPane.showMessageDialog(null, msg, "HDCapes", mode);
    }
}
