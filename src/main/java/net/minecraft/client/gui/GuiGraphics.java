package net.minecraft.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Compatibility shim for the 1.21.11-era GuiGraphics API, which was replaced by
 * GuiGraphicsExtractor in Minecraft 26.1. Provides the old method surface used by
 * the mod while delegating actual rendering to the new deferred extractor.
 */
public class GuiGraphics {
    private final GuiGraphicsExtractor delegate;
    private final PoseStack pose = new PoseStack();

    public GuiGraphics(GuiGraphicsExtractor delegate) {
        this.delegate = delegate;
    }

    public GuiGraphicsExtractor getExtractor() {
        return delegate;
    }

    public PoseStack pose() {
        return pose;
    }

    public int guiWidth() {
        return delegate.guiWidth();
    }

    public int guiHeight() {
        return delegate.guiHeight();
    }

    public void drawString(Font font, String text, int x, int y, int color) {
        delegate.text(font, text, x, y, color);
    }

    public void drawString(Font font, String text, int x, int y, int color, boolean shadow) {
        delegate.text(font, text, x, y, color, shadow);
    }

    public void drawString(Font font, Component text, int x, int y, int color) {
        delegate.text(font, text, x, y, color);
    }

    public void drawString(Font font, Component text, int x, int y, int color, boolean shadow) {
        delegate.text(font, text, x, y, color, shadow);
    }

    public void fill(int x1, int y1, int x2, int y2, int color) {
        delegate.fill(x1, y1, x2, y2, color);
    }

    public void blit(Identifier texture, int x, int y, int width, int height) {
        delegate.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, height, width, height);
    }

    public void blit(Identifier texture, int x, int y, int u, int v, int width, int height) {
        delegate.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, width, height);
    }

    public void blit(Identifier texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        delegate.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, width, height, textureWidth, textureHeight);
    }

    public void blit(Identifier texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int blitOffset) {
        delegate.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, width, height, textureWidth, textureHeight);
    }

    public void blit(Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        delegate.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public void blitSprite(Identifier sprite, int x, int y, int width, int height) {
        delegate.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height);
    }

    public void drawSprite(int x, int y, int z, int width, int height, TextureAtlasSprite sprite) {
        delegate.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height);
    }

    public void renderItem(ItemStack stack, int x, int y) {
        delegate.item(stack, x, y);
    }

    public void renderItem(ItemStack stack, int x, int y, int seed) {
        delegate.item(stack, x, y, seed);
    }

    public void drawItemInSlot(Font font, ItemStack stack, int x, int y) {
        delegate.itemDecorations(font, stack, x, y);
    }

    public void renderItemDecorations(Font font, ItemStack stack, int x, int y) {
        delegate.itemDecorations(font, stack, x, y);
    }

    public void setTooltipForNextFrame(Component tooltip, int mouseX, int mouseY) {
        delegate.setTooltipForNextFrame(tooltip, mouseX, mouseY);
    }

    public void enableScissor(int x1, int y1, int x2, int y2) {
        delegate.enableScissor(x1, y1, x2, y2);
    }

    public void disableScissor() {
        delegate.disableScissor();
    }

    public static GuiGraphics of(Minecraft minecraft, GuiGraphicsExtractor extractor) {
        return extractor == null ? null : new GuiGraphics(extractor);
    }
}
