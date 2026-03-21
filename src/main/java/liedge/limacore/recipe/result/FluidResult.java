package liedge.limacore.recipe.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.EmptyFieldMapCodec;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import javax.annotation.Nullable;
import java.util.List;

public final class FluidResult extends ResourceResult<FluidResource>
{
    public static final Codec<FluidResult> CODEC = codec(FluidResource.CODEC.fieldOf("fluid"), Integer.MAX_VALUE, FluidResult::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidResult> STREAM_CODEC = streamCodec(FluidResource.STREAM_CODEC, FluidResult::new);
    public static final String MAP_CODEC_KEY = "fluid_results";
    public static final MapCodec<List<FluidResult>> LIST_UNIT_MAP_CODEC = EmptyFieldMapCodec.emptyListField(MAP_CODEC_KEY);

    public static MapCodec<List<FluidResult>> listMapCodec(int min, int max)
    {
        return listMapCodec(CODEC, MAP_CODEC_KEY, min, max);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, List<FluidResult>> listStreamCodec(int min, int max)
    {
        return STREAM_CODEC.apply(LimaStreamCodecs.asClampedList(min, max));
    }

    public static FluidResult create(FluidResource resource, ResultCount count, float chance, ResultPriority priority)
    {
        return new FluidResult(resource, count, chance, priority);
    }

    public static FluidResult create(FluidStack stack, float chance, ResultPriority priority, @Nullable ResultCount count)
    {
        FluidResource resource = FluidResource.of(stack);
        count = count != null ? count : ResultCount.exactly(stack.getAmount());
        return create(resource, count, chance, priority);
    }

    public static FluidResult create(FluidStack stack, float chance, ResultPriority priority)
    {
        return create(stack, chance, priority, null);
    }

    public static FluidResult create(Fluid fluid, ResultCount count, float chance, ResultPriority priority)
    {
        return create(FluidResource.of(fluid), count, chance, priority);
    }

    public static FluidResult create(Holder<Fluid> fluidHolder, ResultCount count, float chance, ResultPriority priority)
    {
        return create(fluidHolder.value(), count, chance, priority);
    }

    private FluidResult(FluidResource resource, ResultCount count, float chance, ResultPriority priority)
    {
        super(resource, count, chance, priority);
    }

    public Fluid getFluid()
    {
        return resource.getFluid();
    }
}