package liedge.limacore.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/**
 * To be extended by {@link net.neoforged.neoforge.client.extensions.common.IClientItemExtensions}. Overrides
 * the rendering of the item by {@link net.minecraft.client.gui.GuiGraphics}
 */
public interface ItemGuiRenderOverride extends IClientItemExtensions
{
    /**
     * This method can be used to render visuals in the GUI before an item stack is rendered.
     * Optionally, the rendering of the item stack can be skipped.
     * @param graphics The {@link GuiGraphics} object used for rendering
     * @param stack The item stack to be rendered
     * @param x X position of the stack being rendered
     * @param y Y position of the stack being rendered
     * @return Return true to cancel rendering of the item stack, false to proceed.
     */
    boolean renderCustomGuiItem(GuiGraphics graphics, ItemStack stack, int x, int y);
}