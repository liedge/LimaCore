package liedge.limacore.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

public class VerticalGradientRenderState extends FloatingRenderState
{
    private final int fromColor;
    private final int toColor;

    public VerticalGradientRenderState(RenderPipeline pipeline, @Nullable ScreenRectangle scissorArea, Matrix3x2f pose, float x1, float y1, float x2, float y2, int fromColor, int toColor)
    {
        super(pipeline, TextureSetup.noTexture(), scissorArea, pose, x1, y1, x2, y2);
        this.fromColor = fromColor;
        this.toColor = toColor;
    }

    @Override
    public void buildVertices(VertexConsumer buffer)
    {
        buffer.addVertexWith2DPose(pose, x1, y1).setColor(fromColor);
        buffer.addVertexWith2DPose(pose, x1, y2).setColor(toColor);
        buffer.addVertexWith2DPose(pose, x2, y2).setColor(toColor);
        buffer.addVertexWith2DPose(pose, x2, y1).setColor(fromColor);
    }
}