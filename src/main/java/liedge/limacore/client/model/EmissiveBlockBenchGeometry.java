package liedge.limacore.client.model;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;

import java.util.List;
import java.util.function.Function;

public abstract class EmissiveBlockBenchGeometry<T extends EmissiveBlockBenchGeometry.EmissiveGroupData> extends BlockBenchGroupGeometry<T>
{
    private final IntSet emissiveElements;

    protected EmissiveBlockBenchGeometry(List<BlockElement> elements, List<T> groups)
    {
        super(elements, groups);
        this.emissiveElements = LimaCollectionsUtil.toIntSet(groups.stream().filter(EmissiveGroupData::emissive).flatMapToInt(o -> o.elements().intStream()));
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
    {
        TextureAtlasSprite particleIcon = spriteGetter.apply(context.getMaterial("particle"));
        ResourceLocation hint = context.getRenderTypeHint();
        RenderTypeGroup renderTypeGroup = hint != null ? context.getRenderType(hint) : RenderTypeGroup.EMPTY;

        Builder builder = createBuilder(context, renderTypeGroup, particleIcon, overrides);

        for (int i = 0; i < elements.size(); i++)
        {
            BlockElement element = elements.get(i);

            for (Direction side : element.faces.keySet())
            {
                BlockElementFace face = element.faces.get(side);
                TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.texture()));
                BakedQuad quad = BlockModel.bakeFace(element, face, sprite, side, modelState);

                boolean emissive = emissiveElements.contains(i);
                if (emissive) QuadTransformers.settingEmissivity(15).processInPlace(quad);

                if (face.cullForDirection() != null)
                {
                    builder.addCulledFace(face.cullForDirection(), quad, emissive);
                }
                else
                {
                    builder.addUnculledFace(quad, emissive);
                }
            }
        }

        return builder.build();
    }

    protected abstract Builder createBuilder(IGeometryBakingContext ctx, RenderTypeGroup renderTypeGroup, TextureAtlasSprite particleIcon, ItemOverrides overrides);

    protected abstract static class Builder extends LimaBasicBakedModel.AbstractBuilder<Builder>
    {
        protected Builder(boolean ambientOcclusion, boolean gui3d, boolean useBlockLight, TextureAtlasSprite particleIcon, ItemTransforms transforms, ItemOverrides overrides, boolean useCustomRenderer, RenderTypeGroup renderTypeGroup)
        {
            super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, useCustomRenderer, renderTypeGroup);
        }

        public abstract Builder addCulledFace(Direction facing, BakedQuad quad, boolean emissive);

        public abstract Builder addUnculledFace(BakedQuad quad, boolean emissive);

        @Deprecated
        @Override
        public Builder addCulledFace(Direction facing, BakedQuad quad)
        {
            return addCulledFace(facing, quad, false);
        }

        @Deprecated
        @Override
        public Builder addUnculledFace(BakedQuad quad)
        {
            return addUnculledFace(quad, false);
        }
    }

    public interface EmissiveGroupData
    {
        IntList elements();

        boolean emissive();
    }
}