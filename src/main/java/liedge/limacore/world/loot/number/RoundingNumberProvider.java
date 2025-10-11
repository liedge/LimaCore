package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.LimaRoundingMode;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import liedge.limacore.lib.math.LimaCoreMath;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Set;

public record RoundingNumberProvider(NumberProvider child, LimaRoundingMode mode) implements NumberProvider
{
    public static final MapCodec<RoundingNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("child").forGetter(RoundingNumberProvider::child),
            LimaRoundingMode.CODEC.optionalFieldOf("mode", LimaRoundingMode.NATURAL).forGetter(RoundingNumberProvider::mode))
            .apply(instance, RoundingNumberProvider::new));

    public static NumberProvider of(NumberProvider child, LimaRoundingMode mode)
    {
        return new RoundingNumberProvider(child, mode);
    }

    @Override
    public float getFloat(LootContext context)
    {
        return getInt(context);
    }

    @Override
    public int getInt(LootContext context)
    {
        return LimaCoreMath.round(child.getFloat(context), mode);
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.ROUNDING_NUMBER_PROVIDER.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return child.getReferencedContextParams();
    }
}