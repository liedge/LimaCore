package liedge.limacore.client.gui;

import net.minecraft.client.gui.components.Renderable;

public interface LimaRenderable extends Renderable
{
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    default void extractTooltip(TooltipLineConsumer consumer, int mouseX, int mouseY) { }

    default boolean isMouseOver(double mouseX, double mouseY)
    {
        return LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());
    }
}