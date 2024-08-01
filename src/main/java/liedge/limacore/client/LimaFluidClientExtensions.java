package liedge.limacore.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import liedge.limacore.block.LimaFluidType;
import liedge.limacore.lib.LimaColor;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class LimaFluidClientExtensions implements IClientFluidTypeExtensions
{
    public static LimaFluidClientExtensions forLimaFluidType(LimaFluidType type, boolean useTint, float fogDistance)
    {
        LimaColor fluidColor = type.getFluidColor();
        Vector3f fogColor = new Vector3f(fluidColor.red(), fluidColor.green(), fluidColor.blue());
        int tintColor = useTint ? fluidColor.rgb() : 0xffffffff;

        return new LimaFluidClientExtensions(type.getStillTexture(), type.getFlowingTexture(), type.getOverlayTexture(), tintColor, fogColor, fogDistance);
    }

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final @Nullable ResourceLocation overlayTexture;
    private final int tintColor;
    private final Vector3f fogColor;
    private final float fogDistance;

    public LimaFluidClientExtensions(ResourceLocation stillTexture, ResourceLocation flowingTexture, @Nullable ResourceLocation overlayTexture, int tintColor, Vector3f fogColor, float fogDistance)
    {
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.tintColor = tintColor;
        this.fogColor = fogColor;
        this.fogDistance = fogDistance;
    }

    @Override
    public int getTintColor()
    {
        return tintColor;
    }

    @Override
    public ResourceLocation getStillTexture()
    {
        return stillTexture;
    }

    @Override
    public ResourceLocation getFlowingTexture()
    {
        return flowingTexture;
    }

    @Override
    public @Nullable ResourceLocation getOverlayTexture()
    {
        return overlayTexture;
    }

    @Override
    public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
    {
        return fogColor;
    }

    @Override
    public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
    {
        RenderSystem.setShaderFogStart(0f);
        RenderSystem.setShaderFogEnd(fogDistance);
    }
}