package liedge.limacore.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public final class LimaGuiUtil
{
    private LimaGuiUtil() {}

    public static boolean isMouseWithinXYBounds(double mouseX, double mouseY, int x1, int y1, int x2, int y2)
    {
        return mouseX >= x1 && mouseY >= y1 && mouseX < x2 && mouseY < y2;
    }

    public static boolean isMouseWithinArea(double mouseX, double mouseY, int x, int y, int width, int height)
    {
        return isMouseWithinXYBounds(mouseX, mouseY, x, y, x + width, y + height);
    }

    public static void directBlit(GuiGraphics graphics, ResourceLocation atlasLocation, float x1, float y1, float x2, float y2, float u0, float u1, float v0, float v1)
    {
        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Matrix4f mx4 = graphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.addVertex(mx4, x1, y1, 0).setUv(u0, v0);
        buffer.addVertex(mx4, x1, y2, 0).setUv(u0, v1);
        buffer.addVertex(mx4, x2, y2, 0).setUv(u1, v1);
        buffer.addVertex(mx4, x2, y1, 0).setUv(u1, v0);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    public static void directColorBlit(GuiGraphics graphics, ResourceLocation atlasLocation, float x1, float y1, float x2, float y2, float u0, float u1, float v0, float v1, float red, float green, float blue, float alpha)
    {
        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();

        Matrix4f mx4 = graphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        buffer.addVertex(mx4, x1, y1, 0).setUv(u0, v0).setColor(red, green, blue, alpha);
        buffer.addVertex(mx4, x1, y2, 0).setUv(u0, v1).setColor(red, green, blue, alpha);
        buffer.addVertex(mx4, x2, y2, 0).setUv(u1, v1).setColor(red, green, blue, alpha);
        buffer.addVertex(mx4, x2, y1, 0).setUv(u1, v0).setColor(red, green, blue, alpha);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
    }
}