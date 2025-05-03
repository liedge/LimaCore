package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.advancement.LimaAdvancementUtil;
import liedge.limacore.lib.ModResources;
import liedge.limacore.recipe.LimaSizedIngredientRecipe;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

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
    protected final ModResources modResources;

    private String group;
    private AdvancementRequirements.Strategy strategy = AdvancementRequirements.Strategy.OR;

    protected LimaRecipeBuilder(ModResources modResources)
    {
        this.modResources = modResources;
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

    public B unlockedBy(TagKey<Item> tag)
    {
        String name = "has_any_" + tag.location().getPath().replace("/", "_");
        return unlockedBy(name, LimaAdvancementUtil.playerHasItems(tag));
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

    @Deprecated
    @Override
    public final Item getResult()
    {
        throw new UnsupportedOperationException("Use getDefaultRecipeName instead");
    }

    @Override
    public B group(@Nullable String group)
    {
        this.group = group;
        return selfUnchecked();
    }

    public @Nullable String getGroup()
    {
        return group;
    }

    public String getGroupOrBlank()
    {
        return Objects.requireNonNullElse(getGroup(), LimaSizedIngredientRecipe.EMPTY_GROUP);
    }

    protected String makeTypePrefix(Recipe<?> recipe)
    {
        return LimaRegistryUtil.getNonNullRegistryId(recipe.getType(), BuiltInRegistries.RECIPE_TYPE).getPath() + '/';
    }

    protected String makeSerializerPrefix(Recipe<?> recipe)
    {
        return LimaRegistryUtil.getNonNullRegistryId(recipe.getSerializer(), BuiltInRegistries.RECIPE_SERIALIZER).getPath() + '/';
    }

    protected String defaultFolderPrefix(R recipe, ResourceLocation recipeId)
    {
        return makeSerializerPrefix(recipe);
    }

    protected String getDefaultStackName(ItemStack stack)
    {
        return LimaRegistryUtil.getItemName(stack.getItem());
    }

    protected @Nullable AdvancementHolder buildAdvancement(Advancement.Builder builder, ResourceLocation id, Map<String, Criterion<?>> criteria)
    {
        if (criteria.isEmpty()) return null;

        builder.addCriterion(DEFAULT_CRITERION_KEY, RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(strategy);
        criteria.forEach(builder::addCriterion);

        return builder.build(id.withPrefix("recipes/"));
    }

    private void save(RecipeOutput recipeOutput, ResourceLocation id, boolean appendFolderPrefix)
    {
        // Build recipe & append prefix to id if necessary
        R recipe = buildRecipe();
        if (appendFolderPrefix) id = id.withPrefix(defaultFolderPrefix(recipe, id));

        // Build advancement
        AdvancementHolder advancement = buildAdvancement(recipeOutput.advancement(), id, this.criteria);

        // Save recipe
        recipeOutput.accept(id, recipe, advancement, conditions.toArray(ICondition[]::new));
    }

    @Override
    public final void save(RecipeOutput recipeOutput, ResourceLocation id)
    {
        save(recipeOutput, id, false);
    }

    @Override
    public final void save(RecipeOutput recipeOutput, String name)
    {
        save(recipeOutput, modResources.location(name), true);
    }

    @Override
    public final void save(RecipeOutput recipeOutput)
    {
        save(recipeOutput, getDefaultRecipeName());
    }
}