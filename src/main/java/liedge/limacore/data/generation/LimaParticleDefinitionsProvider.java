package liedge.limacore.data.generation;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.ModResources;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaJsonUtil;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public abstract class LimaParticleDefinitionsProvider implements DataProvider
{
    private static final ExistingFileHelper.IResourceType PARTICLE_TEXTURE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures/particle");

    private final ModResources resources;
    private final ExistingFileHelper helper;
    private final PackOutput.PathProvider pathProvider;
    private final List<ParticleTextureList> particleTextureLists = new ObjectArrayList<>();

    protected LimaParticleDefinitionsProvider(PackOutput output, ModResources resources, ExistingFileHelper helper)
    {
        this.resources = resources;
        this.helper = helper;
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "particles");
    }

    protected abstract void defineParticles();

    protected void singleSprite(ParticleType<?> type, String spriteName)
    {
        singleSprite(type, resources.location(spriteName));
    }

    protected void singleSprite(ParticleType<?> type, ResourceLocation spritePath)
    {
        particleTextureLists.add(new ParticleTextureList(particleId(type), List.of(spritePath)));
    }

    protected void orderedSpriteSet(ParticleType<?> type, ResourceLocation baseSpritePath, int startInclusive, int endExclusive, boolean reverse)
    {
        Preconditions.checkArgument(startInclusive >= 0, "Particle sprites must start at or above 0");
        Preconditions.checkArgument((endExclusive - startInclusive) > 1, "Sprite end index must be greater than start index by more than 1");

        List<Integer> list = IntStream.range(startInclusive, endExclusive).boxed().collect(LimaCollectionsUtil.toObjectArrayList());
        if (reverse) Collections.reverse(list);

        List<ResourceLocation> paths = list.stream().map(i -> ResourceLocation.fromNamespaceAndPath(baseSpritePath.getNamespace(), baseSpritePath.getPath() + "_" + i)).toList();

        particleTextureLists.add(new ParticleTextureList(particleId(type), paths));
    }

    protected void orderedSpriteSet(ParticleType<?> type, String baseSpriteName, int startInclusive, int endExclusive, boolean reverse)
    {
        orderedSpriteSet(type, resources.location(baseSpriteName), startInclusive, endExclusive, reverse);
    }

    protected void orderedSpriteSet(ParticleType<?> type, ResourceLocation baseSpritePath, int numberOfSprites, boolean reverse)
    {
        orderedSpriteSet(type, baseSpritePath, 0, numberOfSprites, reverse);
    }

    protected void orderedSpriteSet(ParticleType<?> type, String baseSpriteName, int numberOfSprites, boolean reverse)
    {
        orderedSpriteSet(type, resources.location(baseSpriteName), numberOfSprites, reverse);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output)
    {
        defineParticles();

        CompletableFuture<?>[] futures = new CompletableFuture[particleTextureLists.size()];
        for (int i = 0; i < particleTextureLists.size(); i++)
        {
            ParticleTextureList list = particleTextureLists.get(i);

            JsonArray spriteArray = list.spritePaths.stream().map(rl -> {
                if (!helper.exists(rl, PARTICLE_TEXTURE))
                {
                    throw new IllegalStateException("Particle sprite texture file " + rl + " does not exist");
                }
                return new JsonPrimitive(rl.toString());
            }).collect(LimaJsonUtil.toJsonArray());

            JsonObject json = new JsonObject();
            json.add("textures", spriteArray);

            futures[i] = DataProvider.saveStable(output, json, pathProvider.json(list.particleId));
        }

        return CompletableFuture.allOf(futures);
    }

    @Override
    public String getName()
    {
        return "Particle Definitions";
    }

    private ResourceLocation particleId(ParticleType<?> type)
    {
        return LimaRegistryUtil.getNonNullRegistryKey(type, BuiltInRegistries.PARTICLE_TYPE);
    }

    private record ParticleTextureList(ResourceLocation particleId, List<ResourceLocation> spritePaths)
    {}
}