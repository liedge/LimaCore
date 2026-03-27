package liedge.limacore.recipe.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.EmptyFieldMapCodec;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidInstance;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.List;

public record FluidResult(Holder<Fluid> fluid, ResultCount count, DataComponentPatch components, String group, boolean required) implements RecipeResult<Fluid, FluidResource>
{
    public static final Codec<FluidResult> CODEC = RecipeResult.codec(FluidInstance.FLUID_HOLDER_CODEC, ResultCount.codec(Integer.MAX_VALUE).fieldOf("amount"), FluidResult::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidResult> STREAM_CODEC = RecipeResult.streamCodec(FluidInstance.FLUID_HOLDER_STREAM_CODEC, FluidResult::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidResult>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final String MAP_CODEC_KEY = "fluid_results";
    public static final MapCodec<List<FluidResult>> LIST_UNIT_MAP_CODEC = EmptyFieldMapCodec.emptyListField(MAP_CODEC_KEY);

    public static MapCodec<List<FluidResult>> listMapCodec(int minInclusive, int maxInclusive)
    {
        return RecipeResult.listCodec(CODEC, MAP_CODEC_KEY, minInclusive, maxInclusive);
    }

    public static FluidResult of(Holder<Fluid> fluid, ResultCount count, DataComponentPatch components, String group, boolean required)
    {
        return new FluidResult(fluid, count, components, group, required);
    }

    public static FluidResult of(Fluid fluid, ResultCount count, DataComponentPatch components, String group, boolean required)
    {
        return new FluidResult(LimaRegistryUtil.builtInHolder(fluid), count, components, group, required);
    }

    public static FluidResult of(Holder<Fluid> fluid, ResultCount count, DataComponentPatch components)
    {
        return of(fluid, count, components, NO_GROUP, true);
    }

    public static FluidResult of(Fluid fluid, ResultCount count, DataComponentPatch components)
    {
        return of(LimaRegistryUtil.builtInHolder(fluid), count, components);
    }

    public static FluidResult of(Holder<Fluid> fluid, ResultCount count, String group, boolean required)
    {
        return of(fluid, count, DataComponentPatch.EMPTY, group, required);
    }

    public static FluidResult of(Fluid fluid, ResultCount count, String group, boolean required)
    {
        return of(fluid, count, DataComponentPatch.EMPTY, group, required);
    }

    public static FluidResult of(Holder<Fluid> fluid, ResultCount count)
    {
        return of(fluid, count, DataComponentPatch.EMPTY);
    }

    public static FluidResult of(Fluid fluid, ResultCount count)
    {
        return of(fluid, count, DataComponentPatch.EMPTY);
    }

    public static FluidResult of(Holder<Fluid> fluid, int amount)
    {
        return of(fluid, ResultCount.exactly(amount), DataComponentPatch.EMPTY);
    }

    public static FluidResult of(Fluid fluid, int amount)
    {
        return of(LimaRegistryUtil.builtInHolder(fluid), amount);
    }

    public static FluidResult fromVanilla(FluidStackTemplate template)
    {
        return of(template.fluid(), ResultCount.exactly(template.amount()), template.components());
    }

    @Override
    public Holder<Fluid> typeHolder()
    {
        return fluid;
    }

    @Override
    public FluidResource getResource()
    {
        return FluidResource.of(fluid, components);
    }

    public FluidStack display(int amount)
    {
        if (amount <= 0) return FluidStack.EMPTY;
        return new FluidStack(fluid, amount, components);
    }

    public FluidStack display()
    {
        return display(count.max());
    }
}