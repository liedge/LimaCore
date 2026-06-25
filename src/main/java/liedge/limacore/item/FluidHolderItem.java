package liedge.limacore.item;

import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.transfer.LimaTransferUtil;
import liedge.limacore.util.LimaCoreObjects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

public interface FluidHolderItem extends ItemLike
{
    static @Nullable ResourceHandler<FluidResource> createItemFluids(ItemStack stack, ItemAccess access)
    {
        FluidHolderItem item = LimaCoreObjects.cast(FluidHolderItem.class, stack.getItem(), "Not a fluid holder item.");
        return item.getFluids(stack, access);
    }

    default int getBaseFluidCapacity(ItemStack stack)
    {
        return 0;
    }

    default int getBaseFluidTransferRate(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }

    default int getFluidCapacity(ItemStack stack)
    {
        return stack.getOrDefault(LimaCoreDataComponents.FLUID_CAPACITY, getBaseFluidCapacity(stack));
    }

    default int getFluidTransferRate(ItemStack stack)
    {
        return stack.getOrDefault(LimaCoreDataComponents.FLUID_TRANSFER_RATE, getBaseFluidTransferRate(stack));
    }

    default @Nullable ResourceHandler<FluidResource> getFluids(ItemStack stack, ItemAccess access)
    {
        return LimaTransferUtil.createItemFluids(access, getFluidCapacity(stack), getFluidTransferRate(stack));
    }
}