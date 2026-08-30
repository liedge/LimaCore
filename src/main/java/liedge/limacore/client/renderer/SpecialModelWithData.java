package liedge.limacore.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import org.jspecify.annotations.Nullable;

public record SpecialModelWithData<T>(SpecialModelRenderer<T> renderer, @Nullable T data)
{
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor)
    {
        renderer.submit(data, poseStack, nodeCollector, lightCoords, overlayCoords, hasFoil, outlineColor);
    }
}