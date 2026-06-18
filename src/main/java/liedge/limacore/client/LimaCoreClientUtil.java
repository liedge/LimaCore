package liedge.limacore.client;

import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaCoreObjects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public final class LimaCoreClientUtil
{
    private LimaCoreClientUtil() {}

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
        return LimaCoreObjects.tryCast(menuClass, getClientPlayerMenu(containerId));
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
        return LimaCoreObjects.tryCast(entityClass, getClientEntity(remoteEntityId));
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

    public static void setQuadParticleColor(SingleQuadParticle particle, int rgb32)
    {
        particle.setColor(ARGB.redFloat(rgb32), ARGB.greenFloat(rgb32), ARGB.blueFloat(rgb32));
    }

    public static void setQuadParticleColor(SingleQuadParticle particle, LimaColor color)
    {
        particle.setColor(color.red(), color.green(), color.blue());
    }

    private static void calculateExtents(Consumer<Vector3fc> output, float x1, float y1, float z1, float x2, float y2, float z2)
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

    public static void cubeExtents(Consumer<Vector3fc> output, float x1, float y1, float z1, float x2, float y2, float z2)
    {
        calculateExtents(output, x1 * 0.0625f, y1 * 0.0625f, z1 * 0.0625f, x2 * 0.0625f, y2 * 0.0625f, z2 * 0.0625f);
    }

    public static void cubeExtents(Consumer<Vector3fc> output, Vector3fc from, Vector3fc to)
    {
        cubeExtents(output, from.x(), from.y(), from.z(), to.x(), to.y(), to.z());
    }

    public static void sizedCubeExtents(Consumer<Vector3fc> output, float x, float y, float z, float xSize, float ySize, float zSize)
    {
        cubeExtents(output, x, y, z, x + xSize, y + ySize, z + zSize);
    }
    //#endregion
}