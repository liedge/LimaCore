package liedge.limacore.client.model;

import com.mojang.blaze3d.platform.Transparency;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.neoforged.neoforge.client.model.ExtendedUnbakedGeometry;
import net.neoforged.neoforge.client.model.quad.MutableQuad;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public interface RenderRemapGeometry extends ExtendedUnbakedGeometry
{
    int NO_LIGHT_REMAP = 16;

    Either<Identifier, @Nullable UnbakedGeometry> source();

    int emissionTarget();

    @Nullable RenderType cutout(Identifier texture);

    @Nullable RenderType translucent(Identifier texture);

    @Nullable RenderType cutoutEmissive(Identifier texture);

    @Nullable RenderType translucentEmissive(Identifier texture);

    @Override
    default QuadCollection bake(TextureSlots textureSlots, ModelBaker baker, ModelState state, ModelDebugName debugName, ContextMap additionalProperties)
    {
        UnbakedGeometry originalGeometry = source().map(id -> baker.getModel(id).getTopGeometry(), Function.identity());
        if (originalGeometry == null) return QuadCollection.EMPTY;

        QuadCollection source = originalGeometry.bake(textureSlots, baker, state, debugName, additionalProperties);
        QuadCollection.Builder output = new QuadCollection.Builder();

        swapRenderTypes(source, output, null);
        for (Direction side : Direction.values())
        {
            swapRenderTypes(source, output, side);
        }

        return output.build();
    }

    private void swapRenderTypes(QuadCollection source, QuadCollection.Builder output, @Nullable Direction side)
    {
        List<BakedQuad> sourceQuads = source.getQuads(side);

        for (BakedQuad quad : sourceQuads)
        {
            BakedQuad.MaterialInfo material = quad.materialInfo();
            TextureAtlasSprite sprite = material.sprite();
            Identifier texture = sprite.atlasLocation();
            Transparency transparency = sprite.transparency();

            BakedQuad outputQuad;

            if (material.lightEmission() >= emissionTarget())
            {
                RenderType renderType = transparency.hasTranslucent() ? translucentEmissive(texture) : cutoutEmissive(texture);
                outputQuad = remapRenderType(quad, sprite, transparency, renderType);
            }
            else
            {
                RenderType renderType = transparency.hasTranslucent() ? translucent(texture) : cutout(texture);
                outputQuad = remapRenderType(quad, sprite, transparency, renderType);
            }

            addFace(output, outputQuad, side);
        }
    }

    private void addFace(QuadCollection.Builder output, BakedQuad quad, @Nullable Direction side)
    {
        if (side == null)
            output.addUnculledFace(quad);
        else
            output.addCulledFace(side, quad);
    }

    private BakedQuad remapRenderType(BakedQuad quad, TextureAtlasSprite sprite, Transparency transparency, @Nullable RenderType renderType)
    {
        if (renderType == null)
        {
            return quad;
        }

        MutableQuad mutable = new MutableQuad();
        mutable.setFrom(quad);
        mutable.setSprite(sprite, ChunkSectionLayer.byTransparency(transparency), renderType);
        return mutable.toBakedQuad();
    }
}