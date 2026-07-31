package liedge.limacore.data.generation;

import liedge.limacore.data.generation.recipe.LimaCookingRecipeBuilder;
import liedge.limacore.data.generation.recipe.LimaShapedRecipeBuilder;
import liedge.limacore.data.generation.recipe.LimaShapelessRecipeBuilder;
import liedge.limacore.data.generation.recipe.LimaStonecuttingRecipeBuilder;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.ItemLike;

public abstract class LimaRecipeProvider extends RecipeProvider
{
    protected final ModResources resources;

    public LimaRecipeProvider(HolderLookup.Provider registries, RecipeOutput output, ModResources resources)
    {
        super(registries, output);
        this.resources = resources;
    }

    //#region Standard/Vanilla recipe builders
    protected LimaShapedRecipeBuilder shaped(ItemStackTemplate result)
    {
        return new LimaShapedRecipeBuilder(resources, registries, result);
    }

    protected LimaShapedRecipeBuilder shaped(ItemLike item, int count)
    {
        return shaped(stackTemplate(item, count));
    }

    protected LimaShapedRecipeBuilder shaped(ItemLike item)
    {
        return shaped(stackTemplate(item));
    }

    protected LimaShapelessRecipeBuilder shapeless(ItemStackTemplate result)
    {
        return new LimaShapelessRecipeBuilder(resources, registries, result);
    }

    protected LimaShapelessRecipeBuilder shapeless(ItemLike item, int count)
    {
        return shapeless(stackTemplate(item, count));
    }

    protected LimaShapelessRecipeBuilder shapeless(ItemLike item)
    {
        return shapeless(stackTemplate(item));
    }

    protected LimaStonecuttingRecipeBuilder stonecutting(ItemStackTemplate result)
    {
        return new LimaStonecuttingRecipeBuilder(resources, registries, result);
    }

    protected LimaStonecuttingRecipeBuilder stonecutting(ItemLike item, int count)
    {
        return stonecutting(stackTemplate(item, count));
    }

    protected LimaStonecuttingRecipeBuilder stonecutting(ItemLike item)
    {
        return stonecutting(stackTemplate(item));
    }

    protected LimaCookingRecipeBuilder smelting(ItemStackTemplate result)
    {
        return new LimaCookingRecipeBuilder(resources, registries, result, 200, SmeltingRecipe::new);
    }

    protected LimaCookingRecipeBuilder smelting(ItemLike item, int count)
    {
        return smelting(stackTemplate(item, count));
    }

    protected LimaCookingRecipeBuilder smelting(ItemLike item)
    {
        return smelting(stackTemplate(item));
    }

    protected LimaCookingRecipeBuilder blasting(ItemStackTemplate result)
    {
        return new LimaCookingRecipeBuilder(resources, registries, result, 100, BlastingRecipe::new);
    }

    protected LimaCookingRecipeBuilder blasting(ItemLike item, int count)
    {
        return blasting(stackTemplate(item, count));
    }

    protected LimaCookingRecipeBuilder blasting(ItemLike item)
    {
        return blasting(stackTemplate(item));
    }

    protected LimaCookingRecipeBuilder smoking(ItemStackTemplate result)
    {
        return new LimaCookingRecipeBuilder(resources, registries, result, 100, SmokingRecipe::new);
    }

    protected LimaCookingRecipeBuilder smoking(ItemLike item, int count)
    {
        return smoking(stackTemplate(item, count));
    }

    protected LimaCookingRecipeBuilder smoking(ItemLike item)
    {
        return smoking(stackTemplate(item));
    }
    //#endregion

    //#region Commonly used recipe formats
    protected LimaShapedRecipeBuilder shaped3x3(ItemLike input, ItemStackTemplate result)
    {
        return shaped(result).input('#', input).patterns("###", "###", "###");
    }

    protected LimaShapedRecipeBuilder shaped3x3(ItemLike input, ItemLike resultItem, int resultCount)
    {
        return shaped3x3(input, stackTemplate(resultItem, resultCount));
    }

    protected LimaShapedRecipeBuilder shaped2x2(ItemLike input, ItemStackTemplate result)
    {
        return shaped(result).input('#', input).patterns("##", "##");
    }

    protected LimaShapedRecipeBuilder shaped2x2(ItemLike input, ItemLike resultItem, int resultCount)
    {
        return shaped2x2(input, stackTemplate(resultItem, resultCount));
    }

    protected void nuggetIngotBlockRecipes(String materialName, ItemLike nugget, ItemLike ingot, ItemLike block)
    {
        shaped3x3(nugget, stackTemplate(ingot)).save(output, materialName + "_nuggets_to_ingot");
        shapeless(stackTemplate(nugget, 9)).input(Ingredient.of(ingot)).save(output, materialName + "_ingot_to_nuggets");
        shaped3x3(ingot, stackTemplate(block)).save(output, materialName + "_ingots_to_block");
        shapeless(stackTemplate(ingot, 9)).input(Ingredient.of(block)).save(output, materialName + "_block_to_ingots");
    }

    protected void nineStorageRecipes(ItemLike unpackedItem, ItemLike packedItem)
    {
        String recipeName = getItemName(unpackedItem);
        shaped3x3(unpackedItem, stackTemplate(packedItem)).save(output, "pack_9_" + recipeName);
        shapeless(stackTemplate(unpackedItem, 9)).input(packedItem).save(output, "unpack_9_" + recipeName);
    }

    protected void fourStorageRecipes(ItemLike unpackedItem, ItemLike packedItem)
    {
        String recipeName = getItemName(unpackedItem);
        shaped2x2(unpackedItem, stackTemplate(packedItem)).save(output, "pack_4_" + recipeName);
        shapeless(stackTemplate(unpackedItem, 4)).input(packedItem).save(output, "unpack_4_" + recipeName);
    }

    protected void oreSmeltBlast(String recipeName, Ingredient input, ItemStackTemplate result)
    {
        smelting(result).input(input).xp(0.7f).save(output, recipeName);
        blasting(result).input(input).xp(0.7f).save(output, recipeName);
    }

    protected void oreSmeltBlast(String recipeName, Ingredient input, ItemLike resultItem, int resultCount)
    {
        oreSmeltBlast(recipeName, input, stackTemplate(resultItem, resultCount));
    }

    protected void oreSmeltBlast(String recipeName, Ingredient input, ItemLike resultItem)
    {
        oreSmeltBlast(recipeName, input, stackTemplate(resultItem));
    }

    protected void oreSmeltBlast(String recipeName, ItemLike input, ItemStackTemplate result)
    {
        smelting(result).input(input).xp(0.7f).save(output, recipeName);
        blasting(result).input(input).xp(0.7f).save(output, recipeName);
    }

    protected void oreSmeltBlast(String recipeName, ItemLike input, ItemLike resultItem, int resultCount)
    {
        oreSmeltBlast(recipeName, input, stackTemplate(resultItem, resultCount));
    }

    protected void oreSmeltBlast(String recipeName, ItemLike input, ItemLike resultItem)
    {
        oreSmeltBlast(recipeName, input, stackTemplate(resultItem));
    }
    //#endregion

    //#region Ingredient & Item Stack factories
    protected ItemStackTemplate stackTemplate(ItemLike item, int count)
    {
        return new ItemStackTemplate(item.asItem(), count);
    }

    protected ItemStackTemplate stackTemplate(ItemLike item)
    {
        return stackTemplate(item, 1);
    }
    //#endregion
}