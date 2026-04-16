package liedge.limacore.client.model;

import com.mojang.blaze3d.platform.Transparency;
import liedge.limacore.client.LimaCoreClient;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.model.quad.MutableQuad;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public enum ItemModelPipeline implements StringRepresentable
{
    ITEM_PIPELINE("item", _ -> null, _ -> null, NeoForgeRenderTypes::getItemCutoutUnlit, NeoForgeRenderTypes::getItemTranslucentUnlit),
    ENTITY_PIPELINE("entity", RenderTypes::entityCutoutCull, RenderTypes::entityTranslucentCullItemTarget, LimaCoreRenderTypes::entityCutoutCullEmissive, LimaCoreRenderTypes::entityTranslucentEmissive);

    private final String name;
    private final Function<Identifier, @Nullable RenderType> cutout;
    private final Function<Identifier, @Nullable RenderType> translucent;
    private final Function<Identifier, @Nullable RenderType> cutoutEmissive;
    private final Function<Identifier, @Nullable RenderType> translucentEmissive;

    public static ItemModelPipeline get(String name)
    {
        return switch (name)
        {
            case "item" -> ITEM_PIPELINE;
            case "entity" -> ENTITY_PIPELINE;
            default -> {
                LimaCoreClient.CLIENT_LOGGER.warn("Unknown item model pipeline '{}', defaulting to the ITEM shader pipeline.", name);
                yield ITEM_PIPELINE;
            }
        };
    }

    ItemModelPipeline(String name, Function<Identifier, @Nullable RenderType> cutout, Function<Identifier, @Nullable RenderType> translucent, Function<Identifier, @Nullable RenderType> cutoutEmissive, Function<Identifier, @Nullable RenderType> translucentEmissive)
    {
        this.name = name;
        this.cutout = cutout;
        this.translucent = translucent;
        this.cutoutEmissive = cutoutEmissive;
        this.translucentEmissive = translucentEmissive;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public BakedQuad apply(BakedQuad quad, boolean forceEmissive)
    {
        BakedQuad.MaterialInfo material = quad.materialInfo();
        TextureAtlasSprite sprite = material.sprite();
        Transparency transparency = sprite.transparency();
        Identifier texture = sprite.atlasLocation();

        RenderType renderType;

        if (forceEmissive || !material.shade())
        {
            renderType = transparency.hasTranslucent() ? translucentEmissive.apply(texture) : cutoutEmissive.apply(texture);
        }
        else
        {
            renderType = transparency.hasTranslucent() ? translucent.apply(texture) : cutout.apply(texture);
        }

        if (renderType == null)
        {
            return quad;
        }
        else
        {
            MutableQuad mutable = new MutableQuad();
            mutable.setFrom(quad);
            mutable.setSprite(sprite, ChunkSectionLayer.byTransparency(transparency), renderType);
            return mutable.toBakedQuad();
        }
    }
}