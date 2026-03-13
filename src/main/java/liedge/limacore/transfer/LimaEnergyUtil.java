package liedge.limacore.transfer;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.util.LimaTextUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;

import static liedge.limacore.lib.math.LimaCoreMath.*;

public final class LimaEnergyUtil
{
    private LimaEnergyUtil() {}

    // Item access energy handlers
    private static ItemAccessEnergyHandler createItemEnergy(ItemStack stack, int defaultCapacity, int transferRate)
    {
        int capacity = stack.getOrDefault(LimaCoreDataComponents.ENERGY_CAPACITY, defaultCapacity);
        return new ItemAccessEnergyHandler(ItemAccess.forStack(stack), LimaCoreDataComponents.ENERGY.get(), capacity, transferRate);
    }

    public static ItemAccessEnergyHandler createUnlimitedTransferItemEnergy(ItemStack stack, int defaultCapacity)
    {
        return createItemEnergy(stack, defaultCapacity, Integer.MAX_VALUE);
    }

    public static ItemAccessEnergyHandler createStandardTransferItemEnergy(ItemStack stack, int defaultCapacity, int defaultTransferRate)
    {
        int transferRate = stack.getOrDefault(LimaCoreDataComponents.ENERGY_TRANSFER_RATE, defaultTransferRate);
        return createItemEnergy(stack, defaultCapacity, transferRate);
    }

    // Misc
    public static float getFillPercentage(EnergyHandler handler)
    {
        return LimaCoreMath.divideFloat(handler.getAmountAsInt(), handler.getCapacityAsInt());
    }

    public static String toEnergyString(int energy)
    {
        if (energy >= BILLION)
        {
            return LimaTextUtil.format2PlaceDecimal(energy / (double) BILLION) + " GCE";
        }
        else if (energy >= MILLION)
        {
            return LimaTextUtil.format2PlaceDecimal(energy / (double) MILLION) + " MCE";
        }
        else if (energy >= 10_000) // Only abbreviate to kilo after 10k for greater precision/readability
        {
            return LimaTextUtil.format2PlaceDecimal(energy / (double) THOUSAND) + " kCE";
        }
        else
        {
            return energy + " CE";
        }
    }

    public static String toEnergyPerTickString(int energy)
    {
        return toEnergyString(energy) + "/t";
    }

    public static String toEnergyStoredString(int energy, int capacity)
    {
        return toEnergyString(energy) + "/" + toEnergyString(capacity);
    }

    public static String toEnergyStoredString(EnergyHandler handler)
    {
        return toEnergyStoredString(handler.getAmountAsInt(), handler.getCapacityAsInt());
    }
}