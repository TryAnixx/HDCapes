package de.tryanixx.hdcapes.utils;

import java.awt.*;
import java.io.File;
import javax.swing.UIManager;

public class FileChooser {
    private static boolean init;

    private static Window window;

    public static boolean isOpened() {
        if (window != null) {
            window.toFront();
            window.requestFocus();
            return true;
        }
        return false;
    }


    public static File openAWTFileDialog() {
        checkLookAndFeel();
        FileDialog chooser = new FileDialog((Dialog) null, "Select texture");
        window = chooser;
        chooser.setMode(0);
        chooser.setFilenameFilter((file, name) -> name.toLowerCase().endsWith(".png"));
        chooser.setDirectory(System.getProperty("user.home") + "/Desktop");
        chooser.setAlwaysOnTop(true);
        chooser.toFront();
        chooser.requestFocus();
        chooser.setVisible(true);
        String filename = chooser.getFile();
        String dirname = chooser.getDirectory();
        chooser.dispose();
        window = null;
        return (dirname != null && filename != null) ? new File(dirname, filename) : null;
    }

    private static void checkLookAndFeel() {
        if (!init) {
            init = true;
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
