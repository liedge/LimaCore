package liedge.limacore.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public abstract class FillBarWidget implements LimaRenderable
{
    private final int x;
    private final int y;
    private final UnmanagedSprite backgroundSprite;

    protected FillBarWidget(int x, int y, UnmanagedSprite backgroundSprite)
    {
        this.x = x;
        this.y = y;
        this.backgroundSprite = backgroundSprite;
    }

    protected abstract float getFillPercentage();

    protected abstract UnmanagedSprite getForegroundSprite(float fillPercentage);

    protected abstract void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float fillPercentage, float partialTicks);

    protected void renderHorizontalBar(GuiGraphics graphics, float fillPercentage)
    {
        if (fillPercentage > 0) getForegroundSprite(fillPercentage).partialHorizontalBlit(graphics, getX() + 1, getY() + 1, Mth.clamp(fillPercentage, 0f, 1f));
    }

    protected void renderVerticalBar(GuiGraphics graphics, float fillPercentage)
    {
        if (fillPercentage > 0) getForegroundSprite(fillPercentage).partialVerticalBlit(graphics, getX() + 1, getY() + 1, Mth.clamp(fillPercentage, 0f, 1f));
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
        return backgroundSprite.width();
    }

    @Override
    public int getHeight()
    {
        return backgroundSprite.height();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        backgroundSprite.singleBlit(graphics, getX(), getY());
        renderForeground(graphics, mouseX, mouseY, getFillPercentage(), partialTicks);
    }

    public abstract static class HorizontalBar extends FillBarWidget
    {
        protected HorizontalBar(int x, int y, UnmanagedSprite backgroundSprite)
        {
            super(x, y, backgroundSprite);
        }

        @Override
        protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float fillPercentage, float partialTicks)
        {
            renderHorizontalBar(graphics, fillPercentage);
        }
    }

    public abstract static class VerticalBar extends FillBarWidget
    {
        protected VerticalBar(int x, int y, UnmanagedSprite backgroundSprite)
        {
            super(x, y, backgroundSprite);
        }

        @Override
        protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float fillPercentage, float partialTicks)
        {
            renderVerticalBar(graphics, fillPercentage);
        }
    }
}