package liedge.limacore.capability.fluid;

import liedge.limacore.util.LimaTextUtil;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public final class LimaFluidUtil
{
    private LimaFluidUtil() {}

    public static final String MILLIBUCKET_UNIT = "mB";
    public static final String BUCKET_UNIT = "B";

    public static String formatCompactFluidAmount(int amount)
    {
        if (amount < FluidType.BUCKET_VOLUME)
        {
            return amount + MILLIBUCKET_UNIT;
        }
        else
        {
            return LimaTextUtil.format2PlaceDecimal(amount / (double) FluidType.BUCKET_VOLUME) + BUCKET_UNIT;
        }
    }

    public static String formatStoredFluidMillibucket(int stored, int capacity)
    {
        return LimaTextUtil.formatWholeNumber(stored) + '/' + LimaTextUtil.formatWholeNumber(capacity) + ' ' + MILLIBUCKET_UNIT;
    }

    public static int transferFluidsBetween(IFluidHandler source, IFluidHandler destination, int maxTransfer, IFluidHandler.FluidAction action)
    {
        FluidStack sourceFluid = source.drain(maxTransfer, IFluidHandler.FluidAction.SIMULATE);
        int accepted = destination.fill(sourceFluid, IFluidHandler.FluidAction.SIMULATE);

        if (action.simulate()) return accepted;

        sourceFluid = source.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
        accepted = destination.fill(sourceFluid, IFluidHandler.FluidAction.EXECUTE);

        return accepted;
    }
}