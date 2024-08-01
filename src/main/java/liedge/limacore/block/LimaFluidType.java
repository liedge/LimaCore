package liedge.limacore.block;

import liedge.limacore.lib.LimaColor;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public class LimaFluidType extends FluidType
{
    public static LimaFluidType createWithDefaultTextures(Properties properties, ResourceLocation id, LimaColor fluidColor, boolean useOverlay)
    {
        ResourceLocation stillTexture = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "fluid/" + id.getPath() + "_source");
        ResourceLocation flowingTexture = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "fluid/" + id.getPath() + "_flowing");
        ResourceLocation overlayTexture = useOverlay ? ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "fluid/" + id.getPath() + "_source") : null;

        return new LimaFluidType(properties, stillTexture, flowingTexture, overlayTexture, fluidColor);
    }

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final @Nullable ResourceLocation overlayTexture;
    private final LimaColor fluidColor;

    private LimaFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, @Nullable ResourceLocation overlayTexture, LimaColor fluidColor)
    {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.fluidColor = fluidColor;
    }

    public ResourceLocation getStillTexture()
    {
        return stillTexture;
    }

    public ResourceLocation getFlowingTexture()
    {
        return flowingTexture;
    }

    public @Nullable ResourceLocation getOverlayTexture()
    {
        return overlayTexture;
    }

    public LimaColor getFluidColor()
    {
        return fluidColor;
    }
}