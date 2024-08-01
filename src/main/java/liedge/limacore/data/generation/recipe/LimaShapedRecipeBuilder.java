package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;
import java.util.Objects;

public class LimaShapedRecipeBuilder extends SingleResultRecipeBuilder<ShapedRecipe, LimaShapedRecipeBuilder>
{
    private final List<String> rows = new ObjectArrayList<>();
    private final Char2ObjectMap<Ingredient> ingredients = new Char2ObjectOpenHashMap<>();
    private boolean showNotification = true;
    private ShapedRecipePattern pattern;

    public LimaShapedRecipeBuilder(ModResources resources, ItemStack result)
    {
        super(RecipeSerializer.SHAPED_RECIPE, resources, result);
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

    public LimaShapedRecipeBuilder showsNotification(boolean showNotification)
    {
        this.showNotification = showNotification;
        return this;
    }

    @Override
    protected void validate(ResourceLocation id)
    {
        try
        {
            this.pattern = ShapedRecipePattern.of(ingredients, rows);
        }
        catch (IllegalArgumentException ex)
        {
            throw new IllegalStateException("Caught error constructing shaped recipe pattern for '" + id + "'", ex);
        }

        Objects.requireNonNull(pattern, "Shaped recipe pattern not built for " + id);
    }

    @Override
    protected ShapedRecipe buildRecipe()
    {
        return new ShapedRecipe("", CraftingBookCategory.MISC, pattern, result, showNotification);
    }
}