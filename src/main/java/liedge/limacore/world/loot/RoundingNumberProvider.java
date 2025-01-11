package liedge.limacore.world.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.LimaCoreLootRegistries;
import liedge.limacore.util.LimaMathUtil;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record RoundingNumberProvider(NumberProvider child, LimaMathUtil.RoundingStrategy strategy) implements NumberProvider
{
    public static final MapCodec<RoundingNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("child").forGetter(RoundingNumberProvider::child),
            LimaMathUtil.RoundingStrategy.NATURAL_DEFAULT_MAP_CODEC.forGetter(RoundingNumberProvider::strategy))
            .apply(instance, RoundingNumberProvider::new));

    public static RoundingNumberProvider roundValue(LimaMathUtil.RoundingStrategy strategy, NumberProvider child)
    {
        return new RoundingNumberProvider(child, strategy);
    }

    public static RoundingNumberProvider roundRandomly(NumberProvider child)
    {
        return new RoundingNumberProvider(child, LimaMathUtil.RoundingStrategy.RANDOM);
    }

    @Override
    public float getFloat(LootContext lootContext)
    {
        return child.getFloat(lootContext);
    }

    @Override
    public int getInt(LootContext lootContext)
    {
        return LimaMathUtil.round(getFloat(lootContext), strategy);
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.ROUNDING_NUMBER_PROVIDER.get();
    }
}