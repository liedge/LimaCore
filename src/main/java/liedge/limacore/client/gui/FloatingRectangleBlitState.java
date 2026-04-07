package liedge.limacore.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FloatingRectangleBlitState extends FloatingGuiRenderState
{
    private final int colX1Y1;
    private final int colX1Y2;
    private final int colX2Y2;
    private final int colX2Y1;

    public FloatingRectangleBlitState(RenderPipeline pipeline, GuiGraphicsExtractor graphics, float x1, float y1, float x2, float y2, int colX1Y1, int colX1Y2, int colX2Y2, int colX2Y1)
    {
        super(pipeline, TextureSetup.noTexture(), graphics, x1, y1, x2, y2);
        this.colX1Y1 = colX1Y1;
        this.colX1Y2 = colX1Y2;
        this.colX2Y2 = colX2Y2;
        this.colX2Y1 = colX2Y1;
    }

    @Override
    public void buildVertices(VertexConsumer vertexConsumer)
    {
        LimaGuiUtil.putColoredQuad(pose, vertexConsumer, x1, y1, x2, y2, colX1Y1, colX1Y2, colX2Y2, colX2Y1);
    }
}