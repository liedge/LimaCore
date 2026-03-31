package liedge.limacore.client.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import liedge.limacore.LimaCore;
import net.minecraft.client.renderer.RenderPipelines;

public final class LimaCoreRenderPipelines
{
    private LimaCoreRenderPipelines() {}

    public static final RenderPipeline.Snippet EMISSIVE_ENTITY_SNIPPET = RenderPipeline.builder(RenderPipelines.ENTITY_EMISSIVE_SNIPPET)
            .withSampler("Sampler1")
            .withShaderDefine("ALPHA_CUTOUT", 0.1f)
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withShaderDefine("EMISSIVE")
            .buildSnippet();

    public static final RenderPipeline ENTITY_CUTOUT_EMISSIVE = RenderPipeline.builder(EMISSIVE_ENTITY_SNIPPET)
            .withLocation(LimaCore.RESOURCES.id("pipeline/entity_cutout_emissive"))
            .withCull(false)
            .build();

    public static final RenderPipeline ENTITY_CUTOUT_CULL_EMISSIVE = RenderPipeline.builder(EMISSIVE_ENTITY_SNIPPET)
            .withLocation(LimaCore.RESOURCES.id("pipeline/entity_cutout_cull_emissive"))
            .withCull(true)
            .build();

    public static final RenderPipeline ENTITY_TRANSLUCENT_EMISSIVE = RenderPipeline.builder(EMISSIVE_ENTITY_SNIPPET)
            .withLocation(LimaCore.RESOURCES.id("pipeline/entity_translucent_emissive"))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .build();
}