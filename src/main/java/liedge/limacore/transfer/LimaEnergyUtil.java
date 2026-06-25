package liedge.limacore.transfer;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.registry.game.LimaCoreDataComponents;
import liedge.limacore.util.LimaTextUtil;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import static liedge.limacore.lib.math.LimaCoreMath.*;

public final class LimaEnergyUtil
{
    private LimaEnergyUtil() {}

    // Item access energy handlers
    public static @Nullable EnergyHandler createItemEnergy(ItemAccess context, int capacity, int transferRate)
    {
        if (capacity <= 0 || transferRate <= 0) return null;

        return new ItemAccessEnergyHandler(context, LimaCoreDataComponents.ENERGY.get(), capacity, transferRate);
    }

    // Usage
    public static boolean useExact(EnergyHandler handler, int amount, @Nullable TransactionContext transaction)
    {
        TransferPreconditions.checkNonNegative(amount);

        try (Transaction tx = Transaction.open(transaction))
        {
            int extracted = handler.extract(amount, tx);

            if (extracted == amount)
            {
                tx.commit();
                return true;
            }
        }

        return false;
    }

    // Misc
    public static float getFillPercentage(EnergyHandler handler)
    {
        return LimaCoreMath.getFloatRatio(handler.getAmountAsInt(), handler.getCapacityAsInt());
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