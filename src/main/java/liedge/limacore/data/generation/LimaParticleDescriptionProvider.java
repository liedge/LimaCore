package liedge.limacore.data.generation;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.data.ParticleDescriptionProvider;

import java.util.Iterator;

public abstract class LimaParticleDescriptionProvider extends ParticleDescriptionProvider
{
    private final ModResources resources;

    protected LimaParticleDescriptionProvider(PackOutput output, ModResources resources)
    {
        super(output);
        this.resources = resources;
    }

    protected void sprite(ParticleType<?> type, String modTexture)
    {
        spriteSet(type, resources.id(modTexture));
    }

    protected void sprite(Holder<ParticleType<?>> typeHolder, Identifier texture)
    {
        spriteSet(typeHolder.value(), texture);
    }

    protected void sprite(Holder<ParticleType<?>> typeHolder, String modTexture)
    {
        sprite(typeHolder.value(), modTexture);
    }

    protected void spriteSet(Holder<ParticleType<?>> typeHolder, Identifier baseTexture, int numOfSprites, boolean reverse)
    {
        spriteSet(typeHolder.value(), baseTexture, numOfSprites, reverse);
    }

    protected void spriteSet(ParticleType<?> type, String modTextureName, int numOfSprites, boolean reverse)
    {
        spriteSet(type, resources.id(modTextureName), numOfSprites, reverse);
    }

    protected void spriteSet(Holder<ParticleType<?>> typeHolder, String modTextureName, int numOfSprites, boolean reverse)
    {
        spriteSet(typeHolder.value(), modTextureName, numOfSprites, reverse);
    }

    protected void spriteSet(ParticleType<?> type, Identifier baseTexture, int startInclusive, int endExclusive, boolean reverse)
    {
        spriteSet(type, textures(baseTexture, startInclusive, endExclusive, reverse));
    }

    protected void spriteSet(Holder<ParticleType<?>> typeHolder, Identifier baseTexture, int startInclusive, int endExclusive, boolean reverse)
    {
        spriteSet(typeHolder.value(), baseTexture, startInclusive, endExclusive, reverse);
    }

    protected void spriteSet(ParticleType<?> type, String modTextureName, int startInclusive, int endExclusive, boolean reverse)
    {
        spriteSet(type, resources.id(modTextureName), startInclusive, endExclusive, reverse);
    }

    protected void spriteSet(Holder<ParticleType<?>> typeHolder, String modTextureName, int startInclusive, int endExclusive, boolean reverse)
    {
        spriteSet(typeHolder.value(), modTextureName, startInclusive, endExclusive, reverse);
    }

    protected Iterable<Identifier> textures(Identifier baseTexture, int startInclusive, int endExclusive, boolean reverse)
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
            public Identifier next()
            {
                int n = reverse ? endExclusive - counter - 1 : startInclusive + counter;
                counter++;
                return baseTexture.withSuffix("_" + n);
            }
        };
    }
}