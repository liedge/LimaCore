package liedge.limacore.data.generation.loot;

import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;

import java.util.stream.Stream;

public abstract class LimaEntityLootSubProvider extends EntityLootSubProvider implements LimaLootSubProviderExtensions
{
    private final String modid;

    protected LimaEntityLootSubProvider(HolderLookup.Provider registries, String modid)
    {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
        this.modid = modid;
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes()
    {
        return LimaRegistryUtil.allNamespaceRegistryValues(modid, BuiltInRegistries.ENTITY_TYPE);
    }
}