package liedge.limacore.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreTriggerTypes;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class RecipeTypeTrigger extends SimpleCriterionTrigger<RecipeTypeTrigger.TriggerInstance>
{
    public RecipeTypeTrigger() {}

    @Override
    public Codec<TriggerInstance> codec()
    {
        return TriggerInstance.CODEC;
    }

    public void triggerCriterion(ServerPlayer player, RecipeType<?> type, ItemStack stack)
    {
        trigger(player, o -> o.matches(type, stack));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, RecipeType<?> recipeType, Optional<ItemPredicate> item) implements SimpleInstance
    {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                BuiltInRegistries.RECIPE_TYPE.byNameCodec().fieldOf("recipe_type").forGetter(TriggerInstance::recipeType),
                ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item))
                .apply(instance, TriggerInstance::new));

        public static Criterion<TriggerInstance> customItemCrafted(@Nullable ContextAwarePredicate player, RecipeType<?> recipeType, @Nullable ItemPredicate predicate)
        {
            TriggerInstance instance = new TriggerInstance(Optional.ofNullable(player), recipeType, Optional.ofNullable(predicate));
            return LimaCoreTriggerTypes.CUSTOM_RECIPE_TYPE_USED.get().createCriterion(instance);
        }

        public static Criterion<TriggerInstance> customItemCrafted(@Nullable ContextAwarePredicate player, RecipeType<?> recipeType, ItemPredicate.Builder builder)
        {
            return customItemCrafted(player, recipeType, builder.build());
        }

        public static Criterion<TriggerInstance> customItemCrafted(RecipeType<?> recipeType, ItemLike... items)
        {
            return customItemCrafted(null, recipeType, LimaAdvancementUtil.matchingItems(items));
        }

        private boolean matches(RecipeType<?> type, ItemStack stack)
        {
            return recipeType.equals(type) && (item.isEmpty() || item.get().test(stack));
        }
    }
}