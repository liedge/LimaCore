package liedge.limacore.client;

import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

public class SimpleFogFluidExtension implements IClientFluidTypeExtensions
{
    public static Vector4f fogTintOf(LimaColor color)
    {
        return new Vector4f(color.red(), color.green(), color.blue(), 1f);
    }

    public static SimpleFogFluidExtension withDefaultPaths(Holder<FluidType> holder, boolean useOverlayTexture, LimaColor fluidColor, LimaColor fogColor, float fogDistance)
    {
        final Identifier id = LimaRegistryUtil.getNonNullRegistryId(holder);
        Identifier stillTexture = id.withPath(s -> String.format("block/%s_still", s));
        Identifier flowingTexture = id.withPath(s -> String.format("block/%s_flowing", s));
        Identifier overlayTexture = useOverlayTexture ? id.withPath(s -> String.format("block/%s_overlay", s)) : null;

        return new SimpleFogFluidExtension(stillTexture, flowingTexture, overlayTexture, fluidColor.argb32(), fogTintOf(fogColor), fogDistance);
    }

    // Class def
    private final Identifier stillTexture;
    private final Identifier flowingTexture;
    private final @Nullable Identifier overlayTexture;
    private final int fluidTint;
    private final Vector4f fogTint;
    private final float fogDistance;

    public SimpleFogFluidExtension(Identifier stillTexture, Identifier flowingTexture, @Nullable Identifier overlayTexture, int fluidTint, Vector4f fogTint, float fogDistance)
    {
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.fluidTint = fluidTint;
        this.fogTint = fogTint;
        this.fogDistance = fogDistance;
    }

    @Override
    public int getTintColor()
    {
        return fluidTint;
    }

    @Override
    public Identifier getStillTexture()
    {
        return stillTexture;
    }

    @Override
    public Identifier getFlowingTexture()
    {
        return flowingTexture;
    }

    @Override
    public @Nullable Identifier getOverlayTexture()
    {
        return overlayTexture;
    }

    @Override
    public Vector4f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor)
    {
        return fogTint;
    }

    @Override
    public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData)
    {
        fogData.renderDistanceStart = 0f;
        fogData.renderDistanceEnd = fogDistance;
    }
}