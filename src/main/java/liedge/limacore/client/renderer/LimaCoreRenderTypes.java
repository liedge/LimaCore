package liedge.limacore.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import liedge.limacore.LimaCore;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.function.Function;

public final class LimaCoreRenderTypes
{
    private LimaCoreRenderTypes () {}

    // Named render type keys
    public static final ResourceLocation EMISSIVE_SOLID_ITEM_NAME = LimaCore.RESOURCES.location("emissive");

    public static final RenderStateShard.ShaderStateShard POSITION_TEX_COLOR_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader);

    private static final Function<ResourceLocation, RenderType> POSITION_TEX_COLOR_SOLID = Util.memoize(texture -> {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(POSITION_TEX_COLOR_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .createCompositeState(true);
        return RenderType.create("position_tex_color_solid", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 1536, false, false, state);
    });

    private static final Function<ResourceLocation, RenderType> POSITION_TEX_COLOR_TRANSLUCENT = Util.memoize(texture -> {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(POSITION_TEX_COLOR_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .createCompositeState(true);
        return RenderType.create("position_tex_color_translucent", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 1536, false, true, state);
    });

    public static RenderType positionTexColorSolid(ResourceLocation texture)
    {
        return POSITION_TEX_COLOR_SOLID.apply(texture);
    }

    public static RenderType positionTexColorTranslucent(ResourceLocation texture)
    {
        return POSITION_TEX_COLOR_TRANSLUCENT.apply(texture);
    }

    public static final RenderType ITEM_POS_TEX_COLOR_SOLID = positionTexColorSolid(InventoryMenu.BLOCK_ATLAS);
    public static final RenderType ITEM_POS_TEX_COLOR_TRANSLUCENT = positionTexColorTranslucent(InventoryMenu.BLOCK_ATLAS);
}