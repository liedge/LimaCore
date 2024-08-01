package liedge.limacore.data.generation;

import liedge.limacore.data.generation.recipe.LimaCookingRecipeBuilder;
import liedge.limacore.data.generation.recipe.LimaShapedRecipeBuilder;
import liedge.limacore.data.generation.recipe.LimaShapelessRecipeBuilder;
import liedge.limacore.lib.ModResources;
import liedge.limacore.recipe.LimaSimpleCountIngredient;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public abstract class LimaRecipeProvider extends RecipeProvider
{
    protected final ModResources modResources;

    public LimaRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ModResources modResources)
    {
        super(output, registries);
        this.modResources = modResources;
    }

    //#region Standard/Vanilla recipe builders
    protected LimaShapedRecipeBuilder shaped(ItemStack result)
    {
        return new LimaShapedRecipeBuilder(modResources, result);
    }

    protected LimaShapelessRecipeBuilder shapeless(ItemStack result)
    {
        return new LimaShapelessRecipeBuilder(modResources, result);
    }

    protected LimaCookingRecipeBuilder smelting(ItemStack result)
    {
        return new LimaCookingRecipeBuilder(RecipeSerializer.SMELTING_RECIPE, modResources, result, 200, SmeltingRecipe::new);
    }

    protected LimaCookingRecipeBuilder blasting(ItemStack result)
    {
        return new LimaCookingRecipeBuilder(RecipeSerializer.BLASTING_RECIPE, modResources, result, 100, BlastingRecipe::new);
    }

    protected LimaCookingRecipeBuilder smoking(ItemStack result)
    {
        return new LimaCookingRecipeBuilder(RecipeSerializer.SMOKING_RECIPE, modResources, result, 100, SmokingRecipe::new);
    }
    //#endregion

    //#region Commonly used recipe formats
    protected LimaShapedRecipeBuilder shaped3x3(ItemLike input, ItemStack result)
    {
        return shaped(result).input('#', in(input)).patterns("###", "###", "###");
    }

    protected LimaShapedRecipeBuilder shaped2x2(ItemLike input, ItemStack result)
    {
        return shaped(result).input('#', in(input)).patterns("##", "##");
    }

    protected void nuggetIngotBlockRecipes(RecipeOutput output, String materialName, ItemLike nugget, ItemLike ingot, ItemLike block)
    {
        shaped3x3(nugget, new ItemStack(ingot)).save(output, materialName + "_nuggets_to_ingot");
        shapeless(new ItemStack(nugget, 9)).input(Ingredient.of(ingot)).save(output, materialName + "_ingot_to_nuggets");
        shaped3x3(ingot, new ItemStack(block)).save(output, materialName + "_ingots_to_block");
        shapeless(new ItemStack(ingot, 9)).input(Ingredient.of(block)).save(output, materialName + "_block_to_ingots");
    }

    protected void nineStorageRecipes(RecipeOutput output, ItemLike unpackedItem, ItemLike packedItem)
    {
        String recipeName = getItemName(unpackedItem);
        shaped3x3(unpackedItem, out(packedItem)).save(output, "pack_9_" + recipeName);
        shapeless(out(unpackedItem, 9)).input(in(packedItem)).save(output, "unpack_9_" + recipeName);
    }

    protected void fourStorageRecipes(RecipeOutput output, ItemLike unpackedItem, ItemLike packedItem)
    {
        String recipeName = getItemName(unpackedItem);
        shaped2x2(unpackedItem, out(packedItem)).save(output, "pack_4_" + recipeName);
        shapeless(out(unpackedItem, 4)).input(in(packedItem)).save(output, "unpack_4_" + recipeName);
    }

    protected void oreSmeltBlast(RecipeOutput recipeOutput, String recipeName, Ingredient input, ItemStack result)
    {
        smelting(result).input(input).xp(0.7f).save(recipeOutput, recipeName);
        blasting(result).input(input).xp(0.7f).save(recipeOutput, recipeName);
    }
    //#endregion

    //#region Ingredient & Item Stack factories
    protected ItemStack out(ItemLike item, int count)
    {
        return new ItemStack(item, count);
    }

    protected ItemStack out(ItemLike item)
    {
        return new ItemStack(item);
    }

    protected Ingredient in(ItemLike item)
    {
        return Ingredient.of(item);
    }

    protected Ingredient in(TagKey<Item> tag)
    {
        return Ingredient.of(tag);
    }

    protected Ingredient in(ItemLike item, int count)
    {
        return LimaSimpleCountIngredient.itemValue(item, count);
    }

    protected Ingredient in(TagKey<Item> tag, int count)
    {
        return LimaSimpleCountIngredient.tagValue(tag, count);
    }
    //#endregion
}