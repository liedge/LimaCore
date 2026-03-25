package liedge.limacore.client.particle;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

public interface CustomGeometryParticleEntry extends SubmitNodeCollector.CustomGeometryRenderer
{
    float x();

    float y();

    float z();

    RenderType renderType();
}