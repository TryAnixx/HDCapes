package de.tryanixx.hdcapes.settingselements;

import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class DiscordElement extends SettingsElement {

    private static final ResourceLocation DISCORDICON = new ResourceLocation("hdcapes/discord.png");

    public DiscordElement(String displayName, String description, String configEntryName) {
        super(displayName, description, configEntryName);
    }

    @Override
    public void drawDescription(int i, int i1, int i2) {

    }

    @Override
    public void mouseClicked(int i, int i1, int i2) {
        LabyMod.getInstance().openWebpage("https://discord.hdcapes.de/", false);
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
        this.mouseOver = (mouseX >= 7 && mouseX <= 25 && mouseY <= height - 17 && mouseY >= height - 40);
        int add = isMouseOver() ? 1 : 0;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(DISCORDICON);
        LabyMod.getInstance().getDrawUtils().drawTexture((add + 3), (height - 40 - add), 245.0D, 255.0D, (25 + add * 2), (25 + add * 2));
        if(isMouseOver()) {
            LabyMod.getInstance().getDrawUtils().drawHoveringText(mouseX, mouseY, new String[] { "OUR DISCORD" });
        }
        GlStateManager.popMatrix();
    }
}
