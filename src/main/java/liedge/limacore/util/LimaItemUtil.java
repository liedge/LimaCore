package liedge.limacore.util;

import liedge.limacore.lib.ModResources;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.transfer.access.ItemAccess;

public final class LimaItemUtil
{
    private LimaItemUtil() {}

    //#region Creative tab helpers
    public static CreativeModeTab.Builder tabBuilderWithTitle(Identifier id)
    {
        return CreativeModeTab.builder()
                .title(Component.translatable(ModResources.prefixedIdLangKey("creative_tab", id)));
    }
    //#endregion

    //#region Capability check helpers
    public static boolean hasValidCapability(ItemAccess context, ItemCapability<?, ItemAccess> capability)
    {
        return context.getCapability(capability) != null;
    }

    public static boolean hasItemHandlerCapability(ItemAccess context)
    {
        return hasValidCapability(context, Capabilities.Item.ITEM);
    }

    public static boolean hasEnergyCapability(ItemAccess context)
    {
        return hasValidCapability(context, Capabilities.Energy.ITEM);
    }

    public static boolean hasEnergyCapability(ItemStack stack)
    {
        return hasEnergyCapability(ItemAccess.forStack(stack));
    }

    public static boolean hasFluidHandlerCapability(ItemAccess context)
    {
        return hasValidCapability(context, Capabilities.Fluid.ITEM);
    }
    //#endregion
}