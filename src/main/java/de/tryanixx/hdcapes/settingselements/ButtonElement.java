package de.tryanixx.hdcapes.settingselements;

import de.tryanixx.hdcapes.HDCapes;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public class ButtonElement extends ControlElement {
    private final GuiButton button;

    private final Consumer<ButtonElement> clickListener;

    private boolean enabled;

    private String initBtnText;

    private int overriddenStringWidth = -1;

    private boolean cooldown;


    public ButtonElement(int id, String displayName, ControlElement.IconData iconData, String inButtonName, boolean cooldown, Consumer<ButtonElement> clickListener) {
        super(displayName, iconData);
        this.cooldown = cooldown;
        this.button = new GuiButton(id, 0, 0, 0, 20, "");;
        this.button.displayString = initBtnText = inButtonName;
        this.clickListener = clickListener;
        this.setSettingEnabled(true);
    }

    public String getText() {
        return this.button.displayString;
    }

    public void setText(String text) {
        this.button.displayString = text;
        this.overriddenStringWidth = -1;
    }
    public void setText(String text, int overriddenStringWidth) {
        this.button.displayString = text;
        this.overriddenStringWidth = overriddenStringWidth;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.button.mousePressed(this.mc, mouseX, mouseY)) {
            LabyModCore.getMinecraft().playSound(SettingsElement.BUTTON_PRESS_SOUND, 1.0F);
            this.clickListener.accept(this);
            if(cooldown) HDCapes.getInstance().getCooldownManager().startCooldown();
        }
    }

    public void setInitBtnText(String initBtnText) {
        this.initBtnText = initBtnText;
    }

    public String getInitBtnText() {
        return initBtnText;
    }
    public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x, y, maxX, maxY, mouseX, mouseY);
        if (this.displayName != null){
            LabyMod.getInstance().getDrawUtils().drawRectangle(x - 1, y, x, maxY, Color.GRAY.getRGB());
        }
        int stringWidth = this.overriddenStringWidth == -1 ? LabyModCore.getMinecraft().getFontRenderer().getStringWidth(this.button.displayString) :  this.overriddenStringWidth;

        if(cooldown){

            setSettingEnabled(!HDCapes.getInstance().getCooldownManager().isCooldown());
            if(HDCapes.getInstance().getCooldownManager().isCooldown()){
                setText("§c" + HDCapes.getInstance().getCooldownManager().getRemainingTime(), LabyModCore.getMinecraft().getFontRenderer().getStringWidth(initBtnText));

            }else {
                setText(initBtnText);
            }
        }
        int buttonWidth = (this.displayName == null) ? (maxX - x) : (stringWidth + 20);
        this.button.setWidth(buttonWidth);
        this.button.enabled = this.enabled;

        LabyModCore.getMinecraft().setButtonXPosition(this.button, maxX - buttonWidth - 2);
        LabyModCore.getMinecraft().setButtonYPosition(this.button, y + 1);
        LabyModCore.getMinecraft().drawButton(this.button, mouseX, mouseY);
    }

    public void setSettingEnabled(boolean settingEnabled) {
        this.enabled = settingEnabled;
        this.button.enabled = settingEnabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
