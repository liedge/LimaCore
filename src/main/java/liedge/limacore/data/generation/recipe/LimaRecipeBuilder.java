package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.advancement.LimaAdvancementUtil;
import liedge.limacore.lib.ModResources;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class LimaRecipeBuilder<R extends Recipe<?>, B extends LimaRecipeBuilder<R, B>> implements RecipeBuilder
{
    public static final String DEFAULT_CRITERION_KEY = "has_the_recipe";
    public static final AdvancementRequirements.Strategy HAS_RECIPE_OR_ALL_OF = keys ->
    {
        List<List<String>> requirements = keys.stream().map(s -> List.of(s, DEFAULT_CRITERION_KEY)).toList();
        return new AdvancementRequirements(requirements);
    };

    private final List<ICondition> conditions = new ObjectArrayList<>();
    private final Map<String, Criterion<?>> criteria = new Object2ObjectOpenHashMap<>();
    protected final ModResources resources;
    protected final HolderLookup.Provider registries;

    private String group = LimaCustomRecipe.EMPTY_GROUP;
    private AdvancementRequirements.Strategy strategy = AdvancementRequirements.Strategy.OR;

    protected LimaRecipeBuilder(ModResources resources, HolderLookup.Provider registries)
    {
        this.resources = resources;
        this.registries = registries;
    }

    public B condition(ICondition condition)
    {
        conditions.add(condition);
        return selfUnchecked();
    }

    @Override
    public B unlockedBy(String criterionKey, Criterion<?> criterion)
    {
        criteria.putIfAbsent(criterionKey, criterion);
        return selfUnchecked();
    }

    public B unlockedBy(ItemLike item)
    {
        String name = "has_" + LimaRegistryUtil.getItemName(item.asItem());
        return unlockedBy(name, InventoryChangeTrigger.TriggerInstance.hasItems(item));
    }

    public B unlockedBy(HolderGetter<Item> items, TagKey<Item> tag)
    {
        String name = "has_any_" + tag.location().getPath().replace("/", "_");
        return unlockedBy(name, LimaAdvancementUtil.playerHasTagItems(items, tag));
    }

    public B unlockStrategy(AdvancementRequirements.Strategy strategy)
    {
        this.strategy = strategy;
        return selfUnchecked();
    }

    protected abstract R buildRecipe();

    protected abstract String getDefaultRecipeName();

    @SuppressWarnings("unchecked")
    protected final B selfUnchecked()
    {
        return (B) this;
    }

    @Override
    public B group(@Nullable String group)
    {
        this.group = Objects.requireNonNullElse(group, LimaCustomRecipe.EMPTY_GROUP);
        return selfUnchecked();
    }

    public String getGroup()
    {
        return group;
    }

    protected String makeTypePrefix(Recipe<?> recipe)
    {
        return LimaRegistryUtil.getNonNullRegistryId(recipe.getType(), BuiltInRegistries.RECIPE_TYPE).getPath() + '/';
    }

    protected String makeSerializerPrefix(Recipe<?> recipe)
    {
        return LimaRegistryUtil.getNonNullRegistryId(recipe.getSerializer(), BuiltInRegistries.RECIPE_SERIALIZER).getPath() + '/';
    }

    protected String defaultFolderPrefix(R recipe)
    {
        return makeSerializerPrefix(recipe);
    }

    protected @Nullable AdvancementHolder buildAdvancement(Advancement.Builder builder, ResourceKey<Recipe<?>> key, Map<String, Criterion<?>> criteria)
    {
        if (criteria.isEmpty()) return null;

        builder.addCriterion(DEFAULT_CRITERION_KEY, RecipeUnlockedTrigger.unlocked(key))
                .rewards(AdvancementRewards.Builder.recipe(key))
                .requirements(strategy);
        criteria.forEach(builder::addCriterion);

        return builder.build(key.identifier().withPrefix("recipes/"));
    }

    private void save(RecipeOutput recipeOutput, R recipe, ResourceKey<Recipe<?>> key)
    {
        AdvancementHolder advancement = buildAdvancement(recipeOutput.advancement(), key, criteria);
        recipeOutput.accept(key, recipe, advancement, conditions.toArray(ICondition[]::new));
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId()
    {
        return resources.recipeKey(getDefaultRecipeName());
    }

    @Override
    public final void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> key)
    {
        save(recipeOutput, buildRecipe(), key);
    }

    @Override
    public final void save(RecipeOutput recipeOutput, String name)
    {
        R recipe = buildRecipe();
        ResourceKey<Recipe<?>> key = resources.recipeKey(defaultFolderPrefix(recipe) + name);

        save(recipeOutput, recipe, key);
    }

    @Override
    public final void save(RecipeOutput recipeOutput)
    {
        save(recipeOutput, getDefaultRecipeName());
    }
}