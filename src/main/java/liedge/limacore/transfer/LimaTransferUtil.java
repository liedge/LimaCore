package liedge.limacore.transfer;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.util.LimaTextUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public final class LimaTransferUtil
{
    private LimaTransferUtil() {}

    public static final Predicate<ItemResource> ALL_ITEMS = Predicates.alwaysTrue();
    public static final Predicate<FluidResource> ALL_FLUIDS = Predicates.alwaysTrue();

    public static final String MILLIBUCKET_UNIT = "mB";
    public static final String BUCKET_UNIT = "B";

    public static <T> Optional<NonNullList<T>> loadSizedResources(ValueInput input, String key, Codec<NonNullList<T>> stacksCodec, int minSize, T emptyStack)
    {
        return input.read(key, stacksCodec).map(list ->
        {
            NonNullList<T> fixedList = NonNullList.withSize(Math.max(list.size(), minSize), emptyStack);

            for (int i = 0; i < list.size(); i++)
            {
                fixedList.set(i, list.get(i));
            }

            return fixedList;
        });
    }

    public static <T extends ResourceHandler<?> & ValueIOSerializable> void loadBlockResources(ValueInput global, String globalKey, Function<BlockContentsType, @Nullable T> accessor)
    {
        ValueInput input = global.child(globalKey).orElse(null);
        if (input == null) return;

        for (BlockContentsType type : BlockContentsType.values())
        {
            T handler = accessor.apply(type);
            if (handler != null) input.child(type.getSerializedName()).ifPresent(handler::deserialize);
        }
    }

    public static <T extends ResourceHandler<?> & ValueIOSerializable> void saveBlockResources(ValueOutput global, String globalKey, Function<BlockContentsType, @Nullable T> accessor)
    {
        ValueOutput output = global.child(globalKey);

        for (BlockContentsType type : BlockContentsType.values())
        {
            T handler = accessor.apply(type);
            if (handler != null) handler.serialize(output.child(type.getSerializedName()));
        }
    }

    public static boolean canMergeIntoIndex(ResourceHandler<ItemResource> handler, int index, ItemStack toInsert)
    {
        ItemResource current = handler.getResource(index);
        if (!(current.isEmpty() || current.matches(toInsert))) return false;

        int limit = handler.getCapacityAsInt(index, current) - handler.getAmountAsInt(index);
        return toInsert.getCount() <= limit;
    }

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