package liedge.limacore.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

public abstract class FloatingGuiRenderState implements GuiElementRenderState
{
    private final RenderPipeline pipeline;
    private final TextureSetup textureSetup;
    private final @Nullable ScreenRectangle scissorArea;
    private final @Nullable ScreenRectangle bounds;

    public final Matrix3x2f pose;
    public final float x1;
    public final float y1;
    public final float x2;
    public final float y2;

    public FloatingGuiRenderState(RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRectangle scissorArea, Matrix3x2f pose, float x1, float y1, float x2, float y2)
    {
        this.pipeline = pipeline;
        this.textureSetup = textureSetup;
        this.scissorArea = scissorArea;
        this.bounds = LimaGuiUtil.floatBounds(x1, y1, x2, y2, scissorArea);

        this.pose = pose;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public FloatingGuiRenderState(RenderPipeline pipeline, TextureSetup textureSetup, GuiGraphicsExtractor graphics, float x1, float y1, float x2, float y2)
    {
        this(pipeline, textureSetup, graphics.peekScissorStack(), new Matrix3x2f(graphics.pose()), x1, y1, x2, y2);
    }

    @Override
    public RenderPipeline pipeline()
    {
        return pipeline;
    }

    @Override
    public TextureSetup textureSetup()
    {
        return textureSetup;
    }

    @Override
    public @Nullable ScreenRectangle scissorArea()
    {
        return scissorArea;
    }

    @Override
    public @Nullable ScreenRectangle bounds()
    {
        return bounds;
    }
}