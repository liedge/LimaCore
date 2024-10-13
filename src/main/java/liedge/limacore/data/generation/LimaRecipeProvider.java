package liedge.limacore.data.generation;

import liedge.limacore.data.generation.recipe.LimaCookingRecipeBuilder;
import liedge.limacore.data.generation.recipe.LimaShapedRecipeBuilder;
import liedge.limacore.data.generation.recipe.LimaShapelessRecipeBuilder;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
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

    @Override
    protected abstract void buildRecipes(RecipeOutput output, HolderLookup.Provider registries);

    @Deprecated
    @Override
    protected final void buildRecipes(RecipeOutput output) {}

    //#region Standard/Vanilla recipe builders
    protected LimaShapedRecipeBuilder shaped(ItemStack result)
    {
        return new LimaShapedRecipeBuilder(modResources, result);
    }

    protected LimaShapedRecipeBuilder shaped(ItemLike item, int count)
    {
        return shaped(stackOf(item, count));
    }

    protected LimaShapedRecipeBuilder shaped(ItemLike item)
    {
        return shaped(stackOf(item));
    }

    protected LimaShapelessRecipeBuilder shapeless(ItemStack result)
    {
        return new LimaShapelessRecipeBuilder(modResources, result);
    }

    protected LimaShapelessRecipeBuilder shapeless(ItemLike item, int count)
    {
        return shapeless(stackOf(item, count));
    }

    protected LimaShapelessRecipeBuilder shapeless(ItemLike item)
    {
        return shapeless(stackOf(item));
    }

    protected LimaCookingRecipeBuilder smelting(ItemStack result)
    {
        return new LimaCookingRecipeBuilder(modResources, result, 200, SmeltingRecipe::new);
    }

    protected LimaCookingRecipeBuilder smelting(ItemLike item)
    {
        return smelting(stackOf(item));
    }

    protected LimaCookingRecipeBuilder blasting(ItemStack result)
    {
        return new LimaCookingRecipeBuilder(modResources, result, 100, BlastingRecipe::new);
    }

    protected LimaCookingRecipeBuilder blasting(ItemLike item)
    {
        return blasting(stackOf(item));
    }

    protected LimaCookingRecipeBuilder smoking(ItemStack result)
    {
        return new LimaCookingRecipeBuilder(modResources, result, 100, SmokingRecipe::new);
    }

    protected LimaCookingRecipeBuilder smoking(ItemLike item)
    {
        return smoking(stackOf(item));
    }
    //#endregion

    //#region Commonly used recipe formats
    protected LimaShapedRecipeBuilder shaped3x3(ItemLike input, ItemStack result)
    {
        return shaped(result).input('#', input).patterns("###", "###", "###");
    }

    protected LimaShapedRecipeBuilder shaped2x2(ItemLike input, ItemStack result)
    {
        return shaped(result).input('#', input).patterns("##", "##");
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
        shaped3x3(unpackedItem, stackOf(packedItem)).save(output, "pack_9_" + recipeName);
        shapeless(stackOf(unpackedItem, 9)).input(packedItem).save(output, "unpack_9_" + recipeName);
    }

    protected void fourStorageRecipes(RecipeOutput output, ItemLike unpackedItem, ItemLike packedItem)
    {
        String recipeName = getItemName(unpackedItem);
        shaped2x2(unpackedItem, stackOf(packedItem)).save(output, "pack_4_" + recipeName);
        shapeless(stackOf(unpackedItem, 4)).input(packedItem).save(output, "unpack_4_" + recipeName);
    }

    protected void oreSmeltBlast(RecipeOutput recipeOutput, String recipeName, Ingredient input, ItemStack result)
    {
        smelting(result).input(input).xp(0.7f).save(recipeOutput, recipeName);
        blasting(result).input(input).xp(0.7f).save(recipeOutput, recipeName);
    }

    protected void oreSmeltBlast(RecipeOutput recipeOutput, String recipeName, ItemLike input, ItemStack result)
    {
        smelting(result).input(input).xp(0.7f).save(recipeOutput, recipeName);
        blasting(result).input(input).xp(0.7f).save(recipeOutput, recipeName);
    }
    //#endregion

    //#region Ingredient & Item Stack factories
    protected ItemStack stackOf(ItemLike item, int count)
    {
        return new ItemStack(item, count);
    }

    protected ItemStack stackOf(ItemLike item)
    {
        return new ItemStack(item);
    }
    //#endregion
}