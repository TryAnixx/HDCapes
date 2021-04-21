package de.tryanixx.hdcapes.settingselements;

import com.sun.jna.platform.win32.WinDef;
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
        this.button = new GuiButton(id, 0, 0, 0, 20, "");
        ;
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
            if (this.displayName.equals("Upload texture")) {
                if (cooldown) HDCapes.getInstance().getCooldownManager().startCooldownUpload();
            } else if (this.displayName.equals("Delete texture")) {
                if (cooldown) HDCapes.getInstance().getCooldownManager().startCooldownDelete();
            } else if (this.displayName.equals("Refresh")) {
                if (cooldown) HDCapes.getInstance().getCooldownManager().startCooldownRefresh();
            }
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
        if (this.displayName != null) {
            LabyMod.getInstance().getDrawUtils().drawRectangle(x - 1, y, x, maxY, Color.GRAY.getRGB());
        }
        int stringWidth = this.overriddenStringWidth == -1 ? LabyModCore.getMinecraft().getFontRenderer().getStringWidth(this.button.displayString) : this.overriddenStringWidth;

        if (cooldown) {

            if (this.displayName.equals("Upload texture")) {
                setSettingEnabled(!HDCapes.getInstance().getCooldownManager().isCooldownUpload());
                if (HDCapes.getInstance().getCooldownManager().isCooldownUpload()) {
                    setText("§c" + HDCapes.getInstance().getCooldownManager().getRemainingTimeUpload(), LabyModCore.getMinecraft().getFontRenderer().getStringWidth(initBtnText));
                } else {
                    setText(initBtnText);
                }
            } else if (this.displayName.equals("Delete texture")) {
                setSettingEnabled(!HDCapes.getInstance().getCooldownManager().isCooldownDelete());
                if (HDCapes.getInstance().getCooldownManager().isCooldownDelete()) {
                    setText("§c" + HDCapes.getInstance().getCooldownManager().getRemainingTimeDelete(), LabyModCore.getMinecraft().getFontRenderer().getStringWidth(initBtnText));
                } else {
                    setText(initBtnText);
                }
            }
            if (this.displayName.equals("Refresh")) {
                setSettingEnabled(!HDCapes.getInstance().getCooldownManager().isCooldownRefresh());
                if (HDCapes.getInstance().getCooldownManager().isCooldownRefresh()) {
                    setText("§c" + HDCapes.getInstance().getCooldownManager().getRemainingTimeRefresh(), LabyModCore.getMinecraft().getFontRenderer().getStringWidth(initBtnText));
                } else {
                    setText(initBtnText);
                }
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
