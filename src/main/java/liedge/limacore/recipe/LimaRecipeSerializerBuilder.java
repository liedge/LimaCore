package liedge.limacore.recipe;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.ingredient.LimaSizedFluidIngredient;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.ItemResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public abstract class LimaRecipeSerializerBuilder<R extends LimaCustomRecipe<?>, S extends RecipeSerializer<R>, B extends LimaRecipeSerializerBuilder<R, S, B>>
{
    protected MapCodec<List<LimaSizedItemIngredient>> itemIngredientCodec = LimaSizedItemIngredient.LIST_UNIT_MAP_CODEC;
    protected MapCodec<List<LimaSizedFluidIngredient>> fluidIngredientCodec = LimaSizedFluidIngredient.LIST_UNIT_MAP_CODEC;
    protected MapCodec<List<ItemResult>> itemResultCodec = ItemResult.LIST_UNIT_MAP_CODEC;
    protected MapCodec<List<FluidStack>> fluidResultCodec = LimaCoreCodecs.FLUID_RESULTS_UNIT;

    protected StreamCodec<RegistryFriendlyByteBuf, List<LimaSizedItemIngredient>> itemIngredientStreamCodec = LimaStreamCodecs.unitList();
    protected StreamCodec<RegistryFriendlyByteBuf, List<LimaSizedFluidIngredient>> fluidIngredientStreamCodec = LimaStreamCodecs.unitList();
    protected StreamCodec<RegistryFriendlyByteBuf, List<ItemResult>> itemResultStreamCodec = LimaStreamCodecs.unitList();
    protected StreamCodec<RegistryFriendlyByteBuf, List<FluidStack>> fluidResultStreamCodec = LimaStreamCodecs.unitList();

    protected LimaRecipeSerializerBuilder() { }

    public B withItemIngredients(int min, int max)
    {
        itemIngredientCodec = LimaSizedItemIngredient.listMapCodec(min, max);
        itemIngredientStreamCodec = LimaSizedItemIngredient.listStreamCodec(min, max);
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
        fluidIngredientCodec = LimaSizedFluidIngredient.listMapCodec(min, max);
        fluidIngredientStreamCodec = LimaSizedFluidIngredient.listStreamCodec(min, max);
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
        itemResultStreamCodec = ItemResult.listStreamCodec(min, max);
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
        fluidResultCodec = LimaCoreCodecs.fluidResults(min, max);
        fluidResultStreamCodec = LimaStreamCodecs.fluidResults(min, max);
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

    public abstract S build(ResourceLocation id);

    protected Products.P4<RecordCodecBuilder.Mu<R>, List<LimaSizedItemIngredient>, List<LimaSizedFluidIngredient>, List<ItemResult>, List<FluidStack>> commonFields(RecordCodecBuilder.Instance<R> instance)
    {
        return instance.group(itemIngredientCodec.forGetter(LimaCustomRecipe::getItemIngredients),
                fluidIngredientCodec.forGetter(LimaCustomRecipe::getFluidIngredients),
                itemResultCodec.forGetter(LimaCustomRecipe::getItemResults),
                fluidResultCodec.forGetter(LimaCustomRecipe::getFluidResults));
    }

    @SuppressWarnings("unchecked")
    private B thisBuilder()
    {
        return (B) this;
    }
}