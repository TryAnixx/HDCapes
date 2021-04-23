package de.tryanixx.hdcapes.settingselements;

import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class CapeTemplateElement extends SettingsElement {

    private static final ResourceLocation CAPE_ICON = new ResourceLocation("hdcapes/cape.png");

    public CapeTemplateElement(String displayName, String description, String configEntryName) {
        super(displayName, description, configEntryName);
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
        int height = LabyMod.getInstance().getDrawUtils().getHeight() - 20;
        this.mouseOver = (mouseX >= 7 && mouseX <= 25 && mouseY <= height - 47 && mouseY >= height - 70);
        int add = isMouseOver() ? 1 : 0;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(CAPE_ICON);
        LabyMod.getInstance().getDrawUtils().drawTexture((add + 3), (height - 70 - add), 245.0D, 255.0D, (25 + add * 2), (25 + add * 2));
        if(isMouseOver()) {
            LabyMod.getInstance().getDrawUtils().drawHoveringText(mouseX, mouseY, new String[] { "CAPE TEMPLATE" });
        }
        GlStateManager.popMatrix();
    }
}
