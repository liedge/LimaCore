package liedge.limacore.client.gui;

import net.minecraft.client.gui.GuiGraphics;

public abstract class VariableBarWidget implements LimaRenderable
{
    private final int x;
    private final int y;

    protected VariableBarWidget(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    protected abstract UnmanagedSprite backgroundSprite();

    protected abstract UnmanagedSprite foregroundSprite();

    protected abstract float fillPercent();

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
        return backgroundSprite().width();
    }

    @Override
    public int getHeight()
    {
        return backgroundSprite().height();
    }

    public abstract static class HorizontalBar extends VariableBarWidget
    {
        protected HorizontalBar(int x, int y)
        {
            super(x, y);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
        {
            backgroundSprite().singleBlit(graphics, getX(), getY());

            float fill = fillPercent();
            if (fill > 0)
            {
                foregroundSprite().partialHorizontalBlit(graphics, getX() + 1, getY() + 1, fill);
            }
        }
    }

    public abstract static class VerticalBar extends VariableBarWidget
    {
        protected VerticalBar(int x, int y)
        {
            super(x, y);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
        {
            backgroundSprite().singleBlit(graphics, getX(), getY());

            float fill = fillPercent();
            if (fill > 0)
            {
                foregroundSprite().partialVerticalBlit(graphics, getX() + 1, getY() + 1, fill);
            }
        }
    }
}