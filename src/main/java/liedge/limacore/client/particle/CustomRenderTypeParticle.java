package liedge.limacore.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public abstract class CustomRenderTypeParticle extends Particle
{
    protected CustomRenderTypeParticle(ClientLevel level, double x, double y, double z)
    {
        super(level, x, y, z);
    }

    protected abstract void renderParticle(VertexConsumer buffer, Matrix4f mx4, Camera camera, float partialTicks);

    protected abstract RenderType getCustomRenderType();

    @Override
    public void render(VertexConsumer ignored, Camera camera, float partialTicks)
    {
        Vec3 camPos = camera.getPosition();
        float px = (float) (Mth.lerp(partialTicks, xo, x) - camPos.x);
        float py = (float) (Mth.lerp(partialTicks, yo, y) - camPos.y);
        float pz = (float) (Mth.lerp(partialTicks, zo, z) - camPos.z);

        Matrix4f mx4 = new Matrix4f();
        mx4.translate(px, py, pz);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(getCustomRenderType());
        renderParticle(buffer, mx4, camera, partialTicks);
        bufferSource.endBatch();
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.CUSTOM;
    }
}