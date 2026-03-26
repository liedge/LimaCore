package liedge.limacore.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

/**
 * To be extended by {@link net.neoforged.neoforge.client.extensions.common.IClientItemExtensions}. Overrides
 * the rendering of the item by {@link net.minecraft.client.gui.GuiGraphicsExtractor}
 */
public interface ItemGuiRenderOverride extends IClientItemExtensions
{
    /**
     * This method can be used to render visuals in the GUI before an item stack is rendered.
     * Optionally, the rendering of the item stack can be skipped.
     * @return Return true to cancel rendering of the item stack, false to proceed.
     */
    boolean renderCustomGuiItem(GuiGraphicsExtractor graphics, @Nullable LivingEntity owner, @Nullable Level level, ItemStack stack, int x, int y);
}