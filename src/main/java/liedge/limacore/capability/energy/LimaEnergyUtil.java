package liedge.limacore.capability.energy;

import liedge.limacore.util.LimaMathUtil;
import net.neoforged.neoforge.energy.IEnergyStorage;

import static liedge.limacore.util.LimaMathUtil.*;

public final class LimaEnergyUtil
{
    private LimaEnergyUtil() {}

    public static int transferEnergyBetween(IEnergyStorage source, IEnergyStorage destination, int maxTransfer, boolean simulate)
    {
        int simulatedExtract = source.extractEnergy(maxTransfer, true);
        int simulatedInsert = destination.receiveEnergy(simulatedExtract, true);

        int actualTransfer = Math.min(simulatedExtract, simulatedInsert);

        if (!simulate)
        {
            source.extractEnergy(actualTransfer, false);
            destination.receiveEnergy(actualTransfer, false);
        }

        return actualTransfer;
    }

    public static float getFillPercentage(IEnergyStorage storage)
    {
        return LimaMathUtil.divideFloat(storage.getEnergyStored(), storage.getMaxEnergyStored());
    }

    public static String formatEnergyWithSuffix(int energy)
    {
        if (energy >= BILLION)
        {
            return FORMAT_2_ROUND_FLOOR.format(energy / (double) BILLION) + " GCE";
        }
        else if (energy >= MILLION)
        {
            return FORMAT_2_ROUND_FLOOR.format(energy / (double) MILLION) + " MCE";
        }
        else if (energy >= THOUSAND)
        {
            return FORMAT_2_ROUND_FLOOR.format(energy / (double) THOUSAND) + " kCE";
        }
        else
        {
            return energy + " CE";
        }
    }

    public static String formatStoredAndTotal(IEnergyStorage storage)
    {
        return formatEnergyWithSuffix(storage.getEnergyStored()) + "/" + formatEnergyWithSuffix(storage.getMaxEnergyStored());
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