package liedge.limacore.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

/**
 * Simple implementation of {@link RecipeSerializer} for a single codec/stream codec pair.
 */
public record LimaRecipeSerializer<R extends Recipe<?>>(ResourceLocation id, MapCodec<R> codec, StreamCodec<RegistryFriendlyByteBuf, R> streamCodec) implements RecipeSerializer<R>
{
    public static <R extends Recipe<?>> LimaRecipeSerializer<R> of(ResourceLocation id, MapCodec<R> codec, StreamCodec<RegistryFriendlyByteBuf, R> streamCodec)
    {
        return new LimaRecipeSerializer<>(id, codec, streamCodec);
    }

    public static <R extends LimaCustomRecipe<?>> Builder<R> builder()
    {
        return new Builder<>();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj instanceof LimaRecipeSerializer<?> other)
        {
            return id.equals(other.id);
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public String toString()
    {
        return id.toString();
    }

    public static final class Builder<R extends LimaCustomRecipe<?>>
    {
        private MapCodec<List<SizedIngredient>> itemIngredientCodec = LimaCoreCodecs.ITEM_INGREDIENTS_UNIT;
        private MapCodec<List<SizedFluidIngredient>> fluidIngredientCodec = LimaCoreCodecs.FLUID_INGREDIENTS_UNIT;
        private MapCodec<List<ItemResult>> itemResultCodec = ItemResult.LIST_UNIT_MAP_CODEC;
        private MapCodec<List<FluidStack>> fluidResultCodec = LimaCoreCodecs.FLUID_RESULTS_UNIT;

        private StreamCodec<RegistryFriendlyByteBuf, List<SizedIngredient>> itemIngredientStreamCodec = LimaStreamCodecs.ITEM_INGREDIENTS_UNIT;
        private StreamCodec<RegistryFriendlyByteBuf, List<SizedFluidIngredient>> fluidIngredientStreamCodec = LimaStreamCodecs.FLUID_INGREDIENTS_UNIT;
        private StreamCodec<RegistryFriendlyByteBuf, List<ItemResult>> itemResultStreamCodec = ItemResult.LIST_UNIT_STREAM_CODEC;
        private StreamCodec<RegistryFriendlyByteBuf, List<FluidStack>> fluidResultStreamCodec = LimaStreamCodecs.FLUID_RESULTS_UNIT;

        private Builder() {}

        public Builder<R> withItemIngredients(int min, int max)
        {
            itemIngredientCodec = LimaCoreCodecs.sizedIngredients(min, max);
            itemIngredientStreamCodec = LimaStreamCodecs.sizedIngredients(min, max);
            return this;
        }

        public Builder<R> withItemIngredients(int max)
        {
            return withItemIngredients(1, max);
        }

        public Builder<R> withOptionalItemIngredients(int max)
        {
            return withItemIngredients(0, max);
        }

        public Builder<R> withFluidIngredients(int min, int max)
        {
            fluidIngredientCodec = LimaCoreCodecs.sizedFluidIngredients(min, max);
            fluidIngredientStreamCodec = LimaStreamCodecs.sizedFluidIngredients(min, max);
            return this;
        }

        public Builder<R> withFluidIngredients(int max)
        {
            return withFluidIngredients(1, max);
        }

        public Builder<R> withOptionalFluidIngredients(int max)
        {
            return withFluidIngredients(0, max);
        }

        public Builder<R> withItemResults(int min, int max)
        {
            itemResultCodec = ItemResult.listMapCodec(min, max);
            itemResultStreamCodec = ItemResult.listStreamCodec(min, max);
            return this;
        }

        public Builder<R> withItemResults(int max)
        {
            return withItemResults(1, max);
        }

        public Builder<R> withOptionalItemResults(int max)
        {
            return withItemResults(0, max);
        }

        public Builder<R> withFluidResults(int min, int max)
        {
            fluidResultCodec = LimaCoreCodecs.fluidResults(min, max);
            fluidResultStreamCodec = LimaStreamCodecs.fluidResults(min, max);
            return this;
        }

        public Builder<R> withFluidResults(int max)
        {
            return withFluidResults(1, max);
        }

        public Builder<R> withOptionalFluidResults(int max)
        {
            return withFluidResults(0, max);
        }

        public LimaRecipeSerializer<R> build(ResourceLocation id, LimaCustomRecipe.RecipeFactory<R> factory)
        {
            MapCodec<R> mapCodec = RecordCodecBuilder.<R>mapCodec(instance -> instance.group(
                    itemIngredientCodec.forGetter(LimaCustomRecipe::getItemIngredients),
                    fluidIngredientCodec.forGetter(LimaCustomRecipe::getFluidIngredients),
                    itemResultCodec.forGetter(LimaCustomRecipe::getItemResults),
                    fluidResultCodec.forGetter(LimaCustomRecipe::getFluidResults))
                    .apply(instance, factory))
                    .validate(LimaCustomRecipe::checkNotEmpty); // Validation happens here
            StreamCodec<RegistryFriendlyByteBuf, R> streamCodec = StreamCodec.composite(
                    itemIngredientStreamCodec,
                    LimaCustomRecipe::getItemIngredients,
                    fluidIngredientStreamCodec,
                    LimaCustomRecipe::getFluidIngredients,
                    itemResultStreamCodec,
                    LimaCustomRecipe::getItemResults,
                    fluidResultStreamCodec,
                    LimaCustomRecipe::getFluidResults,
                    factory);

            return LimaRecipeSerializer.of(id, mapCodec, streamCodec);
        }
    }
}