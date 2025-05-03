package liedge.limacore.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;

public abstract class FillBarWidget implements LimaRenderable
{
    private final int x;
    private final int y;
    private final int backgroundWidth;
    private final int backgroundHeight;
    private final int foregroundWidth;
    private final int foregroundHeight;
    private final TextureAtlasSprite backgroundSprite;

    protected FillBarWidget(int x, int y, int backgroundWidth, int backgroundHeight, int foregroundWidth, int foregroundHeight, TextureAtlasSprite backgroundSprite)
    {
        this.x = x;
        this.y = y;
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
        this.foregroundWidth = foregroundWidth;
        this.foregroundHeight = foregroundHeight;
        this.backgroundSprite = backgroundSprite;
    }

    protected abstract float getFillPercentage();

    protected abstract TextureAtlasSprite getForegroundSprite(float fillPercentage);

    protected abstract void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float fillPercentage, float partialTicks);

    protected void renderHorizontalBar(GuiGraphics graphics, float fillPercentage)
    {
        if (fillPercentage > 0) LimaGuiUtil.partialHorizontalBlit(graphics, getX() + 1, getY() + 1, foregroundWidth, foregroundHeight, Mth.clamp(fillPercentage, 0f, 1f), getForegroundSprite(fillPercentage));
    }

    protected void renderVerticalBar(GuiGraphics graphics, float fillPercentage)
    {
        if (fillPercentage > 0) LimaGuiUtil.partialVerticalBlit(graphics, getX() + 1, getY() + 1, foregroundWidth, foregroundHeight, Mth.clamp(fillPercentage, 0f, 1f), getForegroundSprite(fillPercentage));
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

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        graphics.blit(getX(), getY(), 0, backgroundWidth, backgroundHeight, backgroundSprite);
        renderForeground(graphics, mouseX, mouseY, getFillPercentage(), partialTicks);
    }

    public abstract static class HorizontalBar extends FillBarWidget
    {
        protected HorizontalBar(int x, int y, int backgroundWidth, int backgroundHeight, int foregroundWidth, int foregroundHeight, TextureAtlasSprite backgroundSprite)
        {
            super(x, y, backgroundWidth, backgroundHeight, foregroundWidth, foregroundHeight, backgroundSprite);
        }

        @Override
        protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float fillPercentage, float partialTicks)
        {
            renderHorizontalBar(graphics, fillPercentage);
        }
    }

    public abstract static class VerticalBar extends FillBarWidget
    {
        protected VerticalBar(int x, int y, int backgroundWidth, int backgroundHeight, int foregroundWidth, int foregroundHeight, TextureAtlasSprite backgroundSprite)
        {
            super(x, y, backgroundWidth, backgroundHeight, foregroundWidth, foregroundHeight, backgroundSprite);
        }

        @Override
        protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float fillPercentage, float partialTicks)
        {
            renderVerticalBar(graphics, fillPercentage);
        }
    }
}