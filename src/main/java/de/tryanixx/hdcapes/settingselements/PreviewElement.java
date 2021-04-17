package de.tryanixx.hdcapes.settingselements;

import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.DrawUtils;

public class PreviewElement extends SettingsElement {
    public PreviewElement() {
        super("PreviewElement", null);
    }

    @Override
    public void drawDescription(int i, int i1, int i2) {

    }

    @Override
    public void mouseClicked(int i, int i1, int i2) {

    }

    @Override
    public void mouseRelease(int i, int i1, int i2) {

    }

    @Override
    public void mouseClickMove(int i, int i1, int i2) {

    }

    @Override
    public void keyTyped(char c, int i) {

    }

    @Override
    public void unfocus(int i, int i1, int i2) {

    }

    @Override
    public int getEntryHeight() {
        return 0;
    }

    @Override
    public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
        if(LabyMod.getInstance().isInGame()) {
            int posX = maxX + (maxX / 7);
            int posY = 120 + (x / 2);
            int pointX = mouseX - posX;
            int pointY = -mouseY + posY - 100;
            int rotation = 180;
            posY += 20;
            DrawUtils.drawEntityOnScreen(posX, posY, (int) (maxX / 7.0), pointX, pointY, rotation, 0, 0, LabyModCore.getMinecraft().getPlayer());
        }
    }
}
