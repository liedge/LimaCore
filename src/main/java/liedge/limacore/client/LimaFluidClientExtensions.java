package liedge.limacore.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class LimaFluidClientExtensions implements IClientFluidTypeExtensions
{
    public static Vector3f fogTintFromColor(LimaColor color)
    {
        return new Vector3f(color.red(), color.green(), color.blue());
    }

    public static LimaFluidClientExtensions create(ResourceLocation stillTexture, ResourceLocation flowingTexture, @Nullable ResourceLocation overlayTexture, @Nullable ResourceLocation renderOverlayTexture, LimaColor fluidColor, LimaColor fogColor, float fogDistance)
    {
        return new LimaFluidClientExtensions(stillTexture, flowingTexture, overlayTexture, renderOverlayTexture, fluidColor.argb32(), fogTintFromColor(fogColor), fogDistance);
    }

    public static LimaFluidClientExtensions create(ResourceLocation stillTexture, ResourceLocation flowingTexture, @Nullable ResourceLocation overlayTexture, @Nullable ResourceLocation renderOverlayTexture, LimaColor fluidAndFogColor, float fogDistance)
    {
        return create(stillTexture, flowingTexture, overlayTexture, renderOverlayTexture, fluidAndFogColor, fluidAndFogColor, fogDistance);
    }

    public static LimaFluidClientExtensions create(Holder<FluidType> holder, boolean useOverlayTexture, @Nullable ResourceLocation renderOverlayTexture, LimaColor fluidColor, LimaColor fogColor, float fogDistance)
    {
        final ResourceLocation id = LimaRegistryUtil.getNonNullRegistryId(holder);
        ResourceLocation stillTexture = id.withPath(s -> String.format("block/%s_still", s));
        ResourceLocation flowingTexture = id.withPath(s -> String.format("block/%s_flowing", s));
        ResourceLocation overlayTexture = useOverlayTexture ? id.withPath(s -> String.format("block/%s_overlay", s)) : null;

        return create(stillTexture, flowingTexture, overlayTexture, renderOverlayTexture, fluidColor, fogColor, fogDistance);
    }

    public static LimaFluidClientExtensions create(Holder<FluidType> holder, boolean useOverlayTexture, @Nullable ResourceLocation renderOverlayTexture, LimaColor fluidAndFogColor, float fogDistance)
    {
        return create(holder, useOverlayTexture, renderOverlayTexture, fluidAndFogColor, fluidAndFogColor, fogDistance);
    }

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final @Nullable ResourceLocation overlayTexture;
    private final @Nullable ResourceLocation renderOverlayTexture;
    private final int tintColor;
    private final Vector3f fogTint;
    private final float fogDistance;

    public LimaFluidClientExtensions(ResourceLocation stillTexture, ResourceLocation flowingTexture, @Nullable ResourceLocation overlayTexture, @Nullable ResourceLocation renderOverlayTexture, int tintColor, Vector3f fogTint, float fogDistance)
    {
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.renderOverlayTexture = renderOverlayTexture;
        this.tintColor = tintColor;
        this.fogTint = fogTint;
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
    public @Nullable ResourceLocation getRenderOverlayTexture(Minecraft mc)
    {
        return renderOverlayTexture;
    }

    @Override
    public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
    {
        return fogTint;
    }

    @Override
    public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
    {
        RenderSystem.setShaderFogStart(0f);
        RenderSystem.setShaderFogEnd(fogDistance);
    }
}