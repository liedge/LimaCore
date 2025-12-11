package liedge.limacore.recipe.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.EmptyFieldMapCodec;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class FluidResult extends StackBaseResult<Fluid, FluidStack>
{
    public static final Codec<FluidResult> CODEC = StackBaseResult.codec(FluidStack.FLUID_NON_EMPTY_CODEC, "amount", Integer.MAX_VALUE, FluidResult::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidResult> STREAM_CODEC = StackBaseResult.streamCodec(LimaStreamCodecs.FLUID_HOLDER, FluidResult::new);
    public static final String MAP_CODEC_KEY = "fluid_results";
    public static final MapCodec<List<FluidResult>> LIST_UNIT_MAP_CODEC = EmptyFieldMapCodec.emptyListField(MAP_CODEC_KEY);

    public static MapCodec<List<FluidResult>> listMapCodec(int min, int max)
    {
        return createListMapCodec(CODEC, MAP_CODEC_KEY, min, max);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, List<FluidResult>> listStreamCodec(int min, int max)
    {
        return STREAM_CODEC.apply(LimaStreamCodecs.asClampedList(min, max));
    }

    public static FluidResult create(Holder<Fluid> base, @Nullable DataComponentPatch components, ResultCount count, float chance, ResultPriority priority)
    {
        return new FluidResult(base, Objects.requireNonNullElse(components, DataComponentPatch.EMPTY), count, chance, priority);
    }

    public static FluidResult create(Fluid fluid, @Nullable DataComponentPatch components, ResultCount count, float chance, ResultPriority priority)
    {
        return create(LimaRegistryUtil.getHolder(fluid), components, count, chance, priority);
    }

    public static FluidResult create(FluidStack stack, float chance, ResultPriority priority, @Nullable ResultCount count)
    {
        count = count != null ? count : ResultCount.exactly(stack.getAmount());
        return create(stack.getFluid(), stack.getComponentsPatch(), count, chance, priority);
    }

    public static FluidResult create(FluidStack stack, float chance, ResultPriority priority)
    {
        return create(stack, chance, priority, null);
    }

    private FluidResult(Holder<Fluid> base, DataComponentPatch components, ResultCount count, float chance, ResultPriority priority)
    {
        super(base, components, count, chance, priority);
    }

    @Override
    protected FluidStack createStack(int stackSize)
    {
        return new FluidStack(base, stackSize, components);
    }

    @Override
    protected FluidStack getEmptyStack()
    {
        return FluidStack.EMPTY;
    }

    public Fluid getFluid()
    {
        return base.value();
    }
}