package liedge.limacore.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.LimaCore;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.ParticleGroupRenderState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class CustomGeometryParticleGroup extends ParticleGroup<CustomGeometryParticle>
{
    public static final ParticleRenderType CUSTOM_GEOMETRY_PARTICLE = new ParticleRenderType(LimaCore.RESOURCES.id("custom_geometry").toString());

    public CustomGeometryParticleGroup(ParticleEngine engine)
    {
        super(engine);
    }

    @Override
    public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTick)
    {
        Vec3 cam = camera.position();
        ObjectList<CustomGeometryParticleEntry> entries = new ObjectArrayList<>();

        for (CustomGeometryParticle particle : particles)
        {
            CustomGeometryParticleEntry entry = particle.extractEntry(
                    particle.lerpX(cam, partialTick),
                    particle.lerpY(cam, partialTick),
                    particle.lerpZ(cam, partialTick),
                    camera,
                    partialTick);

            if (entry != null) entries.add(entry);
        }

        return new State(ObjectLists.unmodifiable(entries));
    }

    private record State(List<CustomGeometryParticleEntry> entries) implements ParticleGroupRenderState
    {
        @Override
        public void submit(SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState)
        {
            if (entries.isEmpty()) return;

            PoseStack poseStack = new PoseStack();

            for (var entry : entries)
            {
                poseStack.pushPose();

                poseStack.translate(entry.x(), entry.y(), entry.z());
                nodeCollector.submitCustomGeometry(poseStack, entry.renderType(), entry);

                poseStack.popPose();
            }
        }
    }
}