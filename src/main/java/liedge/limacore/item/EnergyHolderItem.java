package liedge.limacore.item;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.transfer.LimaEnergyUtil;
import liedge.limacore.util.LimaCoreObjects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jspecify.annotations.Nullable;

public interface EnergyHolderItem extends ItemLike
{
    static @Nullable EnergyHandler createItemEnergy(ItemStack stack, ItemAccess access)
    {
        EnergyHolderItem item = LimaCoreObjects.cast(EnergyHolderItem.class, stack.getItem(), "Not an energy holder item.");
        return item.getEnergy(stack, access);
    }

    default int getBaseEnergyCapacity(ItemStack stack)
    {
        return 0;
    }

    default int getBaseEnergyTransferRate(ItemStack stack)
    {
        return 0;
    }

    default int getEnergyStored(ItemStack stack)
    {
        return stack.getOrDefault(LimaCoreDataComponents.ENERGY, 0);
    }

    default int getEnergyCapacity(ItemStack stack)
    {
        return stack.getOrDefault(LimaCoreDataComponents.ENERGY_CAPACITY, getBaseEnergyCapacity(stack));
    }

    default int getEnergyTransferRate(ItemStack stack)
    {
        return stack.getOrDefault(LimaCoreDataComponents.ENERGY_TRANSFER_RATE, getBaseEnergyTransferRate(stack));
    }

    default float getChargePercentage(ItemStack stack)
    {
        return LimaCoreMath.getFloatRatio(getEnergyStored(stack), getEnergyCapacity(stack));
    }

    default @Nullable EnergyHandler getEnergy(ItemStack stack, ItemAccess access)
    {
        return LimaEnergyUtil.createItemEnergy(access, getEnergyCapacity(stack), getEnergyTransferRate(stack));
    }

    default @Nullable EnergyHandler getNoTransferLimitEnergy(ItemStack stack, ItemAccess access)
    {
        return LimaEnergyUtil.createItemEnergy(access, getEnergyCapacity(stack), Integer.MAX_VALUE);
    }
}