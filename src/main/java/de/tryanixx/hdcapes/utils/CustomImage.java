package de.tryanixx.hdcapes.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class CustomImage extends SimpleTexture {

    private BufferedImage img;
    private boolean loaded;

    public CustomImage(ResourceLocation textureResourceLocation, BufferedImage img) {
        super(textureResourceLocation);
        this.img = img;
    }

    @Override
    public int getGlTextureId() {
        int textureId = super.getGlTextureId();
        if (!this.loaded && this.img != null) {
            this.loaded = true;
            TextureUtil.uploadTextureImage(textureId, this.img);
        }
        return textureId;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
    }

    @Override
    public void deleteGlTexture() {
        super.deleteGlTexture();
        img = null;
    }

    public boolean isLoaded() {
        return loaded;
    }
}