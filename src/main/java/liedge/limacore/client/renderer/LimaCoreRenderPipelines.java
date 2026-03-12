package liedge.limacore.client.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import liedge.limacore.LimaCore;
import net.minecraft.client.renderer.RenderPipelines;

public final class LimaCoreRenderPipelines
{
    private LimaCoreRenderPipelines() {}

    public static final RenderPipeline ENTITY_UNLIT_CUTOUT = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(LimaCore.RESOURCES.id("pipeline/entity_unlit_cutout"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1f)
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withSampler("Sampler1")
            .build();
}