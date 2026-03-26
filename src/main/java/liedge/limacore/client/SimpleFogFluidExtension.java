package liedge.limacore.client;

import liedge.limacore.lib.LimaColor;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

public record SimpleFogFluidExtension(LimaColor fogColor, float fogStart, float fogEnd) implements IClientFluidTypeExtensions
{
    public static SimpleFogFluidExtension create(LimaColor fogColor, float fogDistance)
    {
        return new SimpleFogFluidExtension(fogColor, 0f, fogDistance);
    }

    @Override
    public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor)
    {
        fluidFogColor.set(fogColor.red(), fogColor.green(), fogColor.blue());
    }

    @Override
    public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData)
    {
        fogData.renderDistanceStart = fogStart;
        fogData.renderDistanceEnd = fogEnd;
    }
}