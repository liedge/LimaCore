package liedge.limacore.data.generation;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

import java.util.Iterator;

public abstract class LimaParticleDescriptionProvider extends ParticleDescriptionProvider
{
    private final ModResources resources;

    protected LimaParticleDescriptionProvider(PackOutput output, ModResources resources, ExistingFileHelper fileHelper)
    {
        super(output, fileHelper);
        this.resources = resources;
    }

    protected void sprite(ParticleType<?> type, String modTexture)
    {
        sprite(type, resources.location(modTexture));
    }

    protected void sprite(Holder<ParticleType<?>> typeHolder, ResourceLocation texture)
    {
        sprite(typeHolder.value(), texture);
    }

    protected void sprite(Holder<ParticleType<?>> typeHolder, String modTexture)
    {
        sprite(typeHolder.value(), modTexture);
    }

    protected void spriteSet(Holder<ParticleType<?>> typeHolder, ResourceLocation baseTexture, int numOfSprites, boolean reverse)
    {
        spriteSet(typeHolder.value(), baseTexture, numOfSprites, reverse);
    }

    protected void spriteSet(ParticleType<?> type, String modTextureName, int numOfSprites, boolean reverse)
    {
        spriteSet(type, resources.location(modTextureName), numOfSprites, reverse);
    }

    protected void spriteSet(Holder<ParticleType<?>> typeHolder, String modTextureName, int numOfSprites, boolean reverse)
    {
        spriteSet(typeHolder.value(), modTextureName, numOfSprites, reverse);
    }

    protected void spriteSet(ParticleType<?> type, ResourceLocation baseTexture, int startInclusive, int endExclusive, boolean reverse)
    {
        spriteSet(type, textures(baseTexture, startInclusive, endExclusive, reverse));
    }

    protected void spriteSet(Holder<ParticleType<?>> typeHolder, ResourceLocation baseTexture, int startInclusive, int endExclusive, boolean reverse)
    {
        spriteSet(typeHolder.value(), baseTexture, startInclusive, endExclusive, reverse);
    }

    protected void spriteSet(ParticleType<?> type, String modTextureName, int startInclusive, int endExclusive, boolean reverse)
    {
        spriteSet(type, resources.location(modTextureName), startInclusive, endExclusive, reverse);
    }

    protected void spriteSet(Holder<ParticleType<?>> typeHolder, String modTextureName, int startInclusive, int endExclusive, boolean reverse)
    {
        spriteSet(typeHolder.value(), modTextureName, startInclusive, endExclusive, reverse);
    }

    protected Iterable<ResourceLocation> textures(ResourceLocation baseTexture, int startInclusive, int endExclusive, boolean reverse)
    {
        Preconditions.checkArgument(startInclusive >= 0, "Particle sprites must start at or higher than 0");
        Preconditions.checkArgument((endExclusive - startInclusive) > 1, "Sprite end index must be greater than start index by more than 1");

        return () -> new Iterator<>()
        {
            private int counter = 0;
            private final int limit = endExclusive - startInclusive;

            @Override
            public boolean hasNext()
            {
                return counter < limit;
            }

            @Override
            public ResourceLocation next()
            {
                int n = reverse ? endExclusive - counter - 1 : startInclusive + counter;
                counter++;
                return baseTexture.withSuffix("_" + n);
            }
        };
    }
}