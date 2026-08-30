package liedge.limacore.client.util;

import liedge.limacore.client.model.LimaSpecialModelWrapper;
import liedge.limacore.client.model.TranslucentLastModel;
import liedge.limacore.client.renderer.LimaSpecialModelRenderer;
import liedge.limacore.lib.ModResources;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import org.jetbrains.annotations.Contract;
import org.joml.*;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
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

    //#region Extents stuff
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
    //#endregion

    //#region Item model helpers
    public static int resolveTint(ItemTintSource tint, ItemStack item, @Nullable ClientLevel level, @Nullable ItemOwner owner)
    {
        LivingEntity ownerEntity = owner == null ? null : owner.asLivingEntity();
        return tint.calculate(item, level, ownerEntity);
    }

    public static ModelRenderProperties resolveProperties(ModelBaker baker, Identifier source)
    {
        ResolvedModel model = baker.getModel(source);
        TextureSlots textureSlots = model.getTopTextureSlots();

        return ModelRenderProperties.fromResolvedModel(baker, model, textureSlots);
    }

    @Contract("_,null->false")
    public static boolean isFirstPersonMainHand(ItemDisplayContext displayContext, @Nullable LivingEntity entity)
    {
        if (entity == null) return false;
        HumanoidArm arm = entity.getMainArm();

        return (displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND && arm == HumanoidArm.LEFT) ||
                (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND && arm == HumanoidArm.RIGHT);
    }

    @Contract("_,null->false")
    public static boolean isFirstPersonMainHand(ItemDisplayContext displayContext, @Nullable ItemOwner owner)
    {
        if (owner == null)
            return false;
        else
            return isFirstPersonMainHand(displayContext, owner.asLivingEntity());
    }
    //#endregion

    //#region Item model factories
    public static ItemModel.Unbaked blendsLast(Identifier model, List<ItemTintSource> tints)
    {
        return new TranslucentLastModel(model, Optional.empty(), tints);
    }

    public static ItemModel.Unbaked blendsLast(Identifier model, ItemTintSource... tints)
    {
        return blendsLast(model, List.of(tints));
    }

    public static ItemModel.Unbaked blendsLast(Identifier model)
    {
        return blendsLast(model, List.of());
    }

    public static ItemModel.Unbaked composite(List<ItemModel.Unbaked> models)
    {
        return new CompositeModel.Unbaked(models, Optional.empty());
    }

    public static ItemModel.Unbaked specialModel(Identifier base, LimaSpecialModelRenderer.LimaUnbaked<?> specialModel)
    {
        return new LimaSpecialModelWrapper.Unbaked(base, Optional.empty(), specialModel);
    }
    //#endregion
}