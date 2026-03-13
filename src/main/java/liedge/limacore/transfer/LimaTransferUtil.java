package liedge.limacore.transfer;

import liedge.limacore.util.LimaTextUtil;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jspecify.annotations.Nullable;

public final class LimaTransferUtil
{
    private LimaTransferUtil() {}

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

    @Nullable
    public static <T extends Resource> ResourceHandler<T> mergeInputOutputHandlers(@Nullable ResourceHandler<T> input, @Nullable ResourceHandler<T> output)
    {
        if (input != null && output != null)
        {
            return new CombinedResourceHandler<>(input, output);
        }
        else if (input != null)
        {
            return input;
        }
        else
        {
            return output;
        }
    }
}