package liedge.limacore.client.renderer;

import liedge.limacore.LimaCore;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public final class LimaCoreRenderTypes
{
    private LimaCoreRenderTypes () {}

    // Named render type keys
    public static final Identifier ITEM_CUTOUT_UNLIT_ID = LimaCore.RESOURCES.id("cutout_unlit");

    private static final Function<Identifier, RenderType> ENTITY_CUTOUT_UNLIT = Util.memoize(texture ->
    {
        RenderSetup.RenderSetupBuilder builder  = RenderSetup.builder(LimaCoreRenderPipelines.ENTITY_UNLIT_CUTOUT)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE);
        return RenderType.create("limacore_entity_cutout_unlit", builder.createRenderSetup());
    });

    public static RenderType entityCutoutUnlit(Identifier texture)
    {
        return ENTITY_CUTOUT_UNLIT.apply(texture);
    }
}