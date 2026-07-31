package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class LimaShapedRecipeBuilder extends VanillaRecipeBuilder<ShapedRecipe, CraftingBookCategory, CraftingRecipe.CraftingBookInfo, LimaShapedRecipeBuilder>
{
    private final List<String> rows = new ObjectArrayList<>();
    private final Char2ObjectMap<Ingredient> ingredients = new Char2ObjectOpenHashMap<>();

    public LimaShapedRecipeBuilder(ModResources resources, HolderLookup.Provider registries, ItemStackTemplate result)
    {
        super(resources, registries, result, CraftingBookCategory.MISC, CraftingRecipe.CraftingBookInfo::new);
    }

    public LimaShapedRecipeBuilder patterns(String... patterns)
    {
        for (String p : patterns)
        {
            if (!rows.isEmpty() && p.length() != rows.getFirst().length())
            {
                throw new IllegalArgumentException("Pattern must be the same width on every line!");
            }
            else
            {
                rows.add(p);
            }
        }
        return this;
    }

    public LimaShapedRecipeBuilder input(char key, Ingredient ingredient)
    {
        ingredients.put(key, ingredient);
        return this;
    }

    public LimaShapedRecipeBuilder input(char key, ItemLike item)
    {
        return input(key, Ingredient.of(item));
    }

    public LimaShapedRecipeBuilder input(char key, TagKey<Item> tagKey)
    {
        return input(key, Ingredient.of(registries.getOrThrow(tagKey)));
    }

    @Override
    protected String defaultFolderPrefix(ShapedRecipe recipe)
    {
        return "shaped_recipes/";
    }

    @Override
    protected ShapedRecipe buildRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo categoryInfo, ItemStackTemplate result)
    {
        ShapedRecipePattern pattern = ShapedRecipePattern.of(ingredients, rows);
        return new ShapedRecipe(commonInfo, categoryInfo, pattern, result);
    }
}