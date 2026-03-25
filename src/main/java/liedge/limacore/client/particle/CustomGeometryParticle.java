package liedge.limacore.client.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class CustomGeometryParticle extends Particle
{
    protected CustomGeometryParticle(ClientLevel level, double x, double y, double z)
    {
        super(level, x, y, z);
    }

    @Override
    public ParticleRenderType getGroup()
    {
        return CustomGeometryParticleGroup.CUSTOM_GEOMETRY_PARTICLE;
    }

    public abstract @Nullable CustomGeometryParticleEntry extractEntry(float x, float y, float z, Camera camera, float partialTick);

    public float lerpX(Vec3 cam, float partialTick)
    {
        return (float) (Mth.lerp(partialTick, xo, x) - cam.x);
    }

    public float lerpY(Vec3 cam, float partialTick)
    {
        return (float) (Mth.lerp(partialTick, yo, y) - cam.y);
    }

    public float lerpZ(Vec3 cam, float partialTick)
    {
        return (float) (Mth.lerp(partialTick, zo, z) - cam.z);
    }
}