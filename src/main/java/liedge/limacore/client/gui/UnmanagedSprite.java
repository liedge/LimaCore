package liedge.limacore.client.gui;

import liedge.limacore.lib.LimaColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public record UnmanagedSprite(ResourceLocation textureSheet, int u, int v, int width, int height, float u0, float v0, float u1, float v1)
{
    public UnmanagedSprite(ResourceLocation textureSheet, int u, int v, int width, int height)
    {
        this(textureSheet, u, v, width, height,  u / 256f, v / 256f, (u + width) / 256f, (v + height) / 256f);
    }

    // U, V helpers
    public float partialU(float size)
    {
        float f = u1 - u0;
        return u0 + f * size;
    }

    public float partialV(float size)
    {
        float f = v1 - v0;
        return v0 + f * size;
    }

    public float offsetU(int pixels)
    {
        return (u + pixels) / 256f;
    }

    public float offsetV(int pixels)
    {
        return (v + pixels) / 256f;
    }

    public void singleBlit(GuiGraphics graphics, int x, int y)
    {
        graphics.blit(textureSheet, x, y, u, v, width, height);
    }

    public void directBlit(GuiGraphics graphics, float x, float y)
    {
        LimaBlitUtil.directBlit(graphics, textureSheet, x, y, x + width, y + height, u0, u1, v0, v1);
    }

    public void directColorBlit(GuiGraphics graphics, float x, float y, float red, float green, float blue, float alpha)
    {
        LimaBlitUtil.directColorBlit(graphics, textureSheet, x, y, x + width, y + height, u0, u1, v0, v1, red, green, blue, alpha);
    }

    public void directColorBlit(GuiGraphics graphics, float x, float y, LimaColor color)
    {
        directColorBlit(graphics, x, y, color.red(), color.green(), color.blue(), 1f);
    }

    public void partialHorizontalBlit(GuiGraphics graphics, float x, float y, float percentage)
    {
        float dx = width * percentage;
        LimaBlitUtil.directBlit(graphics, textureSheet, x, y, x + dx, y + height, u0, u0 + (dx / 256f), v0, v1);
    }

    public void partialVerticalBlit(GuiGraphics graphics, float x, float y, float percentage)
    {
        float partialHeight = height * percentage;
        float dy = height - partialHeight;
        y += dy;

        LimaBlitUtil.directBlit(graphics, textureSheet, x, y, x + width, y + partialHeight, u0, u1, (v + dy) / 256f, v1);
    }

    @Deprecated
    public void oldPartialHorizontalBlit(GuiGraphics graphics, int x, int y, float percentage)
    {
        graphics.blit(textureSheet, x, y, u, v, (int) (width * percentage), height);
    }

    @Deprecated
    public void oldPartialVerticalBlit(GuiGraphics graphics, int x, int y, float percentage)
    {
        int partialHeight = (int) (height * percentage);
        int offset = height - partialHeight;
        graphics.blit(textureSheet, x, y + offset, u, v + offset, width, partialHeight);
    }
}