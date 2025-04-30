package liedge.limacore.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class LimaBasicBakedModel implements BakedModel
{
    // Base model properties
    private final boolean ambientOcclusion;
    private final boolean gui3d;
    private final boolean useBlockLight;
    private final TextureAtlasSprite particleIcon;
    private final ItemTransforms transforms;
    private final ItemOverrides overrides;
    private final boolean useCustomRenderer;
    private final @Nullable ChunkRenderTypeSet blockRenderTypes;

    protected LimaBasicBakedModel(boolean ambientOcclusion,
                                  boolean gui3d,
                                  boolean useBlockLight,
                                  TextureAtlasSprite particleIcon,
                                  ItemTransforms transforms,
                                  ItemOverrides overrides,
                                  boolean useCustomRenderer,
                                  RenderTypeGroup renderTypeGroup)
    {
        this.ambientOcclusion = ambientOcclusion;
        this.gui3d = gui3d;
        this.useBlockLight = useBlockLight;
        this.particleIcon = particleIcon;
        this.transforms = transforms;
        this.overrides = overrides;
        this.useCustomRenderer = useCustomRenderer;
        this.blockRenderTypes = !renderTypeGroup.isEmpty() ? ChunkRenderTypeSet.of(renderTypeGroup.block()) : null;
    }

    public abstract List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side);

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random)
    {
        return getQuads(state, side);
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return ambientOcclusion;
    }

    @Override
    public boolean isGui3d()
    {
        return gui3d;
    }

    @Override
    public boolean usesBlockLight()
    {
        return useBlockLight;
    }

    @Override
    public boolean isCustomRenderer()
    {
        return useCustomRenderer;
    }

    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return particleIcon;
    }

    @Override
    public ItemOverrides getOverrides()
    {
        return overrides;
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform)
    {
        transforms.getTransform(transformType).apply(applyLeftHandTransform, poseStack);
        return this;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data)
    {
        return blockRenderTypes != null ? blockRenderTypes : BakedModel.super.getRenderTypes(state, rand, data);
    }

    public static abstract class AbstractBuilder<T extends AbstractBuilder<T>> implements IModelBuilder<T>
    {
        protected final boolean ambientOcclusion;
        protected final boolean gui3d;
        protected final boolean useBlockLight;
        protected final TextureAtlasSprite particleIcon;
        protected final ItemTransforms transforms;
        protected final ItemOverrides overrides;
        protected final boolean useCustomRenderer;
        protected final RenderTypeGroup renderTypeGroup;

        protected AbstractBuilder(boolean ambientOcclusion, boolean gui3d, boolean useBlockLight, TextureAtlasSprite particleIcon, ItemTransforms transforms, ItemOverrides overrides, boolean useCustomRenderer, RenderTypeGroup renderTypeGroup)
        {
            this.ambientOcclusion = ambientOcclusion;
            this.gui3d = gui3d;
            this.useBlockLight = useBlockLight;
            this.particleIcon = particleIcon;
            this.transforms = transforms;
            this.overrides = overrides;
            this.useCustomRenderer = useCustomRenderer;
            this.renderTypeGroup = renderTypeGroup;
        }
    }
}