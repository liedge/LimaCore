package liedge.limacore.lib;

import liedge.limacore.data.TransientDataComponentType;
import liedge.limacore.registry.LimaDeferredBlocksWithItems;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public record ModResources(String modid)
{
    public static final ModResources COMMON_NAMESPACE = new ModResources("c");
    public static final ModResources MC = new ModResources("minecraft");

    //#region Static helper methods
    private static String joinComponents(char delimiter, String... components)
    {
        return Arrays.stream(components).filter(StringUtils::isNotBlank).reduce((s1, s2) -> s1 + delimiter + s2).orElseThrow(() -> new IllegalArgumentException("Path must have at least 1 non-blank component."));
    }

    public static String idLangKey(ResourceLocation id)
    {
        return idLangKey(id, "{n}.{p}");
    }

    public static String idLangKey(ResourceLocation id, String keyTemplate)
    {
        return keyTemplate.replace("{n}", id.getNamespace()).replace("{p}", id.getPath()).replace('/', '.');
    }

    public static String idLangKey(ResourceLocation id, String... keyComponents)
    {
        return idLangKey(id, joinComponents('.', keyComponents));
    }

    public static String prefixedIdLangKey(String prefix, ResourceLocation id)
    {
        return idLangKey(id, prefix, "{n}.{p}");
    }

    public static String registryPrefixedIdLangKey(ResourceKey<?> key)
    {
        return prefixedIdLangKey(key.registry().getPath(), key.location());
    }

    public static String prefixedVariantIdLangKey(String prefix, String suffix, ResourceLocation id)
    {
        return idLangKey(id, prefix, "{n}.{p}", suffix);
    }

    public static String registryPrefixVariantIdLangKey(ResourceKey<?> key, String suffix)
    {
        return prefixedVariantIdLangKey(key.registry().getPath(), suffix, key.location());
    }
    //#endregion

    //#region Resource keys
    public <T> ResourceKey<T> resourceKey(ResourceKey<? extends Registry<T>> registryKey, String name)
    {
        return ResourceKey.create(registryKey, location(name));
    }

    public <T> ResourceKey<Registry<T>> registryResourceKey(String name)
    {
        return ResourceKey.createRegistryKey(location(name));
    }
    //#endregion

    //#region Deferred registers
    public <T> DeferredRegister<T> deferredRegister(Registry<T> registry)
    {
        return DeferredRegister.create(registry, modid);
    }

    public <T> DeferredRegister<T> deferredRegister(ResourceKey<? extends Registry<T>> registryKey)
    {
        return DeferredRegister.create(registryKey, modid);
    }

    public <T> DeferredRegister <T> deferredRegister(String registryName)
    {
        return DeferredRegister.create(registryResourceKey(registryName), modid);
    }

    public DeferredRegister.Items deferredItems()
    {
        return DeferredRegister.createItems(modid);
    }

    public DeferredRegister.Blocks deferredBlocks()
    {
        return DeferredRegister.createBlocks(modid);
    }

    public LimaDeferredBlocksWithItems deferredBlocksWithItems()
    {
        return LimaDeferredBlocksWithItems.create(modid);
    }

    public DeferredRegister.DataComponents deferredDataComponents(ResourceKey<Registry<DataComponentType<?>>> componentRegistryKey)
    {
        return DeferredRegister.DataComponents.createDataComponents(componentRegistryKey, modid);
    }

    public DeferredRegister.DataComponents deferredDataComponents()
    {
        return deferredDataComponents(Registries.DATA_COMPONENT_TYPE);
    }

    public <T> RegistryBuilder<T> registryBuilder(ResourceKey<? extends Registry<T>> registryKey)
    {
        return new RegistryBuilder<>(registryKey);
    }

    public <T> RegistryBuilder<T> registryBuilder(String registryName)
    {
        return registryBuilder(registryResourceKey(registryName));
    }
    //#endregion

    //#region Tags
    public <T> TagKey<T> tagKey(ResourceKey<? extends Registry<T>> registryKey, String name)
    {
        return TagKey.create(registryKey, location(name));
    }

    public TagKey<Block> blockTag(String name)
    {
        return BlockTags.create(location(name));
    }

    public TagKey<Item> itemTag(String name)
    {
        return ItemTags.create(location(name));
    }

    public TagKey<BlockEntityType<?>> blockEntityTag(String name)
    {
        return tagKey(Registries.BLOCK_ENTITY_TYPE, name);
    }

    public TagKey<EntityType<?>> entityTypeTag(String name)
    {
        return tagKey(Registries.ENTITY_TYPE, name);
    }

    public TagKey<DamageType> damageTypeTag(String name)
    {
        return tagKey(Registries.DAMAGE_TYPE, name);
    }
    //#endregion

    //#region Resource locations
    public ResourceLocation location(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(modid, path);
    }

    public ResourceLocation extensionLocation(String path, String extension)
    {
        return location(path + '.' + extension);
    }

    public ResourceLocation formatLocation(String... components)
    {
        return location(joinModComponents('/', components));
    }

    public ResourceLocation formatExtensionLocation(String extension, String... components)
    {
        return extensionLocation(joinModComponents('/', components), extension);
    }
    //#endregion

    //#region Commonly used location formats
    public ResourceLocation textureLocation(String folder, String path)
    {
        return formatExtensionLocation("png", "textures", folder, path);
    }
    //#endregion

    //#region Translation objects
    public Translatable translationHolder(String key)
    {
        return Translatable.standalone(translationKey(key));
    }

    public Translatable translationHolder(String... keyComponents)
    {
        return Translatable.standalone(translationKey(keyComponents));
    }

    public String translationKey(String key)
    {
        return key.replace("{}", modid).replace("/", ".");
    }

    public String translationKey(String... keyComponents)
    {
        String key = joinModComponents('.', keyComponents);
        return key.replace("/", ".");
    }

    private String joinModComponents(char delimiter, String... components)
    {
        return joinComponents(delimiter, components).replace("{}", modid);
    }
    //#endregion

    //#region Misc resources
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> packetType(String name)
    {
        return new CustomPacketPayload.Type<>(location(name));
    }

    public <T> TransientDataComponentType<T> transientDataComponent(String name)
    {
        return new TransientDataComponentType<>(location(name));
    }
    //#endregion

    @Override
    public int hashCode()
    {
        return modid.hashCode();
    }

    @Override
    public String toString()
    {
        return modid;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        else if (o instanceof ModResources resources)
        {
            return this.modid.equals(resources.modid);
        }
        else
        {
            return false;
        }
    }
}