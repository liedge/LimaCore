package liedge.limacore.client.gui;

public abstract class BaseLimaRenderable implements LimaRenderable
{
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    protected BaseLimaRenderable(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return LimaRenderable.super.isMouseOver(mouseX, mouseY);
    }
}