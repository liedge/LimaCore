package liedge.limacore.data.generation.recipe;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.advancement.LimaAdvancementUtil;
import liedge.limacore.lib.ModResources;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class LimaRecipeBuilder<R extends Recipe<?>, B extends LimaRecipeBuilder<R, B>> implements RecipeBuilder
{
    private final List<ICondition> conditions = new ObjectArrayList<>();
    private final Map<String, Criterion<?>> criteria = new Object2ObjectOpenHashMap<>();
    private final RecipeSerializer<? extends R> serializer;
    protected final ModResources modResources;

    private String group;

    protected LimaRecipeBuilder(RecipeSerializer<? extends R> serializer, ModResources modResources)
    {
        this.serializer = serializer;
        this.modResources = modResources;
    }

    protected LimaRecipeBuilder(Supplier<RecipeSerializer<? extends R>> supplier, ModResources modResources)
    {
        this(supplier.get(), modResources);
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

    protected abstract void validate(ResourceLocation id);

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
        return Objects.requireNonNullElse(getGroup(), "");
    }

    @Override
    public final void save(RecipeOutput recipeOutput, ResourceLocation id)
    {
        validate(id);

        // Build advancement (if criteria present)
        AdvancementHolder holder;
        if (!criteria.isEmpty())
        {
            Advancement.Builder builder = recipeOutput.advancement()
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
                    .requirements(AdvancementRequirements.Strategy.OR);
            criteria.forEach(builder::addCriterion);
            holder = builder.build(id.withPrefix("recipes/"));
        }
        else
        {
            holder = null;
        }

        // Build and save recipe
        R recipe = buildRecipe();
        if (conditions.isEmpty())
        {
            recipeOutput.accept(id, recipe, holder);
        }
        else
        {
            ICondition[] recipeConditions = conditions.toArray(ICondition[]::new);
            recipeOutput.accept(id, recipe, holder, recipeConditions);
        }
    }

    @Override
    public final void save(RecipeOutput recipeOutput, String name)
    {
        ResourceLocation id = modResources.formatLocation(LimaRegistryUtil.getNonNullRegistryKey(serializer, BuiltInRegistries.RECIPE_SERIALIZER).getPath(), name);
        save(recipeOutput, id);
    }

    @Override
    public final void save(RecipeOutput recipeOutput)
    {
        save(recipeOutput, getDefaultRecipeName());
    }
}