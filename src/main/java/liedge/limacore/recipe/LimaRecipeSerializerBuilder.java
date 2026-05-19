package liedge.limacore.recipe;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.recipe.input.RecipeFluidInput;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

public abstract class LimaRecipeSerializerBuilder<R extends LimaCustomRecipe<?>, B extends LimaRecipeSerializerBuilder<R, B>>
{
    protected MapCodec<List<RecipeItemInput>> itemIngredientCodec = RecipeItemInput.EMPTY_LIST_CODEC;
    protected MapCodec<List<RecipeFluidInput>> fluidIngredientCodec = RecipeFluidInput.EMPTY_LIST_CODEC;
    protected MapCodec<List<ItemResult>> itemResultCodec = ItemResult.LIST_UNIT_MAP_CODEC;
    protected MapCodec<List<FluidResult>> fluidResultCodec = FluidResult.LIST_UNIT_MAP_CODEC;

    protected LimaRecipeSerializerBuilder() { }

    public B withItemIngredients(int min, int max)
    {
        itemIngredientCodec = RecipeItemInput.listMapCodec(min, max);
        return thisBuilder();
    }

    public B withItemIngredients(int max)
    {
        return withItemIngredients(1, max);
    }

    public B withOptionalItemIngredients(int max)
    {
        return withItemIngredients(0, max);
    }

    public B withFluidIngredients(int min, int max)
    {
        fluidIngredientCodec = RecipeFluidInput.listMapCodec(min, max);
        return thisBuilder();
    }

    public B withFluidIngredients(int max)
    {
        return withFluidIngredients(1, max);
    }

    public B withOptionalFluidIngredients(int max)
    {
        return withFluidIngredients(0, max);
    }

    public B withItemResults(int min, int max)
    {
        itemResultCodec = ItemResult.listMapCodec(min, max);
        return thisBuilder();
    }

    public B withItemResults(int max)
    {
        return withItemResults(1, max);
    }

    public B withOptionalItemResults(int max)
    {
        return withItemResults(0, max);
    }

    public B withFluidResults(int min, int max)
    {
        fluidResultCodec = FluidResult.listMapCodec(min, max);
        return thisBuilder();
    }

    public B withFluidResults(int max)
    {
        return withFluidResults(1, max);
    }

    public B withOptionalFluidResults(int max)
    {
        return withFluidResults(0, max);
    }

    protected abstract MapCodec<R> buildCodec();

    protected abstract StreamCodec<RegistryFriendlyByteBuf, R> buildStreamCodec();

    public final RecipeSerializer<R> build()
    {
        MapCodec<R> mapCodec = buildCodec().validate(LimaCustomRecipe::checkNotEmpty);
        return new RecipeSerializer<>(mapCodec, buildStreamCodec());
    }

    protected Products.P4<RecordCodecBuilder.Mu<R>, List<RecipeItemInput>, List<RecipeFluidInput>, List<ItemResult>, List<FluidResult>> commonFields(RecordCodecBuilder.Instance<R> instance)
    {
        return instance.group(itemIngredientCodec.forGetter(LimaCustomRecipe::getItemInputs),
                fluidIngredientCodec.forGetter(LimaCustomRecipe::getFluidInputs),
                itemResultCodec.forGetter(LimaCustomRecipe::getItemResults),
                fluidResultCodec.forGetter(LimaCustomRecipe::getFluidResults));
    }

    @SuppressWarnings("unchecked")
    private B thisBuilder()
    {
        return (B) this;
    }
}