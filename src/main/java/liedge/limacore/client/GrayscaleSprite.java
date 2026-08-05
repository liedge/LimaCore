package liedge.limacore.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.textures.SpriteContentsConstructor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public record GrayscaleSprite(Identifier spriteId, Identifier sourceSprite, float brightness, float alpha) implements SpriteSource, SpriteContentsConstructor
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<GrayscaleSprite> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("id").forGetter(GrayscaleSprite::spriteId),
            Identifier.CODEC.fieldOf("source").forGetter(GrayscaleSprite::sourceSprite),
            Codec.floatRange(0f, 255f).optionalFieldOf("brightness", 1f).forGetter(GrayscaleSprite::brightness),
            Codec.floatRange(0f, 255f).optionalFieldOf("alpha", 1f).forGetter(GrayscaleSprite::alpha))
            .apply(i, GrayscaleSprite::new));

    public GrayscaleSprite(Identifier spriteId, Identifier sourceSprite)
    {
        this(spriteId, sourceSprite, 1f, 1f);
    }

    @Override
    public void run(ResourceManager manager, Output output)
    {
        Identifier fullPath = TEXTURE_ID_CONVERTER.idToFile(sourceSprite);
        Optional<Resource> resource = manager.getResource(fullPath);
        if (resource.isPresent())
        {
            output.add(spriteId, loader -> loader.loadSprite(sourceSprite, resource.get(), this));
        }
        else
        {
            LOGGER.warn("Missing source sprite {}", sourceSprite);
        }
    }

    @Override
    public MapCodec<? extends SpriteSource> codec()
    {
        return CODEC;
    }

    @Override
    public @Nullable SpriteContents create(Identifier id, FrameSize frameSize, NativeImage sourceImage, Optional<AnimationMetadataSection> animationMetadata, List<MetadataSectionType.WithValue<?>> additionalMetadata, Optional<TextureMetadataSection> textureMetadata)
    {
        try (sourceImage)
        {
            NativeImage newImage = sourceImage.mappedCopy(pixel ->
            {
                int gray = ARGB.greyscale(pixel);
                if (brightness == 1f && alpha == 1f) return gray;

                int newAlpha = mulRGBComponent(ARGB.alpha(gray), alpha);
                int newComponent = mulRGBComponent(ARGB.red(gray), brightness);

                return ARGB.color(newAlpha, newComponent, newComponent, newComponent);
            });

            return new SpriteContents(spriteId, frameSize, newImage, animationMetadata, additionalMetadata, textureMetadata);
        }
        catch (IllegalArgumentException ex)
        {
            LOGGER.error("Unbale to apply grayscale filter to {}", id, ex);
            return null;
        }
    }

    private int mulRGBComponent(int component, float multiplier)
    {
        return Math.clamp(Math.round(component * multiplier), 0, 255);
    }
}