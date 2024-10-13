package liedge.limacore.recipe;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public abstract class LimaSimpleRecipe<T extends LimaRecipeInput> extends LimaCustomRecipe<T>
{
    protected static <R extends LimaSimpleRecipe<?>> Products.P2<RecordCodecBuilder.Mu<R>, NonNullList<Ingredient>, ItemStack> codecStart(RecordCodecBuilder.Instance<R> instance, int minIngredients, int maxIngredients)
    {
        return instance.group(
                LimaCoreCodecs.ingredientsMapCodec(minIngredients, maxIngredients).forGetter(LimaCustomRecipe::getIngredients),
                ItemStack.CODEC.fieldOf("result").forGetter(LimaSimpleRecipe::getResultItem));
    }

    public static <R extends LimaSimpleRecipe<?>> LimaRecipeSerializer<R> ingredientRangeSerializer(ResourceLocation id, BiFunction<NonNullList<Ingredient>, ItemStack, R> factory, int minIngredients, int maxIngredients)
    {
        MapCodec<R> codec = RecordCodecBuilder.mapCodec(instance -> codecStart(instance, minIngredients, maxIngredients).apply(instance, factory));
        StreamCodec<RegistryFriendlyByteBuf, R> streamCodec = StreamCodec.composite(
                LimaStreamCodecs.ingredientsStreamCodec(minIngredients, maxIngredients), LimaCustomRecipe::getIngredients,
                ItemStack.STREAM_CODEC, LimaSimpleRecipe::getResultItem,
                factory);
        return new LimaRecipeSerializer<>(id, codec, streamCodec);
    }

    public static <R extends LimaSimpleRecipe<?>> LimaRecipeSerializer<R> maxIngredientsSerializer(ResourceLocation id, BiFunction<NonNullList<Ingredient>, ItemStack, R> factory, int maxIngredients)
    {
        return ingredientRangeSerializer(id, factory, 1, maxIngredients);
    }

    private final ItemStack resultItem;

    protected LimaSimpleRecipe(NonNullList<Ingredient> ingredients, ItemStack resultItem)
    {
        super(ingredients);
        this.resultItem = resultItem;
    }

    protected LimaSimpleRecipe(Ingredient ingredient, ItemStack resultItem)
    {
        super(ingredient);
        this.resultItem = resultItem;
    }

    public ItemStack getResultItem()
    {
        return resultItem;
    }

    @Override
    public ItemStack assemble(T input, HolderLookup.Provider registries)
    {
        return resultItem.copy();
    }

    @Override
    public ItemStack getResultItem(@Nullable HolderLookup.Provider registries)
    {
        return resultItem;
    }
}