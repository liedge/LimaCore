package liedge.limacore.client.renderer;

import liedge.limacore.LimaCore;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class LimaCoreRenderTypes
{
    private LimaCoreRenderTypes () {}

    private static final Function<Identifier, RenderType> ENTITY_CUTOUT_EMISSIVE = Util.memoize(texture ->
    {
        RenderSetup.RenderSetupBuilder builder  = RenderSetup.builder(LimaCoreRenderPipelines.ENTITY_CUTOUT_EMISSIVE)
                .withTexture("Sampler0", texture)
                .useOverlay()
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE);
        return makeType("entity_cutout_emissive", builder);
    });

    private static final Function<Identifier, RenderType> ENTITY_CUTOUT_CULL_EMISSIVE = Util.memoize(texture ->
    {
        RenderSetup.RenderSetupBuilder builder = RenderSetup.builder(LimaCoreRenderPipelines.ENTITY_CUTOUT_CULL_EMISSIVE)
                .withTexture("Sampler0", texture)
                .useOverlay()
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE);
        return makeType("entity_cutout_cull_emissive", builder);
    });

    private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize((texture, affectsOutline) ->
    {
        RenderSetup.RenderSetupBuilder builder = RenderSetup.builder(LimaCoreRenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE)
                .withTexture("Sampler0", texture)
                .useOverlay()
                .affectsCrumbling()
                .setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE);
        return makeType("entity_translucent_emissive", builder);
    });

    public static RenderType entityCutoutEmissive(Identifier texture)
    {
        return ENTITY_CUTOUT_EMISSIVE.apply(texture);
    }

    public static RenderType entityCutoutCullEmissive(Identifier texture)
    {
        return ENTITY_CUTOUT_CULL_EMISSIVE.apply(texture);
    }

    public static RenderType entityTranslucentEmissive(Identifier texture, boolean affectsOutline)
    {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, affectsOutline);
    }

    public static RenderType entityTranslucentEmissive(Identifier texture)
    {
        return entityTranslucentEmissive(texture, true);
    }

    private static RenderType makeType(String name, RenderSetup.RenderSetupBuilder builder)
    {
        return RenderType.create(LimaCore.RESOURCES.modid() + "_" + name, builder.createRenderSetup());
    }
}