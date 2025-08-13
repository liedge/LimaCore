package liedge.limacore.client.model.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;

public abstract class LimaAbstractBakedModel implements IDynamicBakedModel
{
    // Base model properties
    private final boolean ambientOcclusion;
    private final boolean gui3d;
    private final boolean useBlockLight;
    private final TextureAtlasSprite particleIcon;
    private final ItemTransforms transforms;
    private final ItemOverrides overrides;
    private final boolean useCustomRenderer;

    protected LimaAbstractBakedModel(boolean ambientOcclusion,
                                     boolean gui3d,
                                     boolean useBlockLight,
                                     TextureAtlasSprite particleIcon,
                                     ItemTransforms transforms,
                                     ItemOverrides overrides,
                                     boolean useCustomRenderer)
    {
        this.ambientOcclusion = ambientOcclusion;
        this.gui3d = gui3d;
        this.useBlockLight = useBlockLight;
        this.particleIcon = particleIcon;
        this.transforms = transforms;
        this.overrides = overrides;
        this.useCustomRenderer = useCustomRenderer;
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
}