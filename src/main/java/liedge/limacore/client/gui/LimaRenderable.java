package liedge.limacore.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;

public interface LimaRenderable extends Renderable
{
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    @Override
    void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

    default boolean hasTooltip()
    {
        return false;
    }

    default void createWidgetTooltip(TooltipLineConsumer consumer) {}

    default boolean isMouseOver(double mouseX, double mouseY)
    {
        return LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, getX(), getY(), getWidth(), getHeight());
    }
}