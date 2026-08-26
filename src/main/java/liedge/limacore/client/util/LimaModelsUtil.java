package liedge.limacore.client.util;

import liedge.limacore.lib.ModResources;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import org.joml.*;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public final class LimaModelsUtil
{
    private LimaModelsUtil() { }

    // Fluid models
    public static FluidModel.Unbaked fluidModel(ModResources resources, String stillPath, String flowPath, @Nullable String overlayPath, @Nullable FluidTintSource tint)
    {
        Material stillMaterial = new Material(resources.id(stillPath));
        Material flowMaterial = new Material(resources.id(flowPath));
        Material overlayMaterial = overlayPath != null ? new Material(resources.id(overlayPath)) : null;

        return new FluidModel.Unbaked(stillMaterial, flowMaterial, overlayMaterial, tint);
    }

    public static FluidModel.Unbaked fluidModel(ModResources resources, String stillPath, String flowPath, @Nullable FluidTintSource tint)
    {
        return fluidModel(resources, stillPath, flowPath, null, tint);
    }

    // Extents stuff
    public static void cubeExtents(Consumer<Vector3fc> output, float x1, float y1, float z1, float x2, float y2, float z2)
    {
        output.accept(new Vector3f(x1, y1, z1));
        output.accept(new Vector3f(x2, y1, z1));
        output.accept(new Vector3f(x2, y1, z2));
        output.accept(new Vector3f(x1, y1, z2));

        output.accept(new Vector3f(x1, y2, z1));
        output.accept(new Vector3f(x2, y2, z1));
        output.accept(new Vector3f(x2, y2, z2));
        output.accept(new Vector3f(x1, y2, z2));
    }

    public static void cubeExtents(Consumer<Vector3fc> output, Vector3fc from, Vector3fc to)
    {
        cubeExtents(output, from.x(), from.y(), from.z(), to.x(), to.y(), to.z());
    }

    public static void cubeExtents(Consumer<Vector3fc> output, Vector3fc from, Vector3fc to, Matrix4fc transform)
    {
        cubeExtents(output, from.mulPosition(transform, new Vector3f()), to.mulPosition(transform, new Vector3f()));
    }

    public static void cubeExtents(Consumer<Vector3fc> output, Vector3fc from, Vector3fc to, Quaternionfc rotation, Vector3fc origin)
    {
        Matrix4fc transform = new Matrix4f().translate(origin).rotate(rotation);
        cubeExtents(output, from, to, transform);
    }

    public static void scaledCubeExtents(Consumer<Vector3fc> output, float x1, float y1, float z1, float x2, float y2, float z2)
    {
        cubeExtents(output, x1 * 0.0625f, y1 * 0.0625f, z1 * 0.0625f, x2 * 0.0625f, y2 * 0.0625f, z2 * 0.0625f);
    }

    public static void scaledCubeExtents(Consumer<Vector3fc> output, Vector3fc from, Vector3fc to)
    {
        scaledCubeExtents(output, from.x(), from.y(), from.z(), to.x(), to.y(), to.z());
    }

    public static void scaledSizedCubeExtents(Consumer<Vector3fc> output, float x, float y, float z, float xSize, float ySize, float zSize)
    {
        scaledCubeExtents(output, x, y, z, x + xSize, y + ySize, z + zSize);
    }
}