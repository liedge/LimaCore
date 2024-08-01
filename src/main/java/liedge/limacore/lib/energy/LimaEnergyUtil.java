package liedge.limacore.lib.energy;

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
            return FORMAT_2_ROUND_FLOOR.format(energy / (double) BILLION) + " GFE";
        }
        else if (energy >= MILLION)
        {
            return FORMAT_2_ROUND_FLOOR.format(energy / (double) MILLION) + " MFE";
        }
        else if (energy >= THOUSAND)
        {
            return FORMAT_2_ROUND_FLOOR.format(energy / (double) THOUSAND) + " kFE";
        }
        else
        {
            return energy + " FE";
        }
    }

    public static String formatStoredAndTotal(IEnergyStorage storage)
    {
        return formatEnergyWithSuffix(storage.getEnergyStored()) + "/" + formatEnergyWithSuffix(storage.getMaxEnergyStored());
    }

    public static boolean tryConsumeEnergy(IEnergyStorage storage, int toExtract)
    {
        if (storage.extractEnergy(toExtract, true) == toExtract)
        {
            storage.extractEnergy(toExtract, false);
            return true;
        }
        else
        {
            return false;
        }
    }
}