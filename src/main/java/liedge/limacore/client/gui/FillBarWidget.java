package liedge.limacore.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class FillBarWidget implements LimaRenderable
{
    private final int x;
    private final int y;
    private final int backgroundWidth;
    private final int backgroundHeight;
    private final int foregroundWidth;
    private final int foregroundHeight;

    protected FillBarWidget(int x, int y, int backgroundWidth, int backgroundHeight, int foregroundWidth, int foregroundHeight)
    {
        this.x = x;
        this.y = y;
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
        this.foregroundWidth = foregroundWidth;
        this.foregroundHeight = foregroundHeight;
    }

    protected abstract float getFillPercentage();

    protected abstract ResourceLocation getBackgroundSprite();

    protected abstract ResourceLocation getForegroundSprite(float fillPercentage);

    protected void renderBackground(GuiGraphics graphics)
    {
        graphics.blitSprite(getBackgroundSprite(), getX(), getY(), backgroundWidth, backgroundHeight);
    }

    protected void renderHorizontalBar(GuiGraphics graphics, float fillPercentage)
    {
        if (fillPercentage > 0)
        {
            TextureAtlasSprite sprite = Minecraft.getInstance().getGuiSprites().getSprite(getForegroundSprite(fillPercentage));
            LimaGuiUtil.partialHorizontalBlit(graphics, getX() + 1, getY() + 1, foregroundWidth, foregroundHeight, Mth.clamp(fillPercentage, 0f, 1f), sprite);
        }
    }

    protected void renderVerticalBar(GuiGraphics graphics, float fillPercentage)
    {
        if (fillPercentage > 0)
        {
            TextureAtlasSprite sprite = Minecraft.getInstance().getGuiSprites().getSprite(getForegroundSprite(fillPercentage));
            LimaGuiUtil.partialVerticalBlit(graphics, getX() + 1, getY() + 1, foregroundWidth, foregroundHeight, Mth.clamp(fillPercentage, 0f, 1f), sprite);
        }
    }

    @Override
    public int getX()
    {
        return x;
    }

    @Override
    public int getY()
    {
        return y;
    }

    @Override
    public int getWidth()
    {
        return backgroundWidth;
    }

    @Override
    public int getHeight()
    {
        return backgroundHeight;
    }

    public abstract static class HorizontalBar extends FillBarWidget
    {
        protected HorizontalBar(int x, int y, int backgroundWidth, int backgroundHeight, int foregroundWidth, int foregroundHeight)
        {
            super(x, y, backgroundWidth, backgroundHeight, foregroundWidth, foregroundHeight);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
        {
            renderBackground(graphics);
            renderHorizontalBar(graphics, getFillPercentage());
        }
    }

    public abstract static class VerticalBar extends FillBarWidget
    {
        protected VerticalBar(int x, int y, int backgroundWidth, int backgroundHeight, int foregroundWidth, int foregroundHeight)
        {
            super(x, y, backgroundWidth, backgroundHeight, foregroundWidth, foregroundHeight);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
        {
            renderBackground(graphics);
            renderVerticalBar(graphics, getFillPercentage());
        }
    }
}