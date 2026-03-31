package liedge.limacore.client.model;

import com.mojang.blaze3d.platform.Transparency;
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

public interface EmissiveGeometry extends ExtendedUnbakedGeometry
{
    @Nullable UnbakedGeometry getGeometry(TextureSlots textureSlots, ModelBaker baker, ModelState state, ModelDebugName debugName, ContextMap additionalProperties);

    int emissionTarget();

    RenderType cutoutEmissive(Identifier atlasLocation);

    RenderType translucentEmissive(Identifier atlasLocation);

    @Override
    default QuadCollection bake(TextureSlots textureSlots, ModelBaker baker, ModelState state, ModelDebugName debugName, ContextMap additionalProperties)
    {
        UnbakedGeometry source = getGeometry(textureSlots, baker, state, debugName, additionalProperties);
        if (source == null) return QuadCollection.EMPTY;

        QuadCollection original = source.bake(textureSlots, baker, state, debugName, additionalProperties);
        QuadCollection.Builder output = new QuadCollection.Builder();

        swapRenderTypes(original, output, null);
        for (Direction side : Direction.values())
        {
            swapRenderTypes(original, output, side);
        }

        return output.build();
    }

    default void swapRenderTypes(QuadCollection original, QuadCollection.Builder output, @Nullable Direction side)
    {
        List<BakedQuad> originalQuads = original.getQuads(side);

        for (BakedQuad quad : originalQuads)
        {
            BakedQuad.MaterialInfo material = quad.materialInfo();
            if (material.lightEmission() >= emissionTarget())
            {
                TextureAtlasSprite sprite = material.sprite();
                Transparency transparency = sprite.transparency();
                RenderType newRenderType = transparency.hasTranslucent() ?
                        translucentEmissive(sprite.atlasLocation()) :
                        cutoutEmissive(sprite.atlasLocation());

                MutableQuad mutable = new MutableQuad();
                mutable.setFrom(quad);
                mutable.setSprite(sprite, ChunkSectionLayer.byTransparency(transparency), newRenderType);

                addFace(output, side, mutable.toBakedQuad());
            }
            else
            {
                addFace(output, side, quad);
            }
        }
    }

    private void addFace(QuadCollection.Builder output, @Nullable Direction side, BakedQuad quad)
    {
        if (side == null) output.addUnculledFace(quad);
        else output.addCulledFace(side, quad);
    }
}