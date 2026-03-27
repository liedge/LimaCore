package liedge.limacore.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public abstract class LimaBaseButton extends AbstractButton implements LimaRenderable
{
    protected LimaBaseButton(int x, int y, int width, int height, Component message)
    {
        super(x, y, width, height, message);
    }

    protected LimaBaseButton(int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        Identifier sprite = isHoveredOrFocused() ? focusedSprite() : unfocusedSprite();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), width, height);

    }

    protected abstract Identifier unfocusedSprite();

    protected Identifier focusedSprite()
    {
        return unfocusedSprite();
    }
}