package liedge.limacore.client.gui;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import liedge.limacore.capability.fluid.LimaFluidUtil;
import liedge.limacore.lib.LimaColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

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

    //#region Blit Helpers
    public static void directBlit(GuiGraphics graphics, ResourceLocation atlasLocation, float x1, float y1, float x2, float y2, int zOffset, float u0, float u1, float v0, float v1)
    {
        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Matrix4f mx4 = graphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.addVertex(mx4, x1, y1, zOffset).setUv(u0, v0);
        buffer.addVertex(mx4, x1, y2, zOffset).setUv(u0, v1);
        buffer.addVertex(mx4, x2, y2, zOffset).setUv(u1, v1);
        buffer.addVertex(mx4, x2, y1, zOffset).setUv(u1, v0);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    public static void directBlit(GuiGraphics graphics, ResourceLocation atlasLocation, float x1, float y1, float x2, float y2, float u0, float u1, float v0, float v1)
    {
        directBlit(graphics, atlasLocation, x1, y1, x2, y2, 0, u0, u1, v0, v1);
    }

    public static void directBlit(GuiGraphics graphics, float x, float y, int width, int height, TextureAtlasSprite sprite)
    {
        directBlit(graphics, sprite.atlasLocation(), x, y, x + width, y + height, 0, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
    }

    public static void directColorBlit(GuiGraphics graphics, ResourceLocation atlasLocation, float x1, float y1, float x2, float y2, int zOffset, float u0, float u1, float v0, float v1, float red, float green, float blue, float alpha)
    {
        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();

        Matrix4f mx4 = graphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        buffer.addVertex(mx4, x1, y1, zOffset).setUv(u0, v0).setColor(red, green, blue, alpha);
        buffer.addVertex(mx4, x1, y2, zOffset).setUv(u0, v1).setColor(red, green, blue, alpha);
        buffer.addVertex(mx4, x2, y2, zOffset).setUv(u1, v1).setColor(red, green, blue, alpha);
        buffer.addVertex(mx4, x2, y1, zOffset).setUv(u1, v0).setColor(red, green, blue, alpha);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static void directColorBlit(GuiGraphics graphics, float x, float y, int width, int height, float red, float green, float blue, float alpha, TextureAtlasSprite sprite)
    {
        directColorBlit(graphics, sprite.atlasLocation(), x, y, x + width, y + height, 0, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), red, green, blue, alpha);
    }

    public static void directColorBlit(GuiGraphics graphics, float x, float y, int width, int height, LimaColor color, float alpha, TextureAtlasSprite sprite)
    {
        directColorBlit(graphics, x, y, width, height, color.red(), color.green(), color.blue(), alpha, sprite);
    }

    public static void partialHorizontalBlit(GuiGraphics graphics, float x, float y, int width, int height, float percentage, TextureAtlasSprite sprite)
    {
        float partialWidth = width * percentage;

        directBlit(graphics, sprite.atlasLocation(), x, y, x + partialWidth, y + height, 0, sprite.getU0(), sprite.getU(percentage), sprite.getV0(), sprite.getV1());
    }

    public static void partialVerticalBlit(GuiGraphics graphics, float x, float y, int width, int height, float percentage, TextureAtlasSprite sprite)
    {
        float partialHeight = height * percentage;
        y += height - partialHeight;

        directBlit(graphics, sprite.atlasLocation(), x, y, x + width, y + partialHeight, 0, sprite.getU0(), sprite.getU1(), sprite.getV(1f - percentage), sprite.getV1());
    }

    public static void nineSliceBlit(GuiGraphics graphics, ResourceLocation textureLocation, int cornerSize, int x, int y, int width, int height, int textureWidth, int textureHeight)
    {
        final int minSize = (cornerSize * 2) + 1;
        Preconditions.checkArgument(width >= minSize && height >= minSize, "Nine-slice dimensions too small");

        if (width == textureWidth && height == textureHeight)
        {
            graphics.blit(textureLocation, x, y, 0f, 0f, width, height, textureWidth, textureHeight);
            return;
        }

        // Draw corners
        int uOffset = textureHeight - cornerSize;
        int vOffset = textureWidth - cornerSize;
        int cornerX2 = x + width - cornerSize;
        int cornerY2 = y + height - cornerSize;
        graphics.blit(textureLocation, x, y, 0, 0, cornerSize, cornerSize, textureWidth, textureHeight);
        graphics.blit(textureLocation, cornerX2, y, uOffset, 0, cornerSize, cornerSize, textureWidth, textureHeight);
        graphics.blit(textureLocation, x, cornerY2, 0, vOffset, cornerSize, cornerSize, textureWidth, textureHeight);
        graphics.blit(textureLocation, cornerX2, cornerY2, uOffset, vOffset, cornerSize, cornerSize, textureWidth, textureHeight);

        // Draw stretched borders sampled 1-px wide/high only
        int borderWidth = width - cornerSize * 2;
        graphics.blit(textureLocation, x + cornerSize, y, borderWidth, cornerSize, cornerSize, 0, 1, cornerSize, textureWidth, textureHeight);
        graphics.blit(textureLocation, x + cornerSize, cornerY2, borderWidth, cornerSize, cornerSize, vOffset, 1, cornerSize, textureWidth, textureHeight);
        int borderHeight = height - cornerSize * 2;
        graphics.blit(textureLocation, x, y + cornerSize, cornerSize, borderHeight, 0, cornerSize, cornerSize, 1, textureWidth, textureHeight);
        graphics.blit(textureLocation, cornerX2, y + cornerSize, cornerSize, borderHeight, uOffset, cornerSize, cornerSize, 1, textureWidth, textureHeight);

        // Draw center sampled 1x1 only
        graphics.blit(textureLocation, x + cornerSize, y + cornerSize, borderWidth, borderHeight, cornerSize, cornerSize, 1, 1, textureWidth, textureHeight);
    }

    public static void nineSliceNoBottomBlit(GuiGraphics graphics, ResourceLocation textureLocation, int cornerSize, int x, int y, int width, int height, int textureWidth, int textureHeight)
    {
        Preconditions.checkArgument(width >= (cornerSize * 2) + 1 && height >= cornerSize + 1, "Nine-slice dimensions too small");

        // Draw corners
        int uOffset = textureHeight - cornerSize;
        int cornerX2 = x + width - cornerSize;
        graphics.blit(textureLocation, x, y, 0, 0, cornerSize, cornerSize, textureWidth, textureHeight);
        graphics.blit(textureLocation, cornerX2, y, uOffset, 0, cornerSize, cornerSize, textureWidth, textureHeight);

        // Draw only top and side borders. Side borders are 1x corner size longer than normal nine-slice
        int borderWidth = width - cornerSize * 2;
        graphics.blit(textureLocation, x + cornerSize, y, borderWidth, cornerSize, cornerSize, 0, 1, cornerSize, textureWidth, textureHeight);
        int borderHeight = height - cornerSize;
        graphics.blit(textureLocation, x, y + cornerSize, cornerSize, borderHeight, 0, cornerSize, cornerSize, 1, textureWidth, textureHeight);
        graphics.blit(textureLocation, cornerX2, y + cornerSize, cornerSize, borderHeight, uOffset, cornerSize, cornerSize, 1, textureWidth, textureHeight);

        // Draw center sampled 1x1 only
        graphics.blit(textureLocation, x + cornerSize, y + cornerSize, borderWidth, borderHeight, cornerSize, cornerSize, 1, 1, textureWidth, textureHeight);
    }

    public static void renderFluid(GuiGraphics graphics, FluidStack stack, int x, int y)
    {
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(stack.getFluid());
        ResourceLocation stillSpriteLoc = clientFluid.getStillTexture(stack);

        //noinspection ConstantValue
        if (stillSpriteLoc != null)
        {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillSpriteLoc);
            if (sprite.atlasLocation() != MissingTextureAtlasSprite.getLocation())
            {
                int tint = clientFluid.getTintColor(stack);
                float red = FastColor.ARGB32.red(tint) / 255f;
                float green = FastColor.ARGB32.green(tint) / 255f;
                float blue = FastColor.ARGB32.blue(tint) / 255f;
                graphics.blit(x, y, 0, 16, 16, sprite, red, green, blue, 1f);
            }
        }
    }

    public static void renderFluidWithAmount(GuiGraphics graphics, FluidStack stack, int x, int y)
    {
        if (!stack.isEmpty())
        {
            LimaGuiUtil.renderFluid(graphics, stack, x, y);

            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();

            String amountText = LimaFluidUtil.formatCompactFluidAmount(stack.getAmount());
            int textWidth = LimaGuiUtil.halfTextWidth(amountText);
            poseStack.translate(x + 16 - textWidth, y + 16 - FONT_HALF_LINE_HEIGHT, 2); // Slight z-offset
            poseStack.scale(0.5f, 0.5f, 1f);

            graphics.drawString(Minecraft.getInstance().font, amountText, 0, 0, -1, true);

            poseStack.popPose();
        }
    }
    //#endregion
}