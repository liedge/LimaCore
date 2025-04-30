package liedge.limacore.client.model;

import com.google.gson.JsonObject;
import liedge.limacore.LimaCore;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SimpleEmissiveGeometry extends BlockBenchGroupGeometry
{
    public static final ResourceLocation LOADER_ID = LimaCore.RESOURCES.location("simple_emissive");
    public static final LimaGeometryLoader<?> LOADER = new Loader();

    private SimpleEmissiveGeometry(List<BlockElement> elements, List<BlockBenchGroupData> groups)
    {
        super(elements, groups);
    }

    @Override
    protected BakedModel bakeGroups(Map<String, BakedQuadGroup> quadGroups, IGeometryBakingContext context, TextureAtlasSprite particleIcon, RenderTypeGroup renderTypeGroup, ItemOverrides overrides)
    {
        BakedQuadGroup masterGroup = BakedQuadGroup.allOf(quadGroups.values());
        return new Baked(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), particleIcon, context.getTransforms(), overrides, renderTypeGroup, masterGroup);
    }

    private static class Baked extends LimaBasicBakedModel
    {
        private final Map<Direction, List<BakedQuad>> culledFaces;
        private final List<BakedQuad> unculledFaces;
        private final List<BakedModel> renderPasses;

        private Baked(boolean ambientOcclusion,
                      boolean gui3d,
                      boolean useBlockLight,
                      TextureAtlasSprite particleIcon,
                      ItemTransforms transforms,
                      ItemOverrides overrides,
                      RenderTypeGroup renderTypeGroup,
                      BakedQuadGroup masterGroup)
        {
            super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, false, renderTypeGroup);

            this.culledFaces = masterGroup.getCulledFaces();
            this.unculledFaces = masterGroup.getUnculledFaces();

            BakedItemLayer baseLayer = new BakedItemLayer(this, masterGroup.getNonEmissiveQuads(), renderTypeGroup, false);
            BakedItemLayer emissiveLayer = new BakedItemLayer(this, masterGroup.getEmissiveQuads(), LimaCoreRenderTypes.ITEM_POS_TEX_COLOR_SOLID, LimaCoreRenderTypes.ITEM_POS_TEX_COLOR_SOLID, true);

            this.renderPasses = List.of(baseLayer, emissiveLayer);
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side)
        {
            return side == null ? unculledFaces : culledFaces.get(side);
        }

        @Override
        public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
        {
            return renderPasses;
        }
    }

    private static class Loader extends GeometryLoader
    {
        private Loader() {}

        @Override
        protected BlockBenchGroupGeometry createGeometry(JsonObject modelJson, List<BlockElement> elements, List<BlockBenchGroupData> groups)
        {
            return new SimpleEmissiveGeometry(elements, groups);
        }

        @Override
        public ResourceLocation getLoaderId()
        {
            return LOADER_ID;
        }
    }
}