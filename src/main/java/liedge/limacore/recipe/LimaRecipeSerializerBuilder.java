package liedge.limacore.recipe;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.result.ItemResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public abstract class LimaRecipeSerializerBuilder<R extends LimaCustomRecipe<?>, S extends RecipeSerializer<R>, B extends LimaRecipeSerializerBuilder<R, S, B>>
{
    protected MapCodec<List<SizedIngredient>> itemIngredientCodec = LimaCoreCodecs.ITEM_INGREDIENTS_UNIT;
    protected MapCodec<List<SizedFluidIngredient>> fluidIngredientCodec = LimaCoreCodecs.FLUID_INGREDIENTS_UNIT;
    protected MapCodec<List<ItemResult>> itemResultCodec = ItemResult.LIST_UNIT_MAP_CODEC;
    protected MapCodec<List<FluidStack>> fluidResultCodec = LimaCoreCodecs.FLUID_RESULTS_UNIT;

    protected StreamCodec<RegistryFriendlyByteBuf, List<SizedIngredient>> itemIngredientStreamCodec = LimaStreamCodecs.ITEM_INGREDIENTS_UNIT;
    protected StreamCodec<RegistryFriendlyByteBuf, List<SizedFluidIngredient>> fluidIngredientStreamCodec = LimaStreamCodecs.FLUID_INGREDIENTS_UNIT;
    protected StreamCodec<RegistryFriendlyByteBuf, List<ItemResult>> itemResultStreamCodec = ItemResult.LIST_UNIT_STREAM_CODEC;
    protected StreamCodec<RegistryFriendlyByteBuf, List<FluidStack>> fluidResultStreamCodec = LimaStreamCodecs.FLUID_RESULTS_UNIT;

    protected LimaRecipeSerializerBuilder() { }

    public B withItemIngredients(int min, int max)
    {
        itemIngredientCodec = LimaCoreCodecs.sizedIngredients(min, max);
        itemIngredientStreamCodec = LimaStreamCodecs.sizedIngredients(min, max);
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
        fluidIngredientCodec = LimaCoreCodecs.sizedFluidIngredients(min, max);
        fluidIngredientStreamCodec = LimaStreamCodecs.sizedFluidIngredients(min, max);
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

    protected Products.P4<RecordCodecBuilder.Mu<R>, List<SizedIngredient>, List<SizedFluidIngredient>, List<ItemResult>, List<FluidStack>> commonFields(RecordCodecBuilder.Instance<R> instance)
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