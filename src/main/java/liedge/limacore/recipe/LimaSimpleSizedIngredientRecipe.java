package liedge.limacore.recipe;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class LimaSimpleSizedIngredientRecipe<T extends LimaRecipeInput> extends LimaSizedIngredientRecipe<T>
{
    public static <R extends LimaSizedIngredientRecipe<?>> Products.P2<RecordCodecBuilder.Mu<R>, List<SizedIngredient>, ItemStack> codecStart(RecordCodecBuilder.Instance<R> instance, int minIngredients, int maxIngredients, Function<R, ItemStack> resultItemGetter)
    {
        return instance.group(
                LimaCoreCodecs.sizedIngredientsMapCodec(minIngredients, maxIngredients).forGetter(LimaSizedIngredientRecipe::getRecipeIngredients),
                ItemStack.CODEC.fieldOf("result").forGetter(resultItemGetter));
    }

    public static <R extends LimaSimpleSizedIngredientRecipe<?>> Products.P2<RecordCodecBuilder.Mu<R>, List<SizedIngredient>, ItemStack> codecStart(RecordCodecBuilder.Instance<R> instance, int minIngredients, int maxIngredients)
    {
        return codecStart(instance, minIngredients, maxIngredients, LimaSimpleSizedIngredientRecipe::getResultItem);
    }

    public static <R extends LimaSimpleSizedIngredientRecipe<?>> LimaRecipeSerializer<R> ingredientRangeSerializer(ResourceLocation id, BiFunction<List<SizedIngredient>, ItemStack, R> factory, int minIngredients, int maxIngredients)
    {
        MapCodec<R> codec = RecordCodecBuilder.mapCodec(instance -> codecStart(instance, minIngredients, maxIngredients).apply(instance, factory));
        StreamCodec<RegistryFriendlyByteBuf, R> streamCodec = StreamCodec.composite(
                LimaStreamCodecs.sizedIngredientsStreamCodec(minIngredients, maxIngredients), LimaSizedIngredientRecipe::getRecipeIngredients,
                ItemStack.STREAM_CODEC, LimaSimpleSizedIngredientRecipe::getResultItem,
                factory);
        return new LimaRecipeSerializer<>(id, codec, streamCodec);
    }

    public static <R extends LimaSimpleSizedIngredientRecipe<?>> LimaRecipeSerializer<R> maxIngredientsSerializer(ResourceLocation id, BiFunction<List<SizedIngredient>, ItemStack, R> factory, int maxIngredients)
    {
        return ingredientRangeSerializer(id, factory, 1, maxIngredients);
    }

    private final ItemStack resultItem;

    protected LimaSimpleSizedIngredientRecipe(List<SizedIngredient> recipeIngredients, ItemStack resultItem)
    {
        super(recipeIngredients);
        this.resultItem = resultItem;
    }

    protected LimaSimpleSizedIngredientRecipe(SizedIngredient ingredient, ItemStack resultItem)
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