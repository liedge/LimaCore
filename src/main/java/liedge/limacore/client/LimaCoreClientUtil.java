package liedge.limacore.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public final class LimaCoreClientUtil
{
    private LimaCoreClientUtil() {}

    public static ModelResourceLocation blockStateModelPath(BlockState state)
    {
        String variant = state.getValues().entrySet().stream().map(e -> e.getKey().getName() + "=" + e.getValue().toString()).collect(Collectors.joining(","));
        ResourceLocation id = LimaRegistryUtil.getBlockId(state.getBlock());
        return new ModelResourceLocation(id, variant);
    }

    public static ModelResourceLocation inventoryModelPath(ItemLike itemLike)
    {
        ResourceLocation id = LimaRegistryUtil.getItemId(itemLike.asItem());
        return ModelResourceLocation.inventory(id);
    }

    public static <T extends BakedModel> T getCustomBakedModel(ModelResourceLocation modelPath, Class<T> modelClass)
    {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelPath);

        if (model.equals(Minecraft.getInstance().getModelManager().getMissingModel()))
        {
            throw new NullPointerException("Baked model '" + modelPath + "' not found");
        }
        if (modelClass.isInstance(model))
        {
            return modelClass.cast(model);
        }
        else
        {
            throw new ClassCastException("Expected baked model class '" + modelClass.getSimpleName() + ", got '" + model.getClass().getSimpleName() + "' instead");
        }
    }

    public static @Nullable AbstractContainerMenu getClientPlayerMenu(int containerId)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.containerMenu.containerId == containerId)
        {
            return player.containerMenu;
        }
        else
        {
            return null;
        }
    }

    public static <T> @Nullable T getClientPlayerMenu(int containerId, Class<T> menuClass)
    {
        return LimaCoreUtil.castOrNull(menuClass, getClientPlayerMenu(containerId));
    }

    public static @Nullable BlockEntity getClientSafeBlockEntity(BlockPos blockPos)
    {
        return LimaBlockUtil.getSafeBlockEntity(Minecraft.getInstance().level, blockPos);
    }

    public static <BE> @Nullable BE getClientSafeBlockEntity(BlockPos blockPos, Class<BE> beClass)
    {
        return LimaBlockUtil.getSafeBlockEntity(Minecraft.getInstance().level, blockPos, beClass);
    }

    public static @Nullable LevelChunk getClientSafeLevelChunk(int chunkX, int chunkZ)
    {
        return LimaBlockUtil.getSafeLevelChunk(Minecraft.getInstance().level, chunkX, chunkZ);
    }

    public static @Nullable LevelChunk getClientSafeLevelChunk(ChunkPos chunkPos)
    {
        return LimaBlockUtil.getSafeLevelChunk(Minecraft.getInstance().level, chunkPos);
    }

    public static @Nullable Entity getClientEntity(int remoteEntityId)
    {
        Level level = Minecraft.getInstance().level;
        if (level != null)
        {
            return level.getEntity(remoteEntityId);
        }
        else
        {
            return null;
        }
    }

    public static <T extends Entity> @Nullable T getClientEntity(int remoteEntityId, Class<T> entityClass)
    {
        return LimaCoreUtil.castOrNull(entityClass, getClientEntity(remoteEntityId));
    }

    public static @Nullable Player getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }

    public static ItemStack getClientHeldItem(InteractionHand hand)
    {
        if (Minecraft.getInstance().player != null)
        {
            return Minecraft.getInstance().player.getItemInHand(hand);
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    public static ItemStack getClientMainHandItem()
    {
        return getClientHeldItem(InteractionHand.MAIN_HAND);
    }

    public static boolean isHoldingShiftGUI(@Nullable Level level)
    {
        if (level != null && level.isClientSide())
        {
            return Screen.hasShiftDown();
        }
        else
        {
            return false;
        }
    }

    public static void setParticleColor(Particle particle, LimaColor color)
    {
        particle.setColor(color.red(), color.green(), color.blue());
    }

    // #region Baked model rendering helpers
    public static void renderQuads(PoseStack poseStack, MultiBufferSource bufferSource, RenderType renderType, List<BakedQuad> quads, float red, float green, float blue, int packedLight)
    {
        PoseStack.Pose pose = poseStack.last();
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        for (BakedQuad quad : quads)
        {
            buffer.putBulkData(pose, quad, red, green, blue, 1f, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }
    //#endregion
}