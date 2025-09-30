package liedge.limacore.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class ItemBrokenTrigger extends SimpleCriterionTrigger<ItemBrokenTrigger.TriggerInstance>
{
    public ItemBrokenTrigger() {}

    @Override
    public Codec<TriggerInstance> codec()
    {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack stack, EquipmentSlot slot)
    {
        trigger(player, o -> o.matches(stack, slot));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, EquipmentSlotGroup slots) implements SimpleCriterionTrigger.SimpleInstance
    {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item),
                EquipmentSlotGroup.CODEC.fieldOf("slots").forGetter(TriggerInstance::slots))
                .apply(instance, TriggerInstance::new));

        private boolean matches(ItemStack stack, EquipmentSlot slot)
        {
            return item.map(o -> o.test(stack)).orElse(true) && slots.test(slot);
        }
    }
}