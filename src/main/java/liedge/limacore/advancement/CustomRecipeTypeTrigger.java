package liedge.limacore.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.LimaCoreTriggerTypes;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

public class CustomRecipeTypeTrigger extends SimpleCriterionTrigger<CustomRecipeTypeTrigger.TriggerInstance>
{
    private static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> LimaAdvancementUtil.playerCodecStart(instance)
            .and(BuiltInRegistries.RECIPE_TYPE.byNameCodec().fieldOf("recipe_type").forGetter(TriggerInstance::recipeType))
            .and(ItemPredicate.CODEC.optionalFieldOf("item_crafted").forGetter(TriggerInstance::itemCrafted)).apply(instance, TriggerInstance::new));

    public static Criterion<TriggerInstance> itemCrafted(RecipeType<?> recipeType, ItemLike item)
    {
        return LimaCoreTriggerTypes.CUSTOM_RECIPE_TYPE_USED.get().createCriterion(new TriggerInstance(Optional.empty(), recipeType, Optional.of(ItemPredicate.Builder.item().of(item).build())));
    }

    public static Criterion<TriggerInstance> anyItemCrafted(RecipeType<?> recipeType)
    {
        return LimaCoreTriggerTypes.CUSTOM_RECIPE_TYPE_USED.get().createCriterion(new TriggerInstance(Optional.empty(), recipeType, Optional.empty()));
    }

    public CustomRecipeTypeTrigger() { }

    public void trigger(ServerPlayer serverPlayer, RecipeType<?> recipeType, ItemStack stack)
    {
        trigger(serverPlayer, instance -> instance.matches(recipeType, stack));
    }

    @Override
    public Codec<TriggerInstance> codec()
    {
        return CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, RecipeType<?> recipeType, Optional<ItemPredicate> itemCrafted) implements SimpleInstance
    {
        private boolean matches(RecipeType<?> recipeType, ItemStack stack)
        {
            return this.recipeType.equals(recipeType) && itemCrafted.map(o -> o.test(stack)).orElse(true);
        }
    }
}