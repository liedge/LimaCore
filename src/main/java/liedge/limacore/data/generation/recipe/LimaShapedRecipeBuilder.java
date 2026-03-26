package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.HolderGetter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class LimaShapedRecipeBuilder extends LimaRecipeBuilder<ShapedRecipe, LimaShapedRecipeBuilder>
{
    private final List<String> rows = new ObjectArrayList<>();
    private final Char2ObjectMap<Ingredient> ingredients = new Char2ObjectOpenHashMap<>();
    private final ItemStackTemplate resultItem;
    private CraftingBookCategory category = CraftingBookCategory.MISC;

    private boolean showNotification = true;

    public LimaShapedRecipeBuilder(ModResources resources, ItemStackTemplate resultItem)
    {
        super(resources);
        this.resultItem = resultItem;
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

    public LimaShapedRecipeBuilder input(char key, HolderGetter<Item> holders, TagKey<Item> itemTag)
    {
        return input(key, Ingredient.of(holders.getOrThrow(itemTag)));
    }

    public LimaShapedRecipeBuilder showsNotification(boolean showNotification)
    {
        this.showNotification = showNotification;
        return this;
    }

    public LimaShapedRecipeBuilder bookCategory(CraftingBookCategory category)
    {
        this.category = category;
        return this;
    }

    @Override
    protected String defaultFolderPrefix(ShapedRecipe recipe)
    {
        return "shaped_recipes/";
    }

    @Override
    protected ShapedRecipe buildRecipe()
    {
        ShapedRecipePattern pattern = ShapedRecipePattern.of(ingredients, rows);

        return new ShapedRecipe(new Recipe.CommonInfo(showNotification), new CraftingRecipe.CraftingBookInfo(category, getGroupOrBlank()), pattern, resultItem);
    }

    @Override
    protected String getDefaultRecipeName()
    {
        return LimaRegistryUtil.getNonNullRegistryId(resultItem.typeHolder()).getPath();
    }
}