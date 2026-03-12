package liedge.limacore.client.gui;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import liedge.limacore.capability.fluid.LimaFluidUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
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

    public static int halfTextWidth(String text)
    {
        return Math.ceilDiv(Minecraft.getInstance().font.width(text), 2);
    }

    public static int halfTextWidth(FormattedText text)
    {
        return Math.ceilDiv(Minecraft.getInstance().font.width(text), 2);
    }

    public static @Nullable ScreenRectangle floatBounds(float x1, float y1, float x2, float y2, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea)
    {
        int x = Mth.floor(x1);
        int y = Mth.floor(y1);
        int width = Mth.ceil(x2) - x;
        int height = Mth.ceil(y2) - y;

        ScreenRectangle bounds = new ScreenRectangle(x, y, width, height);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
    }

    //#region Blit Helpers
    public static void floatBlit(GuiGraphics graphics, RenderPipeline pipeline, Identifier atlasLocation, float x1, float y1, float x2, float y2, float u0, float u1, float v0, float v1, int color)
    {
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(atlasLocation);
        TextureSetup textureSetup = TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler());

        graphics.nextStratum();
        GuiElementRenderState renderState = new FloatingBlitRenderState(pipeline, textureSetup, graphics.peekScissorStack(), new Matrix3x2f(graphics.pose()), x1, y1, x2, y2, u0, u1, v0, v1, color);
        graphics.submitGuiElementRenderState(renderState);
    }

    public static void floatBlit(GuiGraphics graphics, RenderPipeline pipeline, Identifier atlasLocation, float x1, float y1, float x2, float y2, float u0, float u1, float v0, float v1)
    {
        floatBlit(graphics, pipeline, atlasLocation, x1, y1, x2, y2, u0, u1, v0, v1, -1);
    }

    public static void floatBlit(GuiGraphics graphics, RenderPipeline pipeline, TextureAtlasSprite sprite, float x, float y, int width, int height, int color)
    {
        floatBlit(graphics, pipeline, sprite.atlasLocation(), x, y, x + width, y + width, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), color);
    }

    public static void partialHorizontalBlit(GuiGraphics graphics, RenderPipeline pipeline, TextureAtlasSprite sprite, float x, float y, int width, int height, float percentage, int color)
    {
        float partialWidth = width * percentage;
        floatBlit(graphics, pipeline, sprite.atlasLocation(), x, y, x + partialWidth, y + height, sprite.getU0(), sprite.getU(percentage), sprite.getV0(), sprite.getV1(), color);
    }

    public static void partialVerticalBlit(GuiGraphics graphics, RenderPipeline pipeline, TextureAtlasSprite sprite, float x, float y, int width, int height, float percentage, int color)
    {
        float partialHeight = height * percentage;
        y += height - partialHeight;
        floatBlit(graphics, pipeline, sprite.atlasLocation(), x, y, x + width, y + partialHeight, sprite.getU0(), sprite.getU1(), sprite.getV(1f - percentage), sprite.getV1(), color);
    }

    public static void nineSliceBlit(GuiGraphics graphics, RenderPipeline pipeline, Identifier textureLocation, int cornerSize, int x, int y, int width, int height, int textureWidth, int textureHeight)
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
        graphics.blit(pipeline, textureLocation, x + cornerSize, y, borderWidth, cornerSize, cornerSize, 0, 1, cornerSize, textureWidth, textureHeight);
        graphics.blit(pipeline, textureLocation, x + cornerSize, cornerY2, borderWidth, cornerSize, cornerSize, vOffset, 1, cornerSize, textureWidth, textureHeight);
        int borderHeight = height - cornerSize * 2;
        graphics.blit(pipeline, textureLocation, x, y + cornerSize, cornerSize, borderHeight, 0, cornerSize, cornerSize, 1, textureWidth, textureHeight);
        graphics.blit(pipeline, textureLocation, cornerX2, y + cornerSize, cornerSize, borderHeight, uOffset, cornerSize, cornerSize, 1, textureWidth, textureHeight);

        // Draw center sampled 1x1 only
        graphics.blit(pipeline, textureLocation, x + cornerSize, y + cornerSize, borderWidth, borderHeight, cornerSize, cornerSize, 1, 1, textureWidth, textureHeight);
    }

    public static void nineSliceNoBottomBlit(GuiGraphics graphics, RenderPipeline pipeline, Identifier textureLocation, int cornerSize, int x, int y, int width, int height, int textureWidth, int textureHeight)
    {
        Preconditions.checkArgument(width >= (cornerSize * 2) + 1 && height >= cornerSize + 1, "Nine-slice dimensions too small");

        // Draw corners
        int uOffset = textureHeight - cornerSize;
        int cornerX2 = x + width - cornerSize;
        graphics.blit(pipeline, textureLocation, x, y, 0, 0, cornerSize, cornerSize, textureWidth, textureHeight);
        graphics.blit(pipeline, textureLocation, cornerX2, y, uOffset, 0, cornerSize, cornerSize, textureWidth, textureHeight);

        // Draw only top and side borders. Side borders are 1x corner size longer than normal nine-slice
        int borderWidth = width - cornerSize * 2;
        graphics.blit(pipeline, textureLocation, x + cornerSize, y, borderWidth, cornerSize, cornerSize, 0, 1, cornerSize, textureWidth, textureHeight);
        int borderHeight = height - cornerSize;
        graphics.blit(pipeline, textureLocation, x, y + cornerSize, cornerSize, borderHeight, 0, cornerSize, cornerSize, 1, textureWidth, textureHeight);
        graphics.blit(pipeline, textureLocation, cornerX2, y + cornerSize, cornerSize, borderHeight, uOffset, cornerSize, cornerSize, 1, textureWidth, textureHeight);

        // Draw center sampled 1x1 only
        graphics.blit(pipeline, textureLocation, x + cornerSize, y + cornerSize, borderWidth, borderHeight, cornerSize, cornerSize, 1, 1, textureWidth, textureHeight);
    }

    public static void renderFluid(GuiGraphics graphics, FluidStack stack, int x, int y)
    {
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(stack.getFluid());
        Identifier stillSpriteLoc = clientFluid.getStillTexture(stack);

        //noinspection ConstantValue
        if (stillSpriteLoc != null)
        {
            TextureAtlasSprite sprite = FluidSpriteCache.getSprite(stillSpriteLoc);
            if (sprite.atlasLocation() != MissingTextureAtlasSprite.getLocation())
            {
                int tint = clientFluid.getTintColor(stack);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, 16, 16, tint);
            }
        }
    }

    public static void renderFluidWithAmount(GuiGraphics graphics, FluidStack stack, int x, int y)
    {
        if (!stack.isEmpty())
        {
            LimaGuiUtil.renderFluid(graphics, stack, x, y);

            Matrix3x2fStack matrixStack = graphics.pose();
            matrixStack.pushMatrix();

            String amountText = LimaFluidUtil.formatCompactFluidAmount(stack.getAmount());
            int textWidth = LimaGuiUtil.halfTextWidth(amountText);
            matrixStack.translate(x + 16 - textWidth, y + 16 - FONT_HALF_LINE_HEIGHT);
            matrixStack.scale(0.5f);

            graphics.drawString(Minecraft.getInstance().font, amountText, 0, 0, -1, true);

            matrixStack.popMatrix();
        }
    }

    public static void submitHorizontalGradient(GuiGraphics graphics, RenderPipeline pipeline, float x1, float y1, float x2, float y2, int leftColor, int rightColor)
    {
        GuiElementRenderState renderState = new HorizontalGradientRenderState(pipeline, graphics.peekScissorStack(), new Matrix3x2f(graphics.pose()), x1, y1, x2, y2, leftColor, rightColor);
        graphics.submitGuiElementRenderState(renderState);
    }

    public static void submitVerticalGradient(GuiGraphics graphics, RenderPipeline pipeline, float x1, float y1, float x2, float y2, int topColor, int bottomColor)
    {
        GuiElementRenderState renderState = new VerticalGradientRenderState(pipeline, graphics.peekScissorStack(), new Matrix3x2f(graphics.pose()), x1, y1, x2, y2, topColor, bottomColor);
        graphics.submitGuiElementRenderState(renderState);
    }
    //#endregion
}