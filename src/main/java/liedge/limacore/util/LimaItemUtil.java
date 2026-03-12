package liedge.limacore.util;

import liedge.limacore.lib.ModResources;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import java.util.function.Predicate;

public final class LimaItemUtil
{
    private LimaItemUtil() {}

    public static final Predicate<ItemStack> ALWAYS_TRUE = stack -> true;
    public static final Predicate<ItemStack> ALWAYS_FALSE = stack -> false;

    public static boolean areItemsMergeCompatible(ItemStack existing, ItemStack other)
    {
        return existing.isEmpty() || ItemStack.isSameItemSameComponents(other, existing);
    }

    public static boolean canMergeItemStacks(ItemStack existing, ItemStack other, boolean allowPartialMerge)
    {
        if (areItemsMergeCompatible(existing, other))
        {
            int limit;
            if (existing.isEmpty())
            {
                limit = other.getMaxStackSize();
            }
            else
            {
                limit = existing.getMaxStackSize();
                limit -= existing.getCount();
            }

            if (limit <= 0) return false;

            return allowPartialMerge || other.getCount() <= limit;
        }

        return false;
    }

    public static boolean canMergeItemStacks(ItemStack existing, ItemStack other)
    {
        return canMergeItemStacks(existing, other, false);
    }

    //#region Creative tab helpers
    public static CreativeModeTab.Builder tabBuilderWithTitle(Identifier id)
    {
        return CreativeModeTab.builder()
                .title(Component.translatable(ModResources.prefixedIdLangKey("creative_tab", id)));
    }
    //#endregion

    //#region Capability check helpers
    public static boolean hasValidCapability(ItemCapability<?, ItemAccess> capability, ItemStack stack, ItemAccess context)
    {
        return stack.getCapability(capability, context) != null;
    }

    public static boolean hasEnergyCapability(ItemStack stack, ItemAccess context)
    {
        return hasValidCapability(Capabilities.Energy.ITEM, stack, context);
    }

    public static boolean hasItemHandlerCapability(ItemStack stack, ItemAccess context)
    {
        return hasValidCapability(Capabilities.Item.ITEM, stack, context);
    }

    public static boolean hasFluidHandlerCapability(ItemStack stack, ItemAccess context)
    {
        return hasValidCapability(Capabilities.Fluid.ITEM, stack, context);
    }
    //#endregion
}