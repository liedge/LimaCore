package liedge.limacore.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

public final class FloatingBlitRenderState extends FloatingRenderState
{
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;
    private final int color;

    public FloatingBlitRenderState(RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRectangle scissorArea, Matrix3x2f pose, float x1, float y1, float x2, float y2, float u0, float u1, float v0, float v1, int color)
    {
        super(pipeline, textureSetup, scissorArea, pose, x1, y1, x2, y2);
        this.u0 = u0;
        this.u1 = u1;
        this.v0 = v0;
        this.v1 = v1;
        this.color = color;
    }

    @Override
    public void buildVertices(VertexConsumer consumer)
    {
        consumer.addVertexWith2DPose(pose, x1, y1).setUv(u0, v0).setColor(color);
        consumer.addVertexWith2DPose(pose, x1, y2).setUv(u0, v1).setColor(color);
        consumer.addVertexWith2DPose(pose, x2, y2).setUv(u1, v1).setColor(color);
        consumer.addVertexWith2DPose(pose, x2, y1).setUv(u1, v0).setColor(color);
    }
}