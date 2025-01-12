package liedge.limacore.client.gui;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.LimaColor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public record UnmanagedSprite(ResourceLocation textureSheet, int u, int v, int width, int height, float u0, float v0, float u1, float v1)
{
    public static final Codec<UnmanagedSprite> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("sheet").forGetter(UnmanagedSprite::textureSheet),
            Codec.intRange(0, 255).fieldOf("u").forGetter(UnmanagedSprite::u),
            Codec.intRange(0, 255).fieldOf("v").forGetter(UnmanagedSprite::v),
            Codec.intRange(1, 256).fieldOf("width").forGetter(UnmanagedSprite::width),
            Codec.intRange(1, 256).fieldOf("height").forGetter(UnmanagedSprite::height))
            .apply(instance, UnmanagedSprite::new));

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
        singleBlit(graphics, x, y, 0);
    }

    public void singleBlit(GuiGraphics graphics, int x, int y, int zOffset)
    {
        graphics.blit(textureSheet, x, y, zOffset, (float) u, (float) v, width, height, 256, 256);
    }

    public void directBlit(GuiGraphics graphics, float x, float y)
    {
        LimaGuiUtil.directBlit(graphics, textureSheet, x, y, x + width, y + height, u0, u1, v0, v1);
    }

    public void directColorBlit(GuiGraphics graphics, float x, float y, float red, float green, float blue, float alpha)
    {
        LimaGuiUtil.directColorBlit(graphics, textureSheet, x, y, x + width, y + height, u0, u1, v0, v1, red, green, blue, alpha);
    }

    public void directColorBlit(GuiGraphics graphics, float x, float y, int argb32)
    {
        LimaGuiUtil.directColorBlit(graphics, textureSheet, x, y, x + width, y + height, u0, u1, v0, v1, argb32);
    }

    public void directColorBlit(GuiGraphics graphics, float x, float y, LimaColor color)
    {
        directColorBlit(graphics, x, y, color.red(), color.green(), color.blue(), 1f);
    }

    public void partialHorizontalBlit(GuiGraphics graphics, float x, float y, float percentage)
    {
        float dx = width * percentage;
        LimaGuiUtil.directBlit(graphics, textureSheet, x, y, x + dx, y + height, u0, u0 + (dx / 256f), v0, v1);
    }

    public void partialVerticalBlit(GuiGraphics graphics, float x, float y, float percentage)
    {
        float partialHeight = height * percentage;
        float dy = height - partialHeight;
        y += dy;

        LimaGuiUtil.directBlit(graphics, textureSheet, x, y, x + width, y + partialHeight, u0, u1, (v + dy) / 256f, v1);
    }
}