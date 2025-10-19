package liedge.limacore.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Simple implementation of {@link RecipeSerializer} for a single codec/stream codec pair.
 */
public record LimaRecipeSerializer<R extends Recipe<?>>(ResourceLocation id, MapCodec<R> codec, StreamCodec<RegistryFriendlyByteBuf, R> streamCodec) implements RecipeSerializer<R>
{
    public static <R extends Recipe<?>> LimaRecipeSerializer<R> of(ResourceLocation id, MapCodec<R> codec, StreamCodec<RegistryFriendlyByteBuf, R> streamCodec)
    {
        return new LimaRecipeSerializer<>(id, codec, streamCodec);
    }

    public static <R extends LimaCustomRecipe<?>> Builder<R> builder(LimaCustomRecipe.RecipeFactory<R> factory)
    {
        return new Builder<>(factory);
    }

    public static final class Builder<R extends LimaCustomRecipe<?>> extends LimaRecipeSerializerBuilder<R, LimaRecipeSerializer<R>, Builder<R>>
    {
        private final LimaCustomRecipe.RecipeFactory<R> factory;

        public Builder(LimaCustomRecipe.RecipeFactory<R> factory)
        {
            this.factory = factory;
        }

        @Override
        public LimaRecipeSerializer<R> build(ResourceLocation id)
        {
            MapCodec<R> mapCodec = RecordCodecBuilder.<R>mapCodec(instance -> commonFields(instance).apply(instance, factory))
                    .validate(LimaCustomRecipe::checkNotEmpty);
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