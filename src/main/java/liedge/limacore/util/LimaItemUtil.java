package liedge.limacore.util;

import liedge.limacore.lib.ModResources;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;

import java.util.function.Predicate;

public final class LimaItemUtil
{
    private LimaItemUtil() {}

    public static final Predicate<ItemStack> ALWAYS_TRUE = stack -> true;

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

    public static <T> InteractionResultHolder<T> sidedFail(T stack, boolean isClientSide)
    {
        return isClientSide ? InteractionResultHolder.fail(stack) : InteractionResultHolder.consume(stack);
    }

    //#region Creative tab helpers
    public static CreativeModeTab.Builder tabBuilderWithTitle(ResourceLocation id)
    {
        return CreativeModeTab.builder()
                .title(Component.translatable(ModResources.prefixedIdLangKey("creative_tab", id)));
    }
    //#endregion

    //#region Capability check helpers
    public static boolean hasValidCapability(ItemCapability<?, Void> capability, ItemStack stack)
    {
        return stack.getCapability(capability) != null;
    }

    public static boolean hasEnergyCapability(ItemStack stack)
    {
        return hasValidCapability(Capabilities.EnergyStorage.ITEM, stack);
    }

    public static boolean hasItemHandlerCapability(ItemStack stack)
    {
        return hasValidCapability(Capabilities.ItemHandler.ITEM, stack);
    }

    public static boolean hasFluidHandlerCapability(ItemStack stack)
    {
        return hasValidCapability(Capabilities.FluidHandler.ITEM, stack);
    }
    //#endregion
}