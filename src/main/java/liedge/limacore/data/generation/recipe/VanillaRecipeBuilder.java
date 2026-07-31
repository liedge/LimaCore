package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public abstract class VanillaRecipeBuilder<R extends Recipe<?>, C, CH, B extends VanillaRecipeBuilder<R, C, CH, B>> extends LimaRecipeBuilder<R, B>
{
    private final ItemStackTemplate result;
    private final C defaultCategory;
    private final BiFunction<C, String, CH> categoryWrapper;

    private boolean showNotification = true;
    private @Nullable C bookCategory;

    protected VanillaRecipeBuilder(ModResources resources, HolderLookup.Provider registries, ItemStackTemplate result, C defaultCategory, BiFunction<C, String, CH> categoryWrapper)
    {
        super(resources, registries);
        this.result = result;
        this.defaultCategory = defaultCategory;
        this.categoryWrapper = categoryWrapper;
    }

    public B category(C bookCategory)
    {
        this.bookCategory = bookCategory;
        return selfUnchecked();
    }

    public B showNotification(boolean showNotification)
    {
        this.showNotification = showNotification;
        return selfUnchecked();
    }

    protected abstract R buildRecipe(Recipe.CommonInfo commonInfo, CH categoryInfo, ItemStackTemplate result);

    @Override
    protected final R buildRecipe()
    {
        Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(showNotification);
        CH categoryHolder = categoryWrapper.apply(Objects.requireNonNullElse(bookCategory, defaultCategory), getGroup());

        return buildRecipe(commonInfo, categoryHolder, result);
    }

    @Override
    protected final String getDefaultRecipeName()
    {
        return LimaRegistryUtil.getNonNullRegistryId(result.typeHolder()).getPath();
    }

    public abstract static class StandardIngredients<R extends Recipe<?>, C, CH, B extends StandardIngredients<R, C, CH, B>> extends VanillaRecipeBuilder<R, C, CH, B>
    {
        protected final List<Ingredient> ingredients = new ObjectArrayList<>();

        protected StandardIngredients(ModResources resources, HolderLookup.Provider registries, ItemStackTemplate result, C defaultCategory, BiFunction<C, String, CH> categoryWrapper)
        {
            super(resources, registries, result, defaultCategory, categoryWrapper);
        }

        public B input(Ingredient ingredient)
        {
            ingredients.add(ingredient);
            return selfUnchecked();
        }

        public B input(ItemLike item)
        {
            return input(Ingredient.of(item));
        }

        public B input(TagKey<Item> tagKey)
        {
            return input(Ingredient.of(registries.getOrThrow(tagKey)));
        }
    }
}