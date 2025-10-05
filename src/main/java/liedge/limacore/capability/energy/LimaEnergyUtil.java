package liedge.limacore.capability.energy;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.util.LimaTextUtil;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.energy.IEnergyStorage;

import static liedge.limacore.lib.math.LimaCoreMath.*;

public final class LimaEnergyUtil
{
    private LimaEnergyUtil() {}

    public static int transferEnergyBetween(IEnergyStorage source, IEnergyStorage destination, int maxTransfer, boolean simulate)
    {
        int extracted = source.extractEnergy(maxTransfer, true);
        int accepted = destination.receiveEnergy(extracted, true);

        if (simulate) return accepted;

        accepted = source.extractEnergy(accepted, false);
        return destination.receiveEnergy(accepted, false);
    }

    public static float getFillPercentage(IEnergyStorage storage)
    {
        return LimaCoreMath.divideFloat(storage.getEnergyStored(), storage.getMaxEnergyStored());
    }

    public static float getClampedFillPercentage(IEnergyStorage storage)
    {
        return Mth.clamp(getFillPercentage(storage), 0f, 1f);
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

    public static String toEnergyStoredString(IEnergyStorage storage)
    {
        return toEnergyStoredString(storage.getEnergyStored(), storage.getMaxEnergyStored());
    }

    public static int receiveWithoutLimit(IEnergyStorage storage, final int toReceive, boolean simulate)
    {
        int totalReceived = 0;

        while (totalReceived < toReceive)
        {
            int limit = (toReceive - totalReceived);
            int received = storage.receiveEnergy(limit, simulate);

            if (received > 0)
            {
                totalReceived += received;
            }
            else
            {
                break;
            }
        }

        return totalReceived;
    }

    public static int extractWithoutLimit(IEnergyStorage storage, final int toExtract, boolean simulate)
    {
        int totalExtracted = 0;

        while (totalExtracted < toExtract)
        {
            int limit = (toExtract - totalExtracted);
            int extracted = storage.extractEnergy(limit, simulate);

            if (extracted > 0)
            {
                totalExtracted += extracted;
            }
            else
            {
                break;
            }
        }

        return totalExtracted;
    }

    public static boolean consumeEnergy(IEnergyStorage storage, int toExtract, boolean ignoreLimit)
    {
        if (storage instanceof LimaEnergyStorage)
        {
            return consumeFromLimaSource((LimaEnergyStorage) storage, toExtract, ignoreLimit);
        }
        else
        {
            return consumeFromGeneralSource(storage, toExtract, ignoreLimit);
        }
    }

    private static boolean consumeFromLimaSource(LimaEnergyStorage storage, int toExtract, boolean ignoreLimit)
    {
        if (storage.extractEnergy(toExtract, true, ignoreLimit) == toExtract)
        {
            storage.extractEnergy(toExtract, false, ignoreLimit);
            return true;
        }

        return false;
    }

    private static boolean consumeFromGeneralSource(IEnergyStorage storage, int toExtract, boolean ignoreLimit)
    {
        if (!ignoreLimit && storage.extractEnergy(toExtract, true) == toExtract)
        {
            storage.extractEnergy(toExtract, false);
            return true;
        }
        else if (ignoreLimit)
        {
            if (extractWithoutLimit(storage, toExtract, true) == toExtract)
            {
                extractWithoutLimit(storage, toExtract, false);
                return true;
            }
        }

        return false;
    }
}