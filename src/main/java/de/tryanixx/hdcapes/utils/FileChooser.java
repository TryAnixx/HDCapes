package de.tryanixx.hdcapes.utils;

import java.awt.*;
import java.io.File;
import javax.swing.UIManager;

import java.awt.FileDialog;
import java.awt.Window;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FileUtils;

public class FileChooser {
    private static boolean init;

    private static boolean godmode;

    private static Window window;

    public static boolean isOpened() {
        if (window != null) {
            window.toFront();
            window.requestFocus();
            return true;
        }
        return false;
    }

    public static File openFileDialog() {
        checkLookAndFeel();
        File file = godmode ? openAWTFileDialog() : openSwingFileDialog();
        if (file != null && file.exists() && file.getName().toLowerCase().endsWith("png")) {
            return file;
        }
        return null;
    }

    private static File openSwingFileDialog() {
        window = new JFrame();
        window.setAlwaysOnTop(true);
        window.toFront();
        window.requestFocus();
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");
        String ext = "png";
        chooser.setFileFilter(new FileNameExtensionFilter(ext.toUpperCase(), new String[]{ext}));
        int callback = chooser.showOpenDialog(window);
        window = null;
        return (callback == 0) ? chooser.getSelectedFile() : null;
    }

    private static File openAWTFileDialog() {
        FileDialog chooser = new FileDialog((Dialog) null, "Select texture");
        window = chooser;
        chooser.setMode(0);
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
                godmode = checkGodmodeFolder();
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean checkGodmodeFolder() {
        File home = FileSystemView.getFileSystemView().getHomeDirectory();
        for (String file : home.list()) {
            if (file.contains("{ED7BA470-8E54-465E-825C-99712043E01C}")) {
                return true;
            }
        }
        return false;
    }
}

