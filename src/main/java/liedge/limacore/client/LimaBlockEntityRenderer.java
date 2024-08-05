package liedge.limacore.client;

import liedge.limacore.blockentity.LimaBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public abstract class LimaBlockEntityRenderer<BE extends LimaBlockEntity> implements BlockEntityRenderer<BE>
{
    protected final ItemRenderer itemRenderer;
    protected final EntityRenderDispatcher entityRenderer;

    protected LimaBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        this.itemRenderer = context.getItemRenderer();
        this.entityRenderer = context.getEntityRenderer();
    }
    
    protected double[] lerpEntityCenter(BlockPos pos, Entity entity, float partialTick)
    {
        double x = Mth.lerp(partialTick, entity.xo - pos.getX(), entity.getX() - pos.getX());
        double y = Mth.lerp(partialTick, entity.yo - pos.getY(), entity.getY() - pos.getY()) - (entity.getBoundingBox().getYsize() / 2d);
        double z = Mth.lerp(partialTick, entity.zo - pos.getZ(), entity.getZ() - pos.getZ());

        return new double[] {x, y, z};
    }
}