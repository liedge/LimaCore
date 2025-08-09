package liedge.limacore.world.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreLootRegistries;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.phys.Vec3;

public record DistanceNumberProvider(LootPositionSource origin, LootPositionSource end, NumberProvider fallback) implements NumberProvider
{
    public static final MapCodec<DistanceNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootPositionSource.CODEC.fieldOf("origin").forGetter(DistanceNumberProvider::origin),
            LootPositionSource.CODEC.fieldOf("end").forGetter(DistanceNumberProvider::end),
            NumberProviders.CODEC.optionalFieldOf("fallback", ConstantValue.exactly(-1)).forGetter(DistanceNumberProvider::fallback))
            .apply(instance, DistanceNumberProvider::new));

    @Override
    public float getFloat(LootContext lootContext)
    {
        Vec3 a = origin.get(lootContext);
        Vec3 b = end.get(lootContext);

        return a != null && b != null ? (float) a.distanceTo(b) : fallback.getFloat(lootContext);
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LimaCoreLootRegistries.DISTANCE_NUMBER_PROVIDER.get();
    }
}