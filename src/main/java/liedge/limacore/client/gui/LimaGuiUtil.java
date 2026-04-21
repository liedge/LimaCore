package liedge.limacore.client.gui;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.transfer.LimaTransferUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

public final class LimaGuiUtil
{
    private LimaGuiUtil() {}

    public static final int FONT_HALF_LINE_HEIGHT = 5;

    public static boolean isMouseWithinXYBounds(double mouseX, double mouseY, int x1, int y1, int x2, int y2)
    {
        return mouseX >= x1 && mouseY >= y1 && mouseX < x2 && mouseY < y2;
    }

    public static boolean isMouseWithinArea(double mouseX, double mouseY, int x, int y, int width, int height)
    {
        return isMouseWithinXYBounds(mouseX, mouseY, x, y, x + width, y + height);
    }

    public static boolean isLeftClickOrSelection(InputWithModifiers input)
    {
        return input.input() == InputConstants.MOUSE_BUTTON_LEFT || input.isSelection();
    }

    public static int halfTextWidth(String text)
    {
        return Math.ceilDiv(Minecraft.getInstance().font.width(text), 2);
    }

    public static int halfTextWidth(FormattedText text)
    {
        return Math.ceilDiv(Minecraft.getInstance().font.width(text), 2);
    }

    public static @Nullable ScreenRectangle floatBounds(float x1, float y1, float x2, float y2, @Nullable ScreenRectangle scissorArea)
    {
        int x = Mth.floor(x1);
        int y = Mth.floor(y1);
        int width = Mth.ceil(x2) - x;
        int height = Mth.ceil(y2) - y;

        ScreenRectangle bounds = new ScreenRectangle(x, y, width, height);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
    }

    //#region Blit Helpers
    public static void floatBlit(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier atlasLocation, float x1, float y1, float x2, float y2, float u0, float u1, float v0, float v1, int color)
    {
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(atlasLocation);
        TextureSetup textureSetup = TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler());

        GuiElementRenderState renderState = new FloatingBlitRenderState(pipeline, textureSetup, graphics, x1, y1, x2, y2, u0, u1, v0, v1, color);
        graphics.submitGuiElementRenderState(renderState);
    }

    public static void floatBlit(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier atlasLocation, float x1, float y1, float x2, float y2, float u0, float u1, float v0, float v1)
    {
        floatBlit(graphics, pipeline, atlasLocation, x1, y1, x2, y2, u0, u1, v0, v1, -1);
    }

    public static void floatBlit(GuiGraphicsExtractor graphics, RenderPipeline pipeline, TextureAtlasSprite sprite, float x, float y, int width, int height, int color)
    {
        floatBlit(graphics, pipeline, sprite.atlasLocation(), x, y, x + width, y + height, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), color);
    }

    public static void partialHorizontalBlit(GuiGraphicsExtractor graphics, RenderPipeline pipeline, TextureAtlasSprite sprite, float x, float y, int width, int height, float percentage, int color)
    {
        float partialWidth = width * percentage;
        floatBlit(graphics, pipeline, sprite.atlasLocation(), x, y, x + partialWidth, y + height, sprite.getU0(), sprite.getU(percentage), sprite.getV0(), sprite.getV1(), color);
    }

    public static void partialVerticalBlit(GuiGraphicsExtractor graphics, RenderPipeline pipeline, TextureAtlasSprite sprite, float x, float y, int width, int height, float percentage, int color)
    {
        float partialHeight = height * percentage;
        y += height - partialHeight;
        floatBlit(graphics, pipeline, sprite.atlasLocation(), x, y, x + width, y + partialHeight, sprite.getU0(), sprite.getU1(), sprite.getV(1f - percentage), sprite.getV1(), color);
    }

    public static void nineSliceBlit(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier textureLocation, int cornerSize, int x, int y, int width, int height, int textureWidth, int textureHeight)
    {
        final int minSize = (cornerSize * 2) + 1;
        Preconditions.checkArgument(width >= minSize && height >= minSize, "Nine-slice dimensions too small");

        if (width == textureWidth && height == textureHeight)
        {
            graphics.blit(pipeline, textureLocation, x, y, 0f, 0f, width, height, textureWidth, textureHeight);
            return;
        }

        // Draw corners
        int uOffset = textureHeight - cornerSize;
        int vOffset = textureWidth - cornerSize;
        int cornerX2 = x + width - cornerSize;
        int cornerY2 = y + height - cornerSize;
        graphics.blit(pipeline, textureLocation, x, y, 0, 0, cornerSize, cornerSize, textureWidth, textureHeight);
        graphics.blit(pipeline, textureLocation, cornerX2, y, uOffset, 0, cornerSize, cornerSize, textureWidth, textureHeight);
        graphics.blit(pipeline, textureLocation, x, cornerY2, 0, vOffset, cornerSize, cornerSize, textureWidth, textureHeight);
        graphics.blit(pipeline, textureLocation, cornerX2, cornerY2, uOffset, vOffset, cornerSize, cornerSize, textureWidth, textureHeight);

        // Draw stretched borders sampled 1-px wide/high only
        int borderWidth = width - cornerSize * 2;
        graphics.blit(pipeline, textureLocation, x + cornerSize, y, cornerSize, 0, borderWidth, cornerSize, 1, cornerSize, textureWidth, textureHeight);
        graphics.blit(pipeline, textureLocation, x + cornerSize, cornerY2, cornerSize, vOffset, borderWidth, cornerSize, 1, cornerSize, textureWidth, textureHeight);

        int borderHeight = height - cornerSize * 2;
        graphics.blit(pipeline, textureLocation, x, y + cornerSize, 0, cornerSize, cornerSize, borderHeight, cornerSize, 1, textureWidth, textureHeight);
        graphics.blit(pipeline, textureLocation, cornerX2, y + cornerSize, uOffset, cornerSize, cornerSize, borderHeight, cornerSize, 1, textureWidth, textureHeight);

        // Draw center sampled 1x1 only
        graphics.blit(pipeline, textureLocation, x + cornerSize, y + cornerSize, cornerSize, cornerSize, borderWidth, borderHeight, 1, 1, textureWidth, textureHeight);
    }

    public static void nineSliceNoBottomBlit(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier textureLocation, int cornerSize, int x, int y, int width, int height, int textureWidth, int textureHeight)
    {
        Preconditions.checkArgument(width >= (cornerSize * 2) + 1 && height >= cornerSize + 1, "Nine-slice dimensions too small");

        // Draw corners
        int uOffset = textureHeight - cornerSize;
        int cornerX2 = x + width - cornerSize;
        graphics.blit(pipeline, textureLocation, x, y, 0, 0, cornerSize, cornerSize, textureWidth, textureHeight);
        graphics.blit(pipeline, textureLocation, cornerX2, y, uOffset, 0, cornerSize, cornerSize, textureWidth, textureHeight);

        // Draw only top and side borders. Side borders are 1x corner size longer than normal nine-slice
        int borderWidth = width - cornerSize * 2;
        graphics.blit(pipeline, textureLocation, x + cornerSize, y, cornerSize, 0, borderWidth, cornerSize, 1, cornerSize, textureWidth, textureHeight);
        int borderHeight = height - cornerSize;
        graphics.blit(pipeline, textureLocation, x, y + cornerSize, 0, cornerSize, cornerSize, borderHeight, cornerSize, 1, textureWidth, textureHeight);
        graphics.blit(pipeline, textureLocation, cornerX2, y + cornerSize, uOffset, cornerSize, cornerSize, borderHeight, cornerSize, 1, textureWidth, textureHeight);

        // Draw center sampled 1x1 only
        graphics.blit(pipeline, textureLocation, x + cornerSize, y + cornerSize, cornerSize, cornerSize, borderWidth, borderHeight, 1, 1, textureWidth, textureHeight);
    }

    public static void fluidSprite(GuiGraphicsExtractor graphics, FluidStack stack, int x, int y, boolean useFlowing)
    {
        FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(stack.getFluid().defaultFluidState());
        Material.Baked material = useFlowing ? model.flowingMaterial() : model.stillMaterial();

        FluidTintSource tintSource = model.fluidTintSource();
        int tint = tintSource != null ? tintSource.colorAsStack(stack) : -1;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, material.sprite(), x, y, 16, 16, tint);
    }

    public static void fluidSprite(GuiGraphicsExtractor graphics, FluidStack stack, int x, int y)
    {
        fluidSprite(graphics, stack, x, y, false);
    }

    public static void fluidSpriteWithAmount(GuiGraphicsExtractor graphics, FluidStack stack, int x, int y, boolean useFlowing)
    {
        if (!stack.isEmpty())
        {
            LimaGuiUtil.fluidSprite(graphics, stack, x, y, useFlowing);

            Matrix3x2fStack matrixStack = graphics.pose();
            matrixStack.pushMatrix();

            String text = LimaTransferUtil.formatCompactFluidAmount(stack.getAmount());
            int textWidth = LimaGuiUtil.halfTextWidth(text);
            matrixStack.translate(x + 16 - textWidth, y + 16 - FONT_HALF_LINE_HEIGHT);
            matrixStack.scale(0.5f);

            graphics.text(Minecraft.getInstance().font, text, 0, 0, -1, true);

            matrixStack.popMatrix();
        }
    }

    public static void fluidSpriteWithAmount(GuiGraphicsExtractor graphics, FluidStack stack, int x, int y)
    {
        fluidSpriteWithAmount(graphics, stack, x, y, false);
    }

    public static void fill(GuiGraphicsExtractor graphics, RenderPipeline pipeline, float x1, float y1, float x2, float y2, int colX1Y1, int colX1Y2, int colX2Y2, int colX2Y1)
    {
        GuiElementRenderState renderState = new FloatingRectangleBlitState(pipeline, graphics, x1, y1, x2, y2, colX1Y1, colX1Y2, colX2Y2, colX2Y1);
        graphics.submitGuiElementRenderState(renderState);
    }

    public static void fillHorizontalGradient(GuiGraphicsExtractor graphics, RenderPipeline pipeline, float x1, float y1, float x2, float y2, int leftColor, int rightColor)
    {
        fill(graphics, pipeline, x1, y1, x2, y2, leftColor, leftColor, rightColor, rightColor);
    }

    public static void fillVerticalGradient(GuiGraphicsExtractor graphics, RenderPipeline pipeline, float x1, float y1, float x2, float y2, int topColor, int bottomColor)
    {
        fill(graphics, pipeline, x1, y1, x2, y2, topColor, bottomColor, bottomColor, topColor);
    }

    public static void putColoredQuad(Matrix3x2f pose, VertexConsumer buffer, float x1, float y1, float x2, float y2, int colX1Y1, int colX1Y2, int colX2Y2, int colX2Y1)
    {
        buffer.addVertexWith2DPose(pose, x1, y1).setColor(colX1Y1);
        buffer.addVertexWith2DPose(pose, x1, y2).setColor(colX1Y2);
        buffer.addVertexWith2DPose(pose, x2, y2).setColor(colX2Y2);
        buffer.addVertexWith2DPose(pose, x2, y1).setColor(colX2Y1);
    }

    public static void putColoredQuad(Matrix3x2f pose, VertexConsumer buffer, float x1, float y1, float x2, float y2, int color)
    {
        putColoredQuad(pose, buffer, x1, y1, x2, y2, color, color, color, color);
    }
    //#endregion
}