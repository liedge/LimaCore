package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Set;

/**
 * This number provider calculates a {@link LevelBasedValue} using the nested provider's {@code int} value as
 * provided by {@link NumberProvider#getInt(LootContext)}.
 */
public record LevelBasedNumberProvider(NumberProvider input, LevelBasedValue formula) implements NumberProvider
{
    public static final MapCodec<LevelBasedNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("input").forGetter(LevelBasedNumberProvider::input),
            LevelBasedValue.CODEC.fieldOf("formula").forGetter(LevelBasedNumberProvider::formula))
            .apply(instance, LevelBasedNumberProvider::new));

    public static LevelBasedNumberProvider of(NumberProvider input, LevelBasedValue formula)
    {
        return new LevelBasedNumberProvider(input, formula);
    }

    @Override
    public float getFloat(LootContext context)
    {
        int level = Math.max(1, input.getInt(context)); // At least 1 LBV type expects a param >= 1.
        return formula.calculate(level);
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.LEVEL_BASED_NUMBER_PROVIDER.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return input.getReferencedContextParams();
    }
}